package cn.moon.live.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.moon.live.I;
import cn.moon.live.LiveHelper;
import cn.moon.live.R;
import cn.moon.live.data.model.IUserModel;
import cn.moon.live.data.model.OnCompleteListener;
import cn.moon.live.data.model.UserModel;
import cn.moon.live.utils.CommonUtils;
import cn.moon.live.utils.L;
import cn.moon.live.utils.MD5;
import cn.moon.live.utils.MFGT;
import cn.moon.live.utils.Result;
import cn.moon.live.utils.ResultUtils;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";
    @BindView(R.id.email)
    EditText metUsername;
    @BindView(R.id.password)
    EditText metPassword;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nickName)
    EditText metUserNick;
    @BindView(R.id.confirmPassword)
    EditText metConfirmPassword;

    ProgressDialog pd;
    String username, nickName, password;
    IUserModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mModel = new UserModel();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.register)
    public void registerAppServer() {
        if (checkInput()) {
            showDialog();
            mModel.register(RegisterActivity.this, username, nickName, MD5.getMessageDigest(password),
                    new OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            boolean success = false;
                            if (s != null) {
                                Result result = ResultUtils.getResultFromJson(s, String.class);
                                if (result != null) {
                                    if (result.isRetMsg()) {
                                        success = true;

                                        registerEMServer();

                                    } else if (result.getRetCode() == I.MSG_REGISTER_USERNAME_EXISTS) {
                                        CommonUtils.showShortToast(R.string.User_already_exists);
                                    } else {
                                        CommonUtils.showShortToast(R.string.Registration_failed);
                                    }
                                }
                                if (!success) {
                                    pd.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onError(String error) {
                            pd.dismiss();
                            CommonUtils.showShortToast(R.string.Registration_failed);
                        }
                    });
        }
    }

    private void registerEMServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(username, MD5.getMessageDigest(password));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            showToast("注册成功");
                            LiveHelper.getInstance().setCurrentUserName(username);
                            MFGT.gotoLogin(RegisterActivity.this);

                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    unRegister();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            showLongToast("注册失败：" + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    private boolean checkInput() {
        username = metUsername.getText().toString().trim();
        nickName = metUserNick.getText().toString().trim();
        password = metPassword.getText().toString().trim();
        String confirm_pwd = metConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            metUsername.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(nickName)) {
            Toast.makeText(this, getResources().getString(R.string.User_nick_cannot_be_empty), Toast.LENGTH_SHORT).show();
            metUserNick.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            metPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            metConfirmPassword.requestFocus();
            return false;
        } else if (!password.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private void showDialog() {
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("正在注册...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }
    private void unRegister() {
        mModel.unRegister(RegisterActivity.this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                L.e(TAG,"result="+result);
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}

package cn.moon.live.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.widget.EaseImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.moon.live.R;

/**
 * Created by wei on 2016/6/7.
 */
@RemoteViews.RemoteView
public class LiveLeftGiftView extends RelativeLayout {
    @BindView(R.id.avatar)
    EaseImageView avatar;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.give_gift_image)
    ImageView giftImage;

    public LiveLeftGiftView(Context context) {
        super(context);
        init(context, null);
    }

    public LiveLeftGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public LiveLeftGiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.widget_left_gift, this);
        ButterKnife.bind(this);
    }

    public void setName(String name){
        this.name.setText(name);
    }

    public void setAvatar(String avatar){
        Glide.with(getContext()).load(avatar).into(this.avatar);
    }

    public ImageView getGiftImageView(){
        return giftImage;
    }
}

package cn.moon.live.ui.activity;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.moon.live.I;
import cn.moon.live.LiveHelper;
import cn.moon.live.R;
import cn.moon.live.data.model.Gift;

/**
 * Created by Moon on 2017/4/19.
 */

public class GiftListDialog extends DialogFragment {
    List<Gift> giftList = new ArrayList<>();
    GiftAdapter mGiftAdapter;
    Unbinder bind;


    GridLayoutManager mGridLayoutManager;
    @BindView(R.id.rv_gift)
    RecyclerView mRvGift;
    @BindView(R.id.tv_my_bill)
    TextView mTvMyBill;
    @BindView(R.id.tv_recharge)
    TextView mTvRecharge;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift, container, false);
        bind = ButterKnife.bind(this, view);
        initView();
        customDialog();
        return view;
    }

    public static GiftListDialog newInstance() {
        GiftListDialog dialog = new GiftListDialog();
        return dialog;
    }

    private void initView() {
        mGridLayoutManager = new GridLayoutManager(getContext(), I.GIFT_COLUMN_COUNT);
        mRvGift.setLayoutManager(mGridLayoutManager);
        mRvGift.setHasFixedSize(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        giftList = LiveHelper.getInstance().getGiftList();
        initData();
    }

    private void initData() {
        if (giftList.size() > 0) {
            if (mGiftAdapter == null) {
                mGiftAdapter = new GiftAdapter(getContext(), giftList);
                mRvGift.setAdapter(mGiftAdapter);
            } else {
                mGiftAdapter.notifyDataSetChanged();
            }
        }
    }


    private void customDialog() {
        getDialog().setCanceledOnTouchOutside(true);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
    }

    private View.OnClickListener mOnClickListener;

    public void setGiftClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {
        Context mContext;
        List<Gift> mList;

        public GiftAdapter(Context context, List<Gift> list) {
            mContext = context;
            mList = list;
        }

        @Override
        public GiftAdapter.GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mContext, R.layout.gift_item, null);
            GiftViewHolder layout = new GiftViewHolder(view);
            return layout;
        }

        @Override
        public void onBindViewHolder(GiftAdapter.GiftViewHolder holder, int position) {
            holder.bind(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.size() : 0;
        }

        class GiftViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.ivGiftThumb)
            ImageView mIvGiftThumb;
            @BindView(R.id.tvGiftName)
            TextView mTvGiftName;
            @BindView(R.id.tvGiftPrice)
            TextView mTvGiftPrice;
            @BindView(R.id.layout_gift)
            LinearLayout mLayoutGift;

            GiftViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bind(Gift gift) {
                EaseUserUtils.setAvatar(mContext, gift.getGurl(), mIvGiftThumb);
                mTvGiftName.setText(gift.getGname());
                mTvGiftPrice.setText(String.valueOf(gift.getGprice()));
                itemView.setTag(gift.getId());
                itemView.setOnClickListener(mOnClickListener);
            }
        }
    }
}


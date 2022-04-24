package com.kayu.business_car_owner.ui.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kayu.business_car_owner.R;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;

import pl.droidsonroids.gif.GifImageView;


/**
 * Created by solo on 2018/1/31.
 * SmartRefreshLayout 的自定义下拉刷新 Header
 */
@SuppressLint("RestrictedApi")
public class CustomRefreshHeader extends LinearLayout implements RefreshHeader {

    private GifImageView mImage;
//    private AnimationDrawable pullDownAnim;
//    private AnimationDrawable refreshingAnim;

    private boolean hasSetPullDownAnim = false;

    public CustomRefreshHeader(Context context) {
        this(context, null, 0);
    }

    public CustomRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.widget_custom_refresh_header, this);
        mImage = (GifImageView) view.findViewById(R.id.iv_refresh_header);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {

    }

    /**
     * 状态改变时调用。在这里切换第三阶段的动画卖萌小人
     * @param refreshLayout
     * @param oldState
     * @param newState
     */
    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case PullDownToRefresh: //下拉刷新开始。正在下拉还没松手时调用
                //每次重新下拉时，将图片资源重置为小人的大脑袋
//                mImage.setImageResource(R.drawable.commonui_pull_image);
                break;
            case Refreshing: //正在刷新。只调用一次
                //状态切换为正在刷新状态时，设置图片资源为小人卖萌的动画并开始执行
//                mImage.setImageResource(R.drawable.loading_gif);
//                refreshingAnim = (AnimationDrawable) mImage.getDrawable();
//                refreshingAnim.start();
                break;
            case ReleaseToRefresh:

                break;
        }
    }


    /**
     * 动画结束后调用
     */
    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        // 结束动画
//        if (pullDownAnim != null && pullDownAnim.isRunning()) {
//            pullDownAnim.stop();
//        }
//        if (refreshingAnim != null && refreshingAnim.isRunning()) {
//            refreshingAnim.stop();
//        }
        //重置状态
        hasSetPullDownAnim = false;
        return 0;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }


}

package com.southernbox.swipemenulayout.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.southernbox.swipemenulayout.adapter.MainAdapter;

/**
 * Created by nanquan.lin on 2016/11/30 0030.
 * 自定义RecyclerView，首页侧滑时，列表不可上下滑
 */

public class MainRecyclerView extends RecyclerView {

    public MainRecyclerView(Context context) {
        this(context, null);
    }

    public MainRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    float mDownX;
    float mDownY;
    boolean isScrollHorizontal;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isScrollHorizontal = false;
                mDownX = e.getRawX();
                mDownY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //横向滑动时不拦截事件
                float deltaX = Math.abs(e.getRawX() - mDownX);
                float deltaY = Math.abs(e.getRawY() - mDownY);
                if (isScrollHorizontal || deltaX / deltaY > 1) {
                    isScrollHorizontal = true;
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (MainAdapter.mOpenItems.size() > 0) {
            MainAdapter.closeAll();
            return false;
        }
        return super.onTouchEvent(e);
    }
}

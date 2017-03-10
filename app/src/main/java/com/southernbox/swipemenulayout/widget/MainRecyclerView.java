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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = e.getRawX();
                mDownY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //竖向滑动时拦截事件
                float deltaX = e.getRawX() - mDownX;
                float deltaY = e.getRawY() - mDownY;
                if (deltaY != 0.0 &&
                        Math.abs(deltaX / deltaY) < 1) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (MainAdapter.mOpenItems.size() > 0) {
            return false;
        }
        return super.onTouchEvent(e);
    }
}

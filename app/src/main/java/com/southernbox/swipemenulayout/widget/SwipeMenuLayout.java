package com.southernbox.swipemenulayout.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.southernbox.swipemenulayout.adapter.MainAdapter;
import com.southernbox.swipemenulayout.util.DisplayUtil;

/**
 * Created by SouthernBox on 2016/11/21 0021.
 * 侧滑菜单控件
 */

public class SwipeMenuLayout extends FrameLayout {

    private ViewDragHelper mDragHelper;
    private ViewGroup mLeftContent;
    private ViewGroup mMainContent;

    /**
     * 屏幕宽高
     */
    private int mWidth;
    private int mHeight;

    /**
     * 左侧拉出来的宽度
     */
    private int mRange = DisplayUtil.getPx(getContext(), 305);

    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mMainContent) {
                if (left < 0) {
                    left = 0;
                } else if (left > mRange) {
                    left = mRange;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mLeftContent) {
                mLeftContent.layout(0, 0, mWidth, mHeight);
                if (mMainContent.getLeft() + dx <= mRange && mMainContent.getLeft() + dx >= 0) {
                    mMainContent.offsetLeftAndRight(dx);
                }
            }
            mState = updateState(left);
            invalidate();
        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int sensitivity = 300; //控制惯性滑动灵敏度
            if (xvel > sensitivity) {
                //手指正在向右滑
                open();
            } else if (releasedChild == mMainContent) {
                //点击的是主页面
                close();
            } else if (xvel > -sensitivity &&
                    mMainContent.getLeft() > mRange * 0.5f &&
                    mMainContent.getLeft() <= mRange) {
                open();
            } else {
                close();
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }
    };

    /**
     * 定义当前状态  默认是关闭状态
     */
    private State mState = State.CLOSE;

    /**
     * 状态枚举
     */
    private enum State {
        CLOSE, OPEN, DRAGGING
    }

    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, mCallback);
    }

    private float mDownX;
    private float mDownY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mState == State.CLOSE) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mDownX = ev.getRawX();
                    mDownY = ev.getRawY();
                }
                break;
                case MotionEvent.ACTION_MOVE: {
                    float deltaX = ev.getRawX() - mDownX;
                    float deltaY = ev.getRawY() - mDownY;

                    //向右滑动且列表没有展开项且横向滑动距离比竖向滑动距离大，则拦截
                    if (deltaX > 0 &&
                            MainAdapter.mOpenItems.size() == 0 &&
                            Math.abs(deltaY / deltaX) < 1) {
                        return true;
                    }

                    //如果是向左滑，且竖直滑动距离大于横向滑动距离，不拦截
                    //MainPage打开的item个数大于0，不拦截
                    if ((deltaX < 0 && Math.abs(deltaY / deltaX) > 1) ||
                            MainAdapter.mOpenItems.size() > 0) {
                        return false;
                    }
                }
                break;
            }
        } else if (mState == State.OPEN) {
            //完全展开时并且点到主页面，拦截并关闭菜单
            if (mMainContent.getLeft() <= mRange && ev.getRawX() > mRange) {
                return true;
            }
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = ev.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //如果是向右滑，不拦截
                    float deltaX = ev.getRawX() - mDownX;
                    if (deltaX > 0) {
                        return false;
                    }
                    break;
            }
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (MainAdapter.mOpenItems.size() > 0) {
            MainAdapter.closeAll();
            return true;
        }
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLeftContent = (ViewGroup) getChildAt(0);
        mMainContent = (ViewGroup) getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mState == State.OPEN) {
            open(false);
        }
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 更新当前状态
     *
     * @param left 主页最左边坐标
     * @return 当前状态
     */
    private State updateState(int left) {
        if (left <= 0) {
            return State.CLOSE;
        } else if (left >= mRange) {
            return State.OPEN;
        } else {
            return State.DRAGGING;
        }
    }

    public void open() {
        open(true);
    }

    public void open(boolean isSmooth) {
        if (isSmooth) {
            mDragHelper.smoothSlideViewTo(mMainContent, mRange, 0);
            invalidate();
        } else {
            mMainContent.layout(mRange, 0, mRange + mWidth, mHeight);
        }
    }

    public void close() {
        close(true);
    }

    public void close(boolean isSmooth) {
        if (isSmooth) {
            mDragHelper.smoothSlideViewTo(mMainContent, 0, 0);
            invalidate();
        } else {
            mMainContent.layout(0, 0, mWidth, mHeight);
        }
    }
}

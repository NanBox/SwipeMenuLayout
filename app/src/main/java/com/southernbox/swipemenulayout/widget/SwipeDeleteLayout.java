package com.southernbox.swipemenulayout.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.southernbox.swipemenulayout.adapter.MainAdapter;

/**
 * Created by nanquan.lin on 2016/8/9 0009.
 * 首页侧滑删除控件
 */
public class SwipeDeleteLayout extends FrameLayout {

    private ViewDragHelper mDragHelper;
    private View mBackView;
    private View mFrontView;
    private int mWidth;
    private int mHeight;
    private int mBackWidth;

    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        //限定移动范围
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mFrontView) {
                if (left < -mBackWidth) {
                    left = -mBackWidth;
                } else if (left > 0) {
                    left = 0;
                }
            } else if (child == mBackView) {
                if (left < mWidth - mBackWidth) {
                    left = mWidth - mBackWidth;
                } else if (left > mWidth) {
                    left = mWidth;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mFrontView) {
                mBackView.offsetLeftAndRight(dx);
            } else if (changedView == mBackView) {
                mFrontView.offsetLeftAndRight(dx);
            }
            dispatchDragState(mFrontView.getLeft());
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            mOnDragStateChangeListener.onViewReleased(SwipeDeleteLayout.this);
            if (xvel == 0.0f && mFrontView.getLeft() < -mBackWidth * 0.5f) {
                open();
            } else if (xvel < 0) {
                open();
            } else {
                close();
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mBackWidth;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mHeight;
        }
    };

    private enum State {
        CLOSE, OPEN, DRAGGING
    }

    private State mState = State.CLOSE;

    public interface OnDragStateChangeListener {

        void onClose(SwipeDeleteLayout layout);

        void onOpen(SwipeDeleteLayout layout);

        void onDragging();

        void onStartOpen(SwipeDeleteLayout layout);

        void onViewReleased(SwipeDeleteLayout layout);
    }

    private OnDragStateChangeListener mOnDragStateChangeListener;

    public void setOnDragStateChangeListener(OnDragStateChangeListener onDragStateChangeListener) {
        mOnDragStateChangeListener = onDragStateChangeListener;
    }

    public SwipeDeleteLayout(Context context) {
        this(context, null);
    }

    public SwipeDeleteLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeDeleteLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, mCallback);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mFrontView.layout(0, 0, mWidth, mHeight);
        mBackView.layout(mWidth, 0, mBackWidth + mWidth, mHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    private boolean isOpen;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //存在已展开的控件且当前控件为关闭状态，则将所有展开控件关闭
        if (MainAdapter.mOpenItems.size() > 0 && mState == State.CLOSE) {
            return false;
        }

        //展开状态下，点击左侧部分将其关闭
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                isOpen = mState == State.OPEN;
            }
            case MotionEvent.ACTION_UP: {
                if (isOpen && mState == State.OPEN && event.getRawX() <= mWidth - mBackWidth) {
                    close();
                    return true;
                }
            }
        }

        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBackView = getChildAt(0);
        mFrontView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mBackWidth = mBackView.getMeasuredWidth();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    protected void dispatchDragState(int left) {
        State preState = mState;
        mState = updateState(left);
        if (mOnDragStateChangeListener == null) {
            return;
        }
        if (mState != preState) {
            if (mState == State.OPEN) {
                mOnDragStateChangeListener.onOpen(this);
            } else if (mState == State.CLOSE) {
                mOnDragStateChangeListener.onClose(this);
            } else if (mState == State.DRAGGING) {
                if (preState == State.CLOSE) {
                    mOnDragStateChangeListener.onStartOpen(this);
                }
            }
        } else {
            mOnDragStateChangeListener.onDragging();
        }
    }

    private State updateState(int left) {
        if (left == -mBackWidth) {
            return State.OPEN;
        } else if (left == 0) {
            return State.CLOSE;
        } else {
            return State.DRAGGING;
        }
    }

    public void open() {
        open(true);
    }

    public void open(boolean isSmooth) {
        if (isSmooth) {
            mDragHelper.smoothSlideViewTo(mFrontView, -mBackWidth, 0);
            invalidate();
        } else {
            layoutContent(State.OPEN);
        }
    }

    public void close() {
        close(true);
    }

    public void close(boolean isSmooth) {
        mDragHelper.cancel();
        if (isSmooth) {
            mDragHelper.smoothSlideViewTo(mFrontView, 0, 0);
            invalidate();
        } else {
            layoutContent(State.CLOSE);
        }
    }

    private void layoutContent(State state) {
        Rect frontRect = computeFrontRect(state);
        Rect backRect = computeBackRectFromFront(frontRect);
        mFrontView.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);
        mBackView.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);
    }

    private Rect computeBackRectFromFront(Rect frontRect) {
        return new Rect(frontRect.right, frontRect.top, frontRect.right + mBackWidth,
                frontRect.bottom);
    }

    private Rect computeFrontRect(State state) {
        int left = 0;
        if (state == State.OPEN) {
            left = -mBackWidth;
        }
        return new Rect(left, 0, left + mWidth, mHeight);
    }

}

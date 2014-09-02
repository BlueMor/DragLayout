package com.bluemor.reddotface.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class DragLayout extends FrameLayout {

    private GestureDetector gestureDetector;
    private DragListener dragListener;
    private ViewDragHelper dragHelper;

    private int width;
    private int height;

    private RelativeLayout vg_left;
    private MyRelativeLayout vg_main;

    private Status status = Status.Close;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("deprecation")
    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetector = new GestureDetector(new YScrollDetector());
        dragHelper = ViewDragHelper.create(this, dragHelperCallback);
    }

    class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            return Math.abs(distanceY) <= Math.abs(distanceX);
        }
    }

    private ViewDragHelper.Callback dragHelperCallback = new ViewDragHelper.Callback() {

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (vg_main.getLeft() + dx < 0) {
                return 0;
            } else {
                return left;
            }
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return width;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel > 0) {
                open();
            } else if (xvel < 0) {
                close();
            } else if (releasedChild == vg_main
                    && vg_main.getLeft() > width / 3) {
                open();
            } else if (releasedChild == vg_left
                    && vg_main.getLeft() > width * 2 / 3) {
                open();
            } else {
                close();
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                int dx, int dy) {
            int mainLeft = vg_main.getLeft() + dx;
            if (mainLeft < 0) {
                mainLeft = 0;
            }
            if (mainLeft > width) {
                mainLeft = width;
            }
            if (changedView == vg_left) {
                vg_left.layout(0, 0, width, height);
                vg_main.layout(mainLeft, 0, mainLeft + width, height);
            }
            dispatchDragEvent(mainLeft);
        }
    };

    public interface DragListener {
        public void onOpen();

        public void onClose();

        public void onDrag(float percent);
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        vg_left = (RelativeLayout) getChildAt(0);
        vg_main = (MyRelativeLayout) getChildAt(1);
        vg_main.setDragLayout(this);
    }

    public ViewGroup getVg_main() {
        return vg_main;
    }

    public ViewGroup getVg_left() {
        return vg_left;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = vg_left.getMeasuredWidth();
        height = vg_left.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        if (getStatus() == Status.Drag) {
            return;
        }
        super.onLayout(changed, left, top, right, bottom);
        if (status == Status.Open) {
            open(false);
        } else {
            close(false);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev)
                && gestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        try {
            dragHelper.processTouchEvent(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void dispatchDragEvent(int mainLeft) {
        if (dragListener == null) {
            return;
        }
        float percent = (float) mainLeft / (float) width;
        dragListener.onDrag(percent);
        Status lastStatus = status;
        if (lastStatus != getStatus() && status == Status.Close) {
            vg_left.setEnabled(false);
            dragListener.onClose();
        } else if (lastStatus != getStatus() && status == Status.Open) {
            vg_left.setEnabled(true);
            dragListener.onOpen();
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public enum Status {
        Drag, Open, Close
    }

    public Status getStatus() {
        int mainLeft = vg_main.getLeft();
        if (mainLeft == 0) {
            status = Status.Close;
        } else if (mainLeft == width) {
            status = Status.Open;
        } else {
            status = Status.Drag;
        }
        return status;
    }

    public void open() {
        open(true);
    }

    public void open(boolean animate) {
        if (animate) {
            dragHelper.smoothSlideViewTo(vg_main, width, 0);
        } else {
            vg_main.layout(width, 0, width * 2, height);
            dispatchDragEvent(width);
        }
        invalidate();
    }

    public void close() {
        close(true);
    }

    public void close(boolean animate) {
        if (animate) {
            dragHelper.smoothSlideViewTo(vg_main, 0, 0);
        } else {
            vg_main.layout(0, 0, width, height);
            dispatchDragEvent(0);
        }
        invalidate();
    }

}

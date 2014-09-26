package com.bluemor.reddotface.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bluemor.reddotface.R;
import com.nineoldandroids.view.ViewHelper;

public class DragLayout extends FrameLayout {

    private boolean isShowShadow = true;

    private GestureDetectorCompat gestureDetector;
    private ViewDragHelper dragHelper;
    private DragListener dragListener;
    private int range;
    private int width;
    private int height;
    private int mainLeft;
    private Context context;
    private ImageView iv_shadow;
    private RelativeLayout vg_left;
    private MyRelativeLayout vg_main;
    private Status status = Status.Close;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetector = new GestureDetectorCompat(context, new YScrollDetector());
        dragHelper = ViewDragHelper.create(this, dragHelperCallback);
    }

    class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
            return Math.abs(dy) <= Math.abs(dx);
        }
    }

    private ViewDragHelper.Callback dragHelperCallback = new ViewDragHelper.Callback() {

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (mainLeft + dx < 0) {
                return 0;
            } else if (mainLeft + dx > range) {
                return range;
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
            } else if (releasedChild == vg_main && mainLeft > range * 0.3) {
                open();
            } else if (releasedChild == vg_left && mainLeft > range * 0.7) {
                open();
            } else {
                close();
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                int dx, int dy) {
            if (changedView == vg_main) {
                mainLeft = left;
            } else {
                mainLeft = mainLeft + left;
            }
            if (mainLeft < 0) {
                mainLeft = 0;
            } else if (mainLeft > range) {
                mainLeft = range;
            }

            if (isShowShadow) {
                iv_shadow.layout(mainLeft, 0, mainLeft + width, height);
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
        if (isShowShadow) {
            iv_shadow = new ImageView(context);
            iv_shadow.setImageResource(R.drawable.shadow);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(iv_shadow, 1, lp);
        }
        vg_left = (RelativeLayout) getChildAt(0);
        vg_main = (MyRelativeLayout) getChildAt(isShowShadow ? 2 : 1);
        vg_main.setDragLayout(this);
        vg_left.setClickable(true);
        vg_main.setClickable(true);
    }

    public ViewGroup getVg_main() {
        return vg_main;
    }

    public ViewGroup getVg_left() {
        return vg_left;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = vg_left.getMeasuredWidth();
        height = vg_left.getMeasuredHeight();
        range = (int) (width * 0.6f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        vg_left.layout(0, 0, width, height);
        vg_main.layout(mainLeft, 0, mainLeft + width, height);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
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
        float percent = mainLeft / (float) range;
        animateView(percent);
        dragListener.onDrag(percent);
        Status lastStatus = status;
        if (lastStatus != getStatus() && status == Status.Close) {
            dragListener.onClose();
        } else if (lastStatus != getStatus() && status == Status.Open) {
            dragListener.onOpen();
        }
    }

    private void animateView(float percent) {
        float f1 = 1 - percent * 0.3f;
        ViewHelper.setScaleX(vg_main, f1);
        ViewHelper.setScaleY(vg_main, f1);
        ViewHelper.setTranslationX(vg_left, -vg_left.getWidth() / 2.3f + vg_left.getWidth() / 2.3f * percent);
        ViewHelper.setScaleX(vg_left, 0.5f + 0.5f * percent);
        ViewHelper.setScaleY(vg_left, 0.5f + 0.5f * percent);
        ViewHelper.setAlpha(vg_left, percent);
        if (isShowShadow) {
            ViewHelper.setScaleX(iv_shadow, f1 * 1.4f * (1 - percent * 0.12f));
            ViewHelper.setScaleY(iv_shadow, f1 * 1.85f * (1 - percent * 0.12f));
        }
        getBackground().setColorFilter(evaluate(percent, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
    }

    private Integer evaluate(float fraction, Object startValue, Integer endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;
        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;
        return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
                | (int) ((startR + (int) (fraction * (endR - startR))) << 16)
                | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
                | (int) ((startB + (int) (fraction * (endB - startB))));
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public enum Status {
        Drag, Open, Close
    }

    public Status getStatus() {
        if (mainLeft == 0) {
            status = Status.Close;
        } else if (mainLeft == range) {
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
            if (dragHelper.smoothSlideViewTo(vg_main, range, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            vg_main.layout(range, 0, range * 2, height);
            dispatchDragEvent(range);
        }
    }

    public void close() {
        close(true);
    }

    public void close(boolean animate) {
        if (animate) {
            if (dragHelper.smoothSlideViewTo(vg_main, 0, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            vg_main.layout(0, 0, width, height);
            dispatchDragEvent(0);
        }
    }

}

package com.bluemor.reddotface.view;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class DragLayout extends FrameLayout {

	private static final float RANGE = 0.6f;

	private GestureDetectorCompat gestureDetector;
	private DragListener dragListener;
	private ViewDragHelper dragHelper;

	private int width;
	private int height;
	private int range;
	private int mainLeft;

	private RelativeLayout vg_left;
	private MyRelativeLayout vg_main;

	private Status status = Status.Close;

	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		gestureDetector = new GestureDetectorCompat(context,
				new YScrollDetector());
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
			dispatchDragEvent(mainLeft);
			if (changedView == vg_left) {
				vg_left.layout(0, 0, width, height);
				vg_main.layout(mainLeft, 0, mainLeft + width, height);
			}
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
		range = (int) (width * RANGE);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		vg_left.layout(0, 0, width, height);
		vg_main.layout(mainLeft, 0, mainLeft + width, height);
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
		float percent = mainLeft / (float) range;
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

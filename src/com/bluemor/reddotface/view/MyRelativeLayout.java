package com.bluemor.reddotface.view;

import com.bluemor.reddotface.view.DragLayout.Status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class MyRelativeLayout extends RelativeLayout {
	private DragLayout sl;

	public MyRelativeLayout(Context context) {
		super(context);
	}

	public MyRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setSl(DragLayout sl) {
		this.sl = sl;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (sl.getStatus() != Status.Close) {
			return true;
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (sl.getStatus() != Status.Close) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				sl.close();
			}
			return true;
		}
		return super.onTouchEvent(event);
	}

}

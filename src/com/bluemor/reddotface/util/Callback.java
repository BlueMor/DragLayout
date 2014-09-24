package com.bluemor.reddotface.util;

public interface Callback {
	void onBefore();

	boolean onRun();

	void onAfter(boolean b);
}

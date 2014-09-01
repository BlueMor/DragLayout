DragLayout
==========

* 一个制作头像的Android Project
* 主界面仿QQ5.0主界面侧滑效果
* 使用新support.v4包下的ViewDragHelper实现
* DragLayout类代码仅200行


## **How To Use** ##
```java
<com.bluemor.reddotface.view.DragLayout
    android:id="@+id/sl_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >
    
    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent" >
    </RelativeLayout>
    
    <com.bluemor.reddotface.view.MyRelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </com.bluemor.reddotface.view.MyRelativeLayout>
    
</com.bluemor.reddotface.view.DragLayout>


DragLayout sl = (DragLayout) findViewById(R.id.sl_main);
sl.setDragListener(new DragListener() {
	@Override
	public void onOpen() {
	}

	@Override
	public void onClose() {
	}

	@Override
	public void onDrag(float percent) {
		//percent为打开的比例
	}
});
```

##Screenshots
![image](https://github.com/BlueMor/DragLayout/blob/master/screenshots/1.png)

![image](https://github.com/BlueMor/DragLayout/blob/master/screenshots/2.png)

![image](https://github.com/BlueMor/DragLayout/blob/master/screenshots/3.png)


DragLayout
==========

##Screenshots
![image](https://github.com/BlueMor/DragLayout/blob/master/screenshots/1.gif)

![image](https://github.com/BlueMor/DragLayout/blob/master/screenshots/2.png)


* 一个制作头像的Android Project
* 完全实现QQ5.0主界面侧滑效果
* 使用新support.v4包下的ViewDragHelper实现
* DragLayout类代码仅200行

相对于 https://github.com/daimajia/AndroidSwipeLayout 项目，优点有：
* 左右滑动和上下滑动事件互不冲突（不会卡顿）
* 子view（包括listview，gridview的item）的重新绘制不影响DragLayout的滑动（不会卡顿）

参考文档
* http://flavienlaurent.com/blog/2013/08/28/each-navigation-drawer-hides-a-viewdraghelper/



## **How To Use** ##
```java
<com.bluemor.reddotface.view.DragLayout
    android:id="@+id/dl"
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


DragLayout dl = (DragLayout) findViewById(R.id.dl);
dl.setDragListener(new DragListener() {
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


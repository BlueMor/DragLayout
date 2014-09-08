package com.bluemor.reddotface.activity;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemor.reddotface.R;
import com.bluemor.reddotface.adapter.ImageAdapter;
import com.bluemor.reddotface.util.Util;
import com.bluemor.reddotface.view.DragLayout;
import com.bluemor.reddotface.view.DragLayout.DragListener;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends Activity {

	private DragLayout dl;
	private GridView gv_img;
	private ImageAdapter adapter;
	private ListView lv;
	private TextView tv_noimg;
	private ImageView iv_icon, iv_bottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Util.initImageLoader(this);
		initDragLayout();
		initView();
	}

	private void initDragLayout() {
		dl = (DragLayout) findViewById(R.id.dl);
		dl.setDragListener(new DragListener() {
			@Override
			public void onOpen() {
				lv.smoothScrollToPosition(new Random().nextInt(30));
			}

			@Override
			public void onClose() {
			}

			@Override
			public void onDrag(float percent) {
				animate(percent);
			}
		});
	}

	private void animate(float percent) {
		ViewGroup vg_left = dl.getVg_left();
		ViewGroup vg_main = dl.getVg_main();

		float f1 = 1 - percent * 0.3f;
		ViewHelper.setScaleX(vg_main, f1);
		ViewHelper.setScaleY(vg_main, f1);
		ViewHelper.setTranslationX(vg_left, -vg_left.getWidth() / 2.2f
				+ vg_left.getWidth() / 2.2f * percent);
		ViewHelper.setScaleX(vg_left, 0.5f + 0.5f * percent);
		ViewHelper.setScaleY(vg_left, 0.5f + 0.5f * percent);
		ViewHelper.setAlpha(vg_left, percent);
		ViewHelper.setAlpha(iv_icon, 1 - percent);

		int color = (Integer) Util.evaluate(percent,
				Color.parseColor("#ff000000"), Color.parseColor("#00000000"));
		dl.getBackground().setColorFilter(color, Mode.SRC_OVER);
	}

	private void initView() {
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		iv_bottom = (ImageView) findViewById(R.id.iv_bottom);
		gv_img = (GridView) findViewById(R.id.gv_img);
		tv_noimg = (TextView) findViewById(R.id.iv_noimg);
		gv_img.setFastScrollEnabled(true);
		adapter = new ImageAdapter(this);
		gv_img.setAdapter(adapter);
		gv_img.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MainActivity.this,
						ImageActivity.class);
				intent.putExtra("path", adapter.getItem(position));
				startActivity(intent);
			}
		});
		lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				R.layout.item_text, new String[] { "NewBee", "ViCi Gaming",
						"Evil Geniuses", "Team DK", "Invictus Gaming", "LGD",
						"Natus Vincere", "Team Empire", "Alliance", "Cloud9",
						"Titan", "Mousesports", "Fnatic", "Team Liquid",
						"MVP Phoenix", "NewBee", "ViCi Gaming",
						"Evil Geniuses", "Team DK", "Invictus Gaming", "LGD",
						"Natus Vincere", "Team Empire", "Alliance", "Cloud9",
						"Titan", "Mousesports", "Fnatic", "Team Liquid",
						"MVP Phoenix" }));
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Toast.makeText(getApplicationContext(), "click " + position,
						Toast.LENGTH_LONG).show();
			}
		});
		iv_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dl.open();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadImage();
	}

	private void loadImage() {
		adapter.addAll(Util.getGalleryPhotos(this));
		if (adapter.isEmpty()) {
			tv_noimg.setVisibility(View.VISIBLE);
		} else {
			tv_noimg.setVisibility(View.GONE);
			String s = "file://" + adapter.getItem(0);
			ImageLoader.getInstance().displayImage(s, iv_icon);
			ImageLoader.getInstance().displayImage(s, iv_bottom);
		}
		iv_icon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
	}

}

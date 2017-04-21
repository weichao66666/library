package io.weichao.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerRecyclerView extends RecyclerView {
	public ViewPagerRecyclerView(Context context) {
		this(context, null, 0);
	}

	public ViewPagerRecyclerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewPagerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return false;
	}
}

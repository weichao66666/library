package io.weichao.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewPagerRecyclerViewAdapter extends Adapter<ViewPagerRecyclerViewAdapter.ViewHolder> {
	public static final int TYPE_NORMAL = 1;
	public static final int TYPE_END = 2;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public String boundText;
		public TextView textView;

		public ViewHolder(TextView textView) {
			super(textView);
			this.textView = textView;
		}

		@Override
		public String toString() {
			return textView.toString();
		}
	}

	private ArrayList<String> mTextLists;

	public ViewPagerRecyclerViewAdapter(String text) {
		mTextLists = new ArrayList<String>();
		mTextLists.add(text);
		mTextLists.add("继续上滑从头开始阅读简介，下滑退出");
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		TextView textView = new TextView(parent.getContext());

		textView.setBackgroundColor(Color.TRANSPARENT);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

		return new ViewHolder(textView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		position = position % mTextLists.size();
		String text = mTextLists.get(position);

		TextPaint tp = holder.textView.getPaint();

		switch (position) {
		case 0:
			text = "        " + text;
			tp.setFakeBoldText(false);
			holder.textView.setGravity(Gravity.START);
			break;
		case 1:
			text = "\n" + text + "\n";
			tp.setFakeBoldText(true);
			holder.textView.setGravity(Gravity.CENTER);
			break;
		}

		holder.boundText = text;
		holder.textView.setText(text);
	}

	@Override
	public int getItemCount() {
		return Integer.MAX_VALUE;
	}
}

package io.weichao.adapter;

import android.app.Activity;
import android.graphics.Bitmap;

import java.util.ArrayList;

import io.weichao.util.BitmapUtil;

public class BaseMultPagesAdapter {
    protected Activity mActivity;
    protected ArrayList<Integer> mImageLists;

    public BaseMultPagesAdapter(Activity activity) {
        mActivity = activity;
        mImageLists = new ArrayList<>();
    }

    public int getCount() {
        return mImageLists.size();
    }


    public Bitmap getImage(int position) {
        return BitmapUtil.getBitmap(mActivity, mImageLists.get(position));
    }
}

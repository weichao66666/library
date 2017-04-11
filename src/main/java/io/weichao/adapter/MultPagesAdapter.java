package io.weichao.adapter;

import android.app.Activity;
import android.graphics.Bitmap;

import java.util.ArrayList;

import io.weichao.util.BitmapUtil;

public class MultPagesAdapter {
    private Activity mActivity;
    private ArrayList<Integer> mImageLists;

    public MultPagesAdapter(Activity activity) {
        mActivity = activity;
        mImageLists = new ArrayList<>();
//        mImageLists.add(R.mipmap.a);
//        mImageLists.add(R.mipmap.b);
//        mImageLists.add(R.mipmap.c);
//        mImageLists.add(R.mipmap.d);
//        mImageLists.add(R.mipmap.e);
    }

    public int getCount() {
        return mImageLists.size();
    }


    public Bitmap getImage(int position) {
        return BitmapUtil.getBitmap(mActivity, mImageLists.get(position));
    }
}

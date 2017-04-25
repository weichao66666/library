package io.weichao.util;

import android.support.v4.view.ViewPager;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class ViewPagerUtil {
    private ViewPagerUtil() {
    }

    public static void setScroller(ViewPager viewPager, Scroller scroller) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

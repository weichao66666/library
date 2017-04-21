package io.weichao.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ArrayList<Fragment> fragmentLists = new ArrayList<>();
    public ViewPager viewPager;

    public ViewPagerAdapter(FragmentManager fm, ViewPager viewPager) {
        super(fm);

        this.viewPager = viewPager;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            position %= fragmentLists.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragmentLists.get(position);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public void add(Fragment fragment) {
        fragmentLists.add(fragment);
        notifyDataSetChanged();
        viewPager.setCurrentItem(getCount() - 1, true);

    }

    public void remove(int position) {
        position %= fragmentLists.size();
        fragmentLists.remove(position);
        notifyDataSetChanged();
    }

    public void remove(Fragment fragment) {
        fragmentLists.remove(fragment);
        notifyDataSetChanged();
        int position = viewPager.getCurrentItem();
        viewPager.setAdapter(this);
        if (position >= this.getCount()) {
            position = this.getCount() - 1;
        }
        viewPager.setCurrentItem(position, true);
    }
}

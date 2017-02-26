package com.cq9191.cashbook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 91910 on 2016/8/31.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    private List<String> titleList;
    private FragmentManager fm;

    public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.fm = fm;
        this.fragmentList = fragments;
        this.titleList = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    public void refreshFragments(List<Fragment> newFragmentList) {
        if (fragmentList != null){
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : fragmentList){
                ft.remove(f);
            }
            ft.commit();
            ft = null;
            fm.executePendingTransactions();
        }
        this.fragmentList = newFragmentList;
        notifyDataSetChanged();
    }
}

package com.sepidsa.fortytwocalculator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Farshid on 6/12/2015.
 */
class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;


        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int index) {
        // TODO Auto-generated method stub



        return this.fragments.get(index);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.fragments.size();
    }

}

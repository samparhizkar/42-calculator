package com.sepidsa.fortytwocalculator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by Farshid on 6/12/2015.
 */
internal class ViewPagerAdapter(
    fm: FragmentManager,
    private val fragments: List<Fragment>,
) : FragmentStatePagerAdapter(fm) {

    override fun getItem(index: Int): Fragment {
        return fragments[index]
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int {
        return fragments.size
    }
}


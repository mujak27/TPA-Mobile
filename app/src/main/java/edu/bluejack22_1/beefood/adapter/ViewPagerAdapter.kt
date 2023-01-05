package edu.bluejack22_1.beefood.adapter

import android.icu.text.CaseMap.Title
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    var fragmentArrayList : ArrayList<Fragment> = ArrayList()

    var titleList  : ArrayList<String> = ArrayList()

    override fun getCount(): Int {
        return fragmentArrayList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentArrayList.get(position)
    }

    fun addFragment(fragment: Fragment, title: String){
        fragmentArrayList.add(fragment)
        titleList.add(title)
    }

    override fun getPageTitle(position: Int) : CharSequence?{
        return titleList.get(position)
    }


}
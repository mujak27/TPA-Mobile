package edu.bluejack22_1.beefood.seller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.ViewPagerAdapter

class SellerTransactions : AppCompatActivity() {

    lateinit var tabLayout : TabLayout
    lateinit var viewPager : ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_seller_transactions)


        tabLayout = findViewById(R.id.layout_viewpager)
        viewPager = findViewById(R.id.seller_viewpager)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(SellerFragmentPast(), "past")
        adapter.addFragment(SellerFragmentActive(), "active")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

    }
}
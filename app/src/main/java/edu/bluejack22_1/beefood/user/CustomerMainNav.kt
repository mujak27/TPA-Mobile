package edu.bluejack22_1.beefood.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.user.fragments.CustomerFragmentHome
import edu.bluejack22_1.beefood.user.fragments.CustomerFragmentTransactions
import edu.bluejack22_1.beefood.user.fragments.CustomerFragmentUpdate

class CustomerMainNav : AppCompatActivity() {

    fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.customer_nav_page, fragment)
            commit()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_main_nav)

        var customerFragmentHome = CustomerFragmentHome()
        var customerFragmentTransactions = CustomerFragmentTransactions()
        var customerFragmentUpdate = CustomerFragmentUpdate()

        makeCurrentFragment(customerFragmentHome)

        findViewById<BottomNavigationView>(R.id.customer_nav).setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.nav_menu_home -> makeCurrentFragment(customerFragmentHome)
                R.id.nav_menu_transaction -> makeCurrentFragment(customerFragmentTransactions)
                R.id.nav_menu_update -> makeCurrentFragment(customerFragmentUpdate)
            }
            true
        }

    }
}
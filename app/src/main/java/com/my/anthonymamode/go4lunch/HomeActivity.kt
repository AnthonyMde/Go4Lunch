package com.my.anthonymamode.go4lunch

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.my.anthonymamode.go4lunch.R.id.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(homeToolbar)
        configureDrawerMenu()
        homeBottomNavBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_map -> {
                // TODO: fragment transaction (map fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                // TODO: fragment transaction (list fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_workmates -> {
                // TODO: fragment transaction (workmates fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                homeDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun configureDrawerMenu() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_drawer_white_24dp)
        }
        homeNavigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                drawer_logout -> {
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                }
                drawer_settings -> Toast.makeText(this, "Non implémenté", Toast.LENGTH_SHORT).show()
                drawer_my_food -> Toast.makeText(this, "Non implémenté", Toast.LENGTH_SHORT).show() 
            }
            true
        }
    }


}

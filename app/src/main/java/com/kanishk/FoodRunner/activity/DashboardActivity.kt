package com.kanishk.FoodRunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.kanishk.FoodRunner.R
import com.kanishk.FoodRunner.adapter.RestaurantMenuAdapter
import com.kanishk.FoodRunner.fragment.*
import com.kanishk.FoodRunner.fragment.RestaurantFragment.Companion.resId
import com.kanishk.FoodRunner.util.DrawerLocker
import com.kanishk.FoodRunner.util.SessionManager

class DashboardActivity : AppCompatActivity(), DrawerLocker {

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled)
            DrawerLayout.LOCK_MODE_UNLOCKED
        else
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED

        drawerLayout.setDrawerLockMode(lockMode)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = enabled
    }

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var sessionManager: SessionManager
    private lateinit var sharedPrefs: SharedPreferences
    private var previousMenuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this@DashboardActivity)
        sharedPrefs = this@DashboardActivity.getSharedPreferences(
            sessionManager.PREF_NAME,
            sessionManager.PRIVATE_MODE
        )

        //******************************INITALIZATION**********************************
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)


        //****************************SETTING UP TOOL BAR*******************************
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        //*************************ACTION BAR TOGGLE****************************
        actionBarDrawerToggle = object :
            ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                val pendingRunnable = Runnable {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }
                Handler().postDelayed(pendingRunnable, 50)
            }
        }
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        //***********************DISPLAYING************************
        display()

       //**************************CLICK LISTENER INSIDE DRAWER MENU***************************
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->

            /*Unchecking the previous menu item when a new item is clicked*/
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            item.isCheckable = true
            item.isChecked = true

            previousMenuItem = item

            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 100)
            //To make transition Good, I used a post delay of 100ms

            val fragmentTransaction = supportFragmentManager.beginTransaction()

            when (item.itemId) {

                R.id.home -> {
                    val homeFragment = HomeFragment()
                    fragmentTransaction.replace(R.id.frame, homeFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "All Restaurants"
                }

                R.id.myProfile -> {
                    val profileFragment = ProfileFragment()
                    fragmentTransaction.replace(R.id.frame, profileFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "My profile"
                }

                R.id.order_history -> {
                    val orderHistoryFragment = OrderHistoryFragment()
                    fragmentTransaction.replace(R.id.frame, orderHistoryFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "My Previous Orders"
                }

                R.id.favRes -> {
                    val favFragment = FavouritesFragment()
                    fragmentTransaction.replace(R.id.frame, favFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "Favorite Restaurants"
                }

                R.id.faqs -> {
                    val faqFragment = FAQFragment()
                    fragmentTransaction.replace(R.id.frame, faqFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                }

                R.id.logout -> {

                    /*Creating a confirmation dialog*/
                    val builder = AlertDialog.Builder(this@DashboardActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            sessionManager.setLogin(false)
                            sharedPrefs.edit().clear().apply()
                            startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                            Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                            ActivityCompat.finishAffinity(this)
                        }
                        .setNegativeButton("No") { _, _ ->
                            display()
                        }
                        .create()
                        .show()
                }

            }
            return@setNavigationItemSelectedListener true
        }

        val convertView = LayoutInflater.from(this@DashboardActivity).inflate(R.layout.drawer_header, null)
        val userName: TextView = convertView.findViewById(R.id.txtDrawerText)
        val userPhone: TextView = convertView.findViewById(R.id.txtDrawerSecondaryText)
        val appIcon: ImageView = convertView.findViewById(R.id.imgDrawerImage)
        userName.text = sharedPrefs.getString("user_name", null)
        val phoneText = "+91-${sharedPrefs.getString("user_mobile_number", null)}"
        userPhone.text = phoneText
        navigationView.addHeaderView(convertView)

        userName.setOnClickListener {
            val profileFragment = ProfileFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, profileFragment)
            transaction.commit()
            supportActionBar?.title = "My profile"
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 50)
        }

        appIcon.setOnClickListener {
            val profileFragment = ProfileFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, profileFragment)
            transaction.commit()
            supportActionBar?.title = "My profile"
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 50)
        }
    }

    fun display(){
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val f = supportFragmentManager.findFragmentById(R.id.frame)
        when (id) {
            android.R.id.home -> {
                if (f is RestaurantFragment) {
                    onBackPressed()
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentById(R.id.frame)
        when (f) {
            is HomeFragment -> {
                Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                super.onBackPressed()
            }
            is RestaurantFragment -> {
                if (!RestaurantMenuAdapter.isCartEmpty) {
                    val builder = AlertDialog.Builder(this@DashboardActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Going back will reset cart items. Do you still want to proceed?")
                        .setPositiveButton("Yes") { _, _ ->
                            val clearCart =
                                CartActivity.ClearDBAsync(applicationContext, resId.toString()).execute().get()
                            display()
                            RestaurantMenuAdapter.isCartEmpty = true
                        }
                        .setNegativeButton("No") { _, _ ->

                        }
                        .create()
                        .show()
                } else {
                    display()
                }
            }
            else -> display()
        }
    }

}

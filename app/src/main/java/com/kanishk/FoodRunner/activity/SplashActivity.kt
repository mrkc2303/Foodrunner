package com.kanishk.FoodRunner.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.kanishk.FoodRunner.R
import com.kanishk.FoodRunner.util.SessionManager

class SplashActivity : AppCompatActivity() {

    val permissionString =
        arrayOf(Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET)

    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sessionManager = SessionManager(this)

        if (!permissions(this, permissionString)) {
            ActivityCompat.requestPermissions(this, permissionString, 101)
        } else {

            Handler().postDelayed({
                if (sessionManager.isLoggedIn()) {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 2000)
        }

    }

    fun permissions(context: Context, permissions: Array<String>): Boolean {
        var hasAllPermissions = true
        for (permission in permissions) {
            val res = context.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED) {
                hasAllPermissions = false
            }
        }
        return hasAllPermissions
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Handler().postDelayed({
                        if (sessionManager.isLoggedIn()) {
                            val intent = Intent(this, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }, 2000)
                } else {
                    Toast.makeText(
                        this,
                        "Please grant all permissions to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                    this.finish()
                }
                return
            }
            else -> {
                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                this.finish()
                return
            }
        }
    }

    /*Lifecycle method. Here the finish() ensures that the activity does not open again when the user presses back button*/
    override fun onPause() {
        super.onPause()
        finish()
    }
}

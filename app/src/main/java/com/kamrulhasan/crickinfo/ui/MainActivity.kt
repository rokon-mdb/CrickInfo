package com.kamrulhasan.crickinfo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.kamrulhasan.crickinfo.R
import com.kamrulhasan.crickinfo.databinding.ActivityMainBinding
import com.kamrulhasan.crickinfo.network.NetworkConnection
import com.kamrulhasan.crickinfo.utils.MyApplication
import com.kamrulhasan.crickinfo.utils.MyNotification
import com.kamrulhasan.crickinfo.viewmodel.CrickInfoViewModel
import java.util.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var viewModel: CrickInfoViewModel

    private var primaryApiCall = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CrickInfoViewModel::class.java]

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_host) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
        binding.bottomNavBar.setupWithNavController(navController)

        binding.bottomNavBar.setOnItemSelectedListener {
            Log.d(TAG, "onCreate: home fragment1")
            when (it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.fixturesFragment -> {
                    navController.navigate(R.id.fixturesFragment)
                    true
                }
                R.id.playersFragment -> {
                    navController.navigate(R.id.playersFragment)
                    true
                }
                R.id.newsFragment -> {
                    navController.navigate(R.id.newsFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }

        NetworkConnection().observe(this) {
            val rootView: View = findViewById(android.R.id.content)
            if (it) {

                /// if api is not call before
                if (!primaryApiCall) {
                    viewModel.apiCallOnce()
                    primaryApiCall = true
                }
                binding.tvConnectionLoss.visibility = View.GONE
                Toast.makeText(
                    MyApplication.appContext,
                    "Internet is connected.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                binding.tvConnectionLoss.visibility = View.VISIBLE
                Snackbar.make(rootView, "No Internet !!!", Snackbar.LENGTH_SHORT).show()
            }
        }

        MyNotification.scheduleNotification(
            Calendar.getInstance().timeInMillis + (6 * 1000),
            "Welcome to CrickInfo"
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
package com.example.bike_app.ui

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.bike_app.BikeApp
import com.example.bike_app.BuildConfig
import com.example.bike_app.R
import com.example.bike_app.databinding.ActivityMainBinding
import com.example.bike_app.di.databaseModule
import com.example.bike_app.di.repositoryModule
import com.example.bike_app.di.serviceModule
import com.example.bike_app.di.viewmodelModule
import com.example.bike_app.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class MainActivity : AppCompatActivity(), KoinComponent {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        navigateToTrackingFragmentifNeeded(intent)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_navigationView)
            .setupWithNavController(navController)
        navController.addOnDestinationChangedListener{_,destination,_->
            when(destination.id){
                R.id.rideListFragment, R.id.homeFragment4 ->binding.bottomNavigationView.visibility = View.VISIBLE
                else-> binding.bottomNavigationView.visibility=View.GONE
        }
    }
        Timber.plant(Timber.DebugTree())
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentifNeeded(intent)
    }
    private fun navigateToTrackingFragmentifNeeded(intent: Intent?){
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            binding.navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
 }
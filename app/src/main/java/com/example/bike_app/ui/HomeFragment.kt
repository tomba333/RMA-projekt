package com.example.bike_app.ui

import android.Manifest
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceControl
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.bike_app.R
import com.example.bike_app.databinding.FragmentUiBinding
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.withCreated
import androidx.navigation.fragment.findNavController
import com.example.bike_app.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.bike_app.other.TrackingUtility
import org.koin.android.ext.android.inject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class HomeFragment: Fragment(), EasyPermissions.PermissionCallbacks {

    lateinit var binding: FragmentUiBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUiBinding.inflate(layoutInflater)
        requestPermission()

        loadData()

        binding.bntSave.setOnClickListener { save()
            loadData()
        }
        binding.btnWalk.setOnClickListener {
            findNavController().navigate(R.id.walkTrackingFragment)

        }
        binding.btnBike.setOnClickListener {
            findNavController().navigate(R.id.trackingFragment)
        }
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermission(){
        if(TrackingUtility.hasLocationPermission(requireContext())){
            return
        }else{
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
                EasyPermissions.requestPermissions(
                    this,
                    "You need to accs location permission to use this app",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            }else{
                EasyPermissions.requestPermissions(
                    this,
                    "You need to accs location permission to use this app",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
    }

    private fun save() {
        val userWeight = binding.etWeight.text.toString()
        val sharedPref = activity?.getSharedPreferences("sharedPrefs",0)
        val editor = sharedPref?.edit()
        editor?.apply {
            putFloat("STRING_KEY", userWeight.toFloat())
        }?.apply()
    }

    private fun loadData(){
        val sharedPref = activity?.getSharedPreferences("sharedPrefs",0)
        val saveString = sharedPref?.getFloat("STRING_KEY",0f)
        binding.tvWeightCheck.text = saveString.toString()
        if(saveString!! > 0f)
        {
            binding.btnBike.visibility = View.VISIBLE
        }else{
            binding.btnBike.visibility = View.GONE
            binding.WeightText.text = "Insert your weight"

        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermission()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this )
    }
}
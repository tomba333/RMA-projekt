package com.example.bike_app.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationChannelCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bike_app.R
import com.example.bike_app.databinding.TrackingBinding
import com.example.bike_app.db.Ride
import com.example.bike_app.di.databaseModule
import com.example.bike_app.di.serviceModule
import com.example.bike_app.other.Constants.ACTION_PAUSE_SERVICE
import com.example.bike_app.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.bike_app.other.Constants.ACTION_STOP_SERVICE
import com.example.bike_app.other.Constants.MAP_ZOOM
import com.example.bike_app.other.Constants.POLYLINE_COLOR
import com.example.bike_app.other.Constants.POLYLINE_WIDTH
import com.example.bike_app.other.TrackingUtility
import com.example.bike_app.presentation.MainViewModel
import com.example.bike_app.services.Polyline
import com.example.bike_app.services.TrackingService
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.*
import kotlin.math.log
import kotlin.math.round

class TrackingFragment: Fragment() {

    private lateinit var binding: TrackingBinding
    private val viewModel:MainViewModel by viewModel()
    private var map:GoogleMap? = null
    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var currentTimeInMillis = 0L
    val sharedPref : SharedPreferences by inject()
    val saveString = sharedPref?.getFloat("STRING_KEY",0f)
    val weight = saveString
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TrackingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        binding.btnFinish.setOnClickListener {
            zoomToSeeWholeTrack()
            endRideAndSaveToDb()

        }
        binding.btnToggle.setOnClickListener{
            toggleRide()
        }
        trackingObserve()
        return binding.root
    }

    private fun trackingObserve(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer{
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner,Observer{
            pathPoints = it
            addAllPolylines()
            moveCameraToUser()
        })
        TrackingService.timeRunInMilis.observe(viewLifecycleOwner,Observer{
            currentTimeInMillis = it
            var formattedTime = TrackingUtility.getFormatedStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime
        })

    }
    private fun toggleRide(){
        if (isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }
    private fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints){
            for (pos in polyline){
                bounds.include(pos)
            }

        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()
            )
        )
    }
    private fun endRideAndSaveToDb(){
        map?.snapshot { bmp->
            var distanceInMetars = 0
            for (polyline in pathPoints){
                distanceInMetars += TrackingUtility.calculatePolylineLenght(polyline).toInt()
            }
            val avgSpeed = round((distanceInMetars/1000f) / (currentTimeInMillis/1000f/60/60)*10)/10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMetars/1000f)* weight.toString().toFloat()).toInt()
            val ride = Ride(bmp,dateTimeStamp,avgSpeed,distanceInMetars,currentTimeInMillis,caloriesBurned)
            viewModel.insertRide(ride)
            Toast.makeText(context,"Ride is saved", Toast.LENGTH_SHORT).show()
        }
        stopRun()
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_homeFragment4)
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking = isTracking
        if(!isTracking && currentTimeInMillis> 0L){
            binding.btnToggle.text = "Resume"
            binding.btnFinish.visibility= View.VISIBLE
        }else if(isTracking){
            binding.btnToggle.text = "Stop"
            binding.btnFinish.visibility= View.GONE
        }
    }
    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines(){
        for (polyline in pathPoints){
            val polylineOption = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOption)
        }
    }
    private fun addLatestPolyline(){
        if (pathPoints.isNotEmpty()&&pathPoints.last().size>1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng = pathPoints.last().last()
            val polylineOption = PolylineOptions().color(POLYLINE_COLOR).width(POLYLINE_WIDTH).add(preLastLatLng).add(lastLatLng)
            map?.addPolyline(polylineOption)
        }
    }

    private fun sendCommandToService(action: String)=
        Intent(requireContext(),TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {//cacha mapu da ju nemoramo stalno otvarati
        super.onSaveInstanceState(outState)
        binding.mapView?.onSaveInstanceState(outState)
    }

}
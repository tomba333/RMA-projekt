package com.example.bike_app.ui

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bike_app.R
import com.example.bike_app.databinding.FragmentWalkTrackingBinding
import com.example.bike_app.db.Ride
import com.example.bike_app.db.Walk
import com.example.bike_app.other.Constants
import com.example.bike_app.other.TrackingUtility
import com.example.bike_app.presentation.MainViewModel
import com.example.bike_app.presentation.WalkViewModel
import com.example.bike_app.services.Polyline
import com.example.bike_app.services.TrackingService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*
import kotlin.math.round



class WalkTrackingFragment:Fragment(), SensorEventListener{
    private  lateinit var binding: FragmentWalkTrackingBinding
    private val walkViewModel : WalkViewModel by viewModel()
    private var map: GoogleMap? = null
    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var currentTimeInMillis = 0L
    val sharedPref : SharedPreferences by inject()
    val saveString = sharedPref?.getFloat("STRING_KEY",0f)
    val weight = saveString

    private var magnitudeprevious :Double = 0.0
    private var stepCounter = 0f

    private lateinit var sensorManager:SensorManager
    private var pedometar : Sensor?= null
    //private var sensorManager:SensorManager ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWalkTrackingBinding.inflate(layoutInflater)
        binding.mapView2.onCreate(savedInstanceState)
        binding.mapView2.getMapAsync {
            map = it
            addAllPolyLines()
        }
        binding.btnFinish.setOnClickListener {
            zoomToSeeWholeTrack()
            endWalkAndSaveToDb()
        }
        binding.btnToggle.setOnClickListener {
            toggleWalk()
        }
        sensorManager = this.activity?.getSystemService(SENSOR_SERVICE) as SensorManager
        trackingObserve()
        return binding.root
    }
    private fun pauseStepCounter(){
        sensorManager.unregisterListener(this)
    }
    private fun startStepCounter(){
            sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }
    private fun trackingObserve() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer{
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner,Observer{
            pathPoints = it
            addAllPolyLines()
            moveCameraToUser()
        })
        TrackingService.timeRunInMilis.observe(viewLifecycleOwner,Observer{
            currentTimeInMillis = it
            var formattedTime = TrackingUtility.getFormatedStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime
        })
       /* TrackingService.stepCounter.observe(viewLifecycleOwner, Observer {
            stepCounter = it
            binding.tvSteps.text = stepCounter.toString()
        })*/
}

    private fun moveCameraToUser() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    Constants.MAP_ZOOM
                )
            )
        }
    }
    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if(!isTracking && currentTimeInMillis> 0L){
            binding.btnToggle.text = "Resume"
            binding.btnFinish.visibility= View.VISIBLE
        }else if(isTracking){
            binding.btnToggle.text = "Stop"
            binding.btnFinish.visibility= View.GONE
        }

    }

    private fun toggleWalk() {
        if (isTracking){
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
            pauseStepCounter()
        }else{
            sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
            startStepCounter()
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
        it.action = action
        requireContext().startService(it)
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints){
            for (pos in polyline){
                bounds.include(pos)
            }

        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView2.width,
                binding.mapView2.height,
                (binding.mapView2.height * 0.05f).toInt()
            )
        )
    }

    private fun endWalkAndSaveToDb() {
        map?.snapshot { bmp->
            var distanceInMetars = 0
            for (polyline in pathPoints){
                distanceInMetars += TrackingUtility.calculatePolylineLenght(polyline).toInt()
            }
            val steps = stepCounter
            val avgSpeed = round((distanceInMetars/1000f) / (currentTimeInMillis/1000f/60/60)*10) /10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMetars/1000f)* weight.toString().toFloat()).toInt()
            val walk = Walk(bmp,dateTimeStamp,avgSpeed,steps,currentTimeInMillis,caloriesBurned)
            walkViewModel.insertWalk(walk)
            Toast.makeText(context,"Walk is saved", Toast.LENGTH_SHORT).show()
        }
        stopRun()
    }

    private fun stopRun() {
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
        pauseStepCounter()
        findNavController().navigate(R.id.action_walkTrackingFragment_to_homeFragment4)
    }

    private fun addAllPolyLines() {
        for (polyline in pathPoints){
            val polylineOption = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOption)
        }
    }
    override fun onResume() {
        super.onResume()
        binding.mapView2.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView2?.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView2?.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView2?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView2?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {//cacha mapu da ju nemoramo stalno otvarati
        super.onSaveInstanceState(outState)
        binding.mapView2?.onSaveInstanceState(outState)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if(p0!!.sensor.type == Sensor.TYPE_ACCELEROMETER){
            var x : Float = p0.values[0]
            var y : Float = p0.values[1]
            var z : Float = p0.values[2]
            var magnitude : Double = Math.sqrt((x*x+y*y+z*z).toDouble())
            var magnitudeDelta :Double = magnitude-magnitudeprevious
            magnitudeprevious = magnitude
            if (magnitudeDelta >4){
                stepCounter++
            }
        }

        binding.tvSteps.text = stepCounter.toInt().toString()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }


}
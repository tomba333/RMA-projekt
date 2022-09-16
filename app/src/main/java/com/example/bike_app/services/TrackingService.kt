package com.example.bike_app.services


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.*
import android.location.Location
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.bike_app.other.Constants.ACTION_PAUSE_SERVICE
import com.example.bike_app.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.bike_app.other.Constants.ACTION_START_STICKY
import com.example.bike_app.other.Constants.ACTION_STOP_SERVICE
import com.example.bike_app.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.bike_app.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.bike_app.other.Constants.NOTIFICATION_CH_ID
import com.example.bike_app.other.Constants.NOTIFICATION_CH_NAME
import com.example.bike_app.other.Constants.NOTIFICATION_ID
import com.example.bike_app.other.Constants.TIMER_UPDATE_INTERVAL
import com.example.bike_app.other.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.scope.serviceScope
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService: LifecycleService(), KoinScopeComponent, SensorEventListener{

    var isFirstRun = true
    var serviceKilled = false
    override val scope: Scope by serviceScope()

    val fusedLocationProviderClient: FusedLocationProviderClient by inject()
    val baseNotificationBuilder: NotificationCompat.Builder by inject()

    lateinit var curNotificationBuilder: NotificationCompat.Builder
    private val timeRunInSecunds = MutableLiveData<Long>()


    //private lateinit var sensorManager:SensorManager
    //private var pedometar : Sensor? = null

    companion object{
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val timeRunInMilis = MutableLiveData<Long>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        curNotificationBuilder = baseNotificationBuilder
        fusedLocationProviderClient
        //sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //pedometar = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        isTracking.observe(this, Observer{
            upadateLocationTracking(it)
        })

    }
   /* private fun pauseStepCounter(){
        sensorManager.unregisterListener(this)
    }
    private fun startStepCounter(){
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
        {
            pedometar = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
            sensorManager.registerListener(this,pedometar, SensorManager.SENSOR_DELAY_UI)
        }
    }*/
    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSecunds.postValue(0L)
        timeRunInMilis.postValue(0L)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    }else
                    Timber.d("Resuming service...")
                    startTimer()
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Pause service")
                    pauseService()

                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stop service")
                    stopService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun stopService(){
            serviceKilled = true
            isFirstRun = true
            pauseService()
            postInitialValues()
            stopForeground(true)
            stopSelf()
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancelAll()

    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecundTimeStamp = 0L

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                //Razlika vremena izmedu sada i vremena pokretanja
                lapTime = System.currentTimeMillis() -timeStarted
                timeRunInMilis.postValue(timeRun+lapTime)
                if (timeRunInMilis.value!! >= lastSecundTimeStamp + 1000L){
                    timeRunInSecunds.postValue(timeRunInSecunds.value!! + 1)
                    lastSecundTimeStamp+= 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }

    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }
    @SuppressLint("MissingPermission")
    private fun upadateLocationTracking(isTracking: Boolean){
        if(isTracking){
            if (TrackingUtility.hasLocationPermission(this)){
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
    val locationCallback = object :LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            if (isTracking.value!!){
                p0?.locations?.let { locations ->
                    for (location in locations){
                        addPathPoint(location)
                        Timber.d("New location: ${location.latitude}, ${location.longitude}")
                    }
                }
            }

        }
    }

    private fun addPathPoint(location: Location?){
        location?.let {
            val pos = LatLng(location.latitude,location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    }?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService(){

        startTimer()
        isTracking.postValue(true)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())

        timeRunInSecunds.observe(this, Observer {
            val notification = curNotificationBuilder.setContentText(TrackingUtility.getFormatedStopWatchTime(it*1000))
            notificationManager.notify(NOTIFICATION_ID,notification.build())
        })
    }
   /* private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )
*/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel= NotificationChannel(
            NOTIFICATION_CH_ID,
            NOTIFICATION_CH_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun onSensorChanged(p0: SensorEvent?) {

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


}
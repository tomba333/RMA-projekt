package com.example.bike_app.ui

import android.text.format.DateFormat.format
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.bike_app.databinding.ItemRideBinding
import com.example.bike_app.db.Ride
import com.example.bike_app.other.TrackingUtility

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class RideViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(ride: Ride){
        val binding = ItemRideBinding.bind(itemView)
        binding.ivMap.setImageBitmap(ride.img)
        binding.tvDate.text ="Date:\n"+getDate(ride.timestamp)
        binding.tvDistance.text = "Distance:\n"+(ride.distanceInMeter/1000L).toString()+"km"
        binding.tvTime.text = "Time: \n"+TrackingUtility.getFormatedStopWatchTime(ride.timeInMilis)
        binding.tvAvgspeed.text= "Speed: \n"+ride.avgSpeed.toString()+" km/h"
        binding.tvCals.text ="Calories:\n"+ ride.caloriesBurned.toString()

    }
    private fun getDate(timestamp: Long) :String {
        val sdf = SimpleDateFormat("MM dd yyyy")
        return sdf.format(timestamp ).toString()
    }
}
package com.example.bike_app.ui.walkrv

import android.view.View
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.example.bike_app.databinding.ItemRideBinding
import com.example.bike_app.databinding.ItemWalkBinding
import com.example.bike_app.db.Ride
import com.example.bike_app.db.Walk
import com.example.bike_app.other.TrackingUtility
import java.text.SimpleDateFormat

class WalkViewHolder(itemView: View, listener: WalkAdapter.onItemClickListener): RecyclerView.ViewHolder(itemView) {

    fun bind(walk: Walk){
        val binding = ItemWalkBinding.bind(itemView)
        //binding.ivMap.setImageBitmap(walk.img)
        binding.tvDate.text ="Date:\n"+getDate(walk.timestamp)
        binding.tvSteps.text = "Steps:\n"+(walk.steps).toString()
        binding.tvTime.text = "Time: \n"+ TrackingUtility.getFormatedStopWatchTime(walk.timeInMilis)
        binding.tvAvgspeed.text= "Speed: \n"+walk.avgSpeed.toString()+" km/h"
        binding.tvCals.text ="Calories:\n"+ walk.caloriesBurned.toString()

    }
    private fun getDate(timestamp: Long) :String {
        val sdf = SimpleDateFormat("MM dd yyyy")
        return sdf.format(timestamp ).toString()
    }
    init {
        itemView.setOnClickListener{
            listener.onItemClick(adapterPosition)
        }
    }
}
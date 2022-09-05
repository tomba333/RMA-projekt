package com.example.bike_app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bike_app.R
import com.example.bike_app.db.Ride

class RideAdapter: RecyclerView.Adapter<RideViewHolder>() {
    private val rides = mutableListOf<Ride>()
    var onRideEventListener:OnRideEventListener?=null

    fun setRides(rides: List<Ride>){
        this.rides.clear()
        this.rides.addAll(rides)
        this.notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ride, parent, false)
        return RideViewHolder(view)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = rides[position]
        holder.bind(ride)
        onRideEventListener?.let {
            listener-> holder.itemView.setOnClickListener{listener.OnRideLongPressed(ride)}
        }
    }

    override fun getItemCount(): Int = rides.count()
}
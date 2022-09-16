package com.example.bike_app.ui.riderv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bike_app.R
import com.example.bike_app.db.Ride

class RideAdapter: RecyclerView.Adapter<RideViewHolder>() {
    private val rides = mutableListOf<Ride>()
    private lateinit var mListener:onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setRides(rides: List<Ride>){
        this.rides.clear()
        this.rides.addAll(rides)
        this.notifyDataSetChanged()
    }
    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener

    }

    fun getRide(position: Int):Ride{
        return this.rides.get(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ride, parent, false)
        return RideViewHolder(view,mListener)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = rides[position]
        holder.bind(ride)
    }

    override fun getItemCount(): Int = rides.count()
}
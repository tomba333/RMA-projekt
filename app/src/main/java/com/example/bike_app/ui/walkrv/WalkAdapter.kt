package com.example.bike_app.ui.walkrv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bike_app.R
import com.example.bike_app.db.Ride
import com.example.bike_app.db.Walk
import com.example.bike_app.ui.riderv.RideAdapter

class WalkAdapter:RecyclerView.Adapter<WalkViewHolder>() {
    private val walks = mutableListOf<Walk>()
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setWalks(walks: List<Walk>){
        this.walks.clear()
        this.walks.addAll(walks)
        this.notifyDataSetChanged()
    }
    fun setOnItemClickListener(listener: WalkAdapter.onItemClickListener){
        mListener = listener

    }
    fun getWalk(position: Int):Walk{
        return this.walks.get(position)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_walk,parent,false)
        return  WalkViewHolder(view,mListener)
    }

    override fun onBindViewHolder(holder: WalkViewHolder, position: Int) {
        val walk = walks[position]
        holder.bind(walk)
    }

    override fun getItemCount(): Int = walks.count()
}
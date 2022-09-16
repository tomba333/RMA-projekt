package com.example.bike_app.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bike_app.R
import com.example.bike_app.databinding.FragmentRideListBinding
import com.example.bike_app.db.Ride
import com.example.bike_app.presentation.MainViewModel
import com.example.bike_app.ui.riderv.RideAdapter
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class RideListFragment: Fragment(){

    private lateinit var binding: FragmentRideListBinding
    private val viewModel:MainViewModel by viewModel()
    private lateinit var adapter: RideAdapter
    private lateinit var rideRv:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRideListBinding.inflate(layoutInflater)
        setupRecyclerView()
        onSwipeDelete()
        viewModel.rides.observe(viewLifecycleOwner){
            if(it != null && it.isNotEmpty()){
                adapter.setRides(it)
            }
        }
        adapter.setOnItemClickListener(object : RideAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val Ride: Ride = adapter.getRide(position)
                val popupWindow = PopupWindow(context)
                val view = layoutInflater.inflate(R.layout.popup_ride,null)
                popupWindow.contentView = view
                val imgView = view.findViewById<ImageView>(R.id.ivMap)
                imgView.setImageBitmap(Ride.img)
                imgView.setOnClickListener {
                    popupWindow.dismiss()
                }
                popupWindow.showAtLocation(rideRv,Gravity.CENTER, 0 ,0)
            }


        })
        return binding.root
    }

    private fun setupRecyclerView() {
        rideRv = binding.rvRide
        rideRv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        adapter = RideAdapter()
        binding.rvRide.adapter = adapter

    }
    private fun onSwipeDelete(){
        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val Ride: Ride = adapter.getRide(viewHolder.adapterPosition)
                viewModel.deleteRide(Ride)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                Snackbar.make(rideRv, "Deleted", Snackbar.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(rideRv)
    }
}
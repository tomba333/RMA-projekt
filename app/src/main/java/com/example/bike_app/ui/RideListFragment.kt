package com.example.bike_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bike_app.databinding.FragmentRideListBinding
import com.example.bike_app.db.Ride
import com.example.bike_app.presentation.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RideListFragment: Fragment(), OnRideEventListener {

    private lateinit var binding: FragmentRideListBinding
    private val viewModel:MainViewModel by viewModel()
    private lateinit var adapter: RideAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRideListBinding.inflate(layoutInflater)
        setupRecyclerView()
        viewModel.rides.observe(viewLifecycleOwner){
            if(it != null && it.isNotEmpty()){
                adapter.setRides(it)
            }
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvRide.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        adapter = RideAdapter()
        adapter.onRideEventListener = this
        binding.rvRide.adapter = adapter

    }

    override fun OnRideLongPressed(ride: Ride?): Boolean {
        ride?.let { it->
            viewModel.deleteRide(it)
            adapter.notifyItemRemoved(it.Id!!)
            Toast.makeText(context,"Ride deleted..", Toast.LENGTH_SHORT)
        }
        return true
    }
}
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
import com.example.bike_app.databinding.FragmentWalkListBinding
import com.example.bike_app.db.Ride
import com.example.bike_app.db.Walk
import com.example.bike_app.presentation.WalkViewModel
import com.example.bike_app.ui.riderv.RideAdapter
import com.example.bike_app.ui.walkrv.WalkAdapter
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalkListFragment :Fragment(){
    lateinit var binding: FragmentWalkListBinding
    private val viewModel: WalkViewModel by viewModel()
    private lateinit var adapter: WalkAdapter
    private lateinit var walkRv: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWalkListBinding.inflate(layoutInflater)
        setupRecyclerView()
        onSwipeDelete()
        viewModel.walks.observe(viewLifecycleOwner){
            if(it != null && it.isNotEmpty()){
                adapter.setWalks(it)
            }
        }
        adapter.setOnItemClickListener(object : WalkAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val Walk: Walk = adapter.getWalk(position)
                val popupWindow = PopupWindow(context)
                val view = layoutInflater.inflate(R.layout.popup_walk,null)
                popupWindow.contentView = view
                val imgView = view.findViewById<ImageView>(R.id.ivMap)
                imgView.setImageBitmap(Walk.img)
                imgView.setOnClickListener {
                    popupWindow.dismiss()
                }
                popupWindow.showAtLocation(walkRv, Gravity.CENTER, 0 ,0)
            }

        })
        return binding.root
    }

    private fun setupRecyclerView() {
        walkRv = binding.rvWalk
        walkRv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        adapter = WalkAdapter()
        binding.rvWalk.adapter = adapter

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
                val walk: Walk = adapter.getWalk(viewHolder.adapterPosition)
                viewModel.deleteWalk(walk)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                Snackbar.make(walkRv, "Deleted", Snackbar.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(walkRv)
    }
}
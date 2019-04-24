package com.example.rover.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rover.R
import com.example.rover.databinding.ListItemRoverCardBinding
import com.example.rover.model.RoverPhoto

class RoverPhotoAdapter(var photos: List<RoverPhoto>) : RecyclerView.Adapter<RoverPhotoAdapter.RoverPhotoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoverPhotoViewHolder {
        val binding = createNewRoverListItemBinding(parent)
        return RoverPhotoViewHolder(binding)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: RoverPhotoViewHolder, position: Int) {
        holder.binding.roverPhoto = photos[position]
    }


    private fun createNewRoverListItemBinding( parent: ViewGroup): ListItemRoverCardBinding {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DataBindingUtil.inflate(layoutInflater, R.layout.list_item_rover_card, parent, false)
    }

    class RoverPhotoViewHolder(val binding: ListItemRoverCardBinding) : RecyclerView.ViewHolder(binding.root)
}
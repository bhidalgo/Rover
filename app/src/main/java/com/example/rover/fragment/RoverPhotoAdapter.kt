package com.example.rover.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rover.R
import com.example.rover.databinding.ListItemRoverCardBinding
import com.example.rover.model.RoverPhoto

class RoverPhotoAdapter(var photos: List<RoverPhoto>, private val onItemClickedListener: View.OnClickListener?) :
    RecyclerView.Adapter<RoverPhotoAdapter.RoverPhotoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoverPhotoViewHolder {
        val binding = createNewRoverListItemBinding(parent)
        return RoverPhotoViewHolder(binding)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: RoverPhotoViewHolder, position: Int) {
        holder.binding.roverImageView.requestLayout()
        holder.binding.roverPhoto = photos[position]
        holder.binding.executePendingBindings()
    }

    private fun createNewRoverListItemBinding(parent: ViewGroup): ListItemRoverCardBinding {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemRoverCardBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_item_rover_card, parent, false)
        binding.root.setOnClickListener(onItemClickedListener)
        return binding
    }

    class RoverPhotoViewHolder(val binding: ListItemRoverCardBinding) : RecyclerView.ViewHolder(binding.root)
}
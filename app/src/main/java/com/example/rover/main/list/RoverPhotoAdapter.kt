package com.example.rover.main.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rover.R
import com.example.rover.databinding.ListItemRoverCardBinding
import com.example.rover.model.RoverPhoto

private const val PHOTO_ITEM_VIEW_TYPE = 0
private const val LOADER_ITEM_VIEW_TYPE = 1

interface RoverPhotoAdapterViewListener : View.OnClickListener {
    var canLoadMorePhotos: Boolean
    fun onListFinished()
}

class RoverPhotoAdapter(var photos: ArrayList<RoverPhoto>, private val listener: RoverPhotoAdapterViewListener?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PHOTO_ITEM_VIEW_TYPE -> {
                val binding = createNewRoverListItemBinding(parent)
                RoverPhotoViewHolder(binding)
            }
            LOADER_ITEM_VIEW_TYPE -> {
                val loaderView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_loading, parent, false)
                LoaderViewHolder(loaderView)
            }
            else -> TODO("Handle invalid view types.")
        }
    }

    override fun getItemCount(): Int = photos.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            PHOTO_ITEM_VIEW_TYPE -> {
                (holder as? RoverPhotoViewHolder)?.apply {
                    binding.roverImageView.requestLayout()
                    binding.roverPhoto = photos[position]
                    binding.executePendingBindings()
                }
            }
            LOADER_ITEM_VIEW_TYPE -> if(listener?.canLoadMorePhotos == true) {
                listener.onListFinished()
            }
        }
    }

    private fun createNewRoverListItemBinding(parent: ViewGroup): ListItemRoverCardBinding {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemRoverCardBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_item_rover_card, parent, false)
        binding.root.setOnClickListener(listener)
        return binding
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position < photos.size -> PHOTO_ITEM_VIEW_TYPE
            position == photos.size -> LOADER_ITEM_VIEW_TYPE
            else -> TODO("Handle invalid view positions.")
        }
    }

    class RoverPhotoViewHolder(val binding: ListItemRoverCardBinding) : RecyclerView.ViewHolder(binding.root)
    class LoaderViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
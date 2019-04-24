package com.example.rover.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imgSrc")
fun loadImage(imageView: ImageView, imgSrc: String?) {
    Picasso.get()
        .load(imgSrc)
        .fit()
        .centerCrop()
        .into(imageView)
}
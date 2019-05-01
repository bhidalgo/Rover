package com.example.rover.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imgSrc")
fun loadImage(imageView: ImageView, imgSrc: String?) {
    val parsedImgSrc = imgSrc?.replace("http:", "https:")

    Picasso.get()
        .load(parsedImgSrc)
        .noFade()
        .into(imageView)
}
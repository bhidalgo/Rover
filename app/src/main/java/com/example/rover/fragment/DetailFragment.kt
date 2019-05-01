package com.example.rover.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.rover.R
import com.example.rover.databinding.FragmentDetailBinding
import com.example.rover.util.enableBackButton
import com.example.rover.util.enableFilterRoverList
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private const val IMG_SRC_KEY = "img_src"
private const val IMG_TITLE_KEY = "img_title"

class DetailFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()
    private lateinit var mBinding: FragmentDetailBinding

    companion object {
        fun newInstance(imgSrc: String?, imgTitle: String): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(IMG_SRC_KEY, imgSrc)
                    putString(IMG_TITLE_KEY, imgTitle)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(IMG_SRC_KEY)?.let {
            loadFullImage(it)
        }
        mBinding.imgTitle = arguments?.getString(IMG_TITLE_KEY)
    }

    override fun onResume() {
        super.onResume()
        enableBackButton(true)
        enableFilterRoverList(false)
    }

    private fun loadFullImage(imgSrc: String) {
        launch {
            val parsedImageSource = imgSrc.replace("http:", "https:")
            val rawBitmap = runCatching {
                Picasso.get().load(parsedImageSource).get()
            }.getOrNull()

            if (rawBitmap == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Could not load image.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            withContext(Dispatchers.Main) {
                val display = resources.displayMetrics
                val screenDensity = resources.displayMetrics.density
                val scaledHeight = (rawBitmap.height * screenDensity).toInt().takeUnless { it <  display.heightPixels} ?: display.heightPixels
                val scaledWidth = (rawBitmap.width * screenDensity).toInt()
                val scaledBitmap = Bitmap.createScaledBitmap(rawBitmap, scaledWidth, scaledHeight, false)
                mBinding.detailImageView.setImageBitmap(scaledBitmap)
            }
            rawBitmap.recycle()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
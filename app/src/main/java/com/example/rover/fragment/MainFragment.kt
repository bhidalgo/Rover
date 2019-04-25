package com.example.rover.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_NONE
import com.example.rover.R
import com.example.rover.activity.MainActivity
import com.example.rover.api.repository.RoverPhotoRepository
import com.example.rover.databinding.FragmentMainBinding
import com.example.rover.util.enableBackButton
import com.example.rover.util.roverComponent
import com.example.rover.viewmodel.MainViewModel
import javax.inject.Inject

class MainFragment : Fragment() {
    private lateinit var mBinding: FragmentMainBinding
    private lateinit var mViewModel: MainViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        roverComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.viewModel = mViewModel
        mBinding.cardGridView.setItemViewCacheSize(100)
        observeRoverPhotos()
    }

    override fun onResume() {
        super.onResume()
        enableBackButton(false)
    }

    @Inject
    fun configureViewModel(roverPhotoRepository: RoverPhotoRepository) {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java).apply {
            this.roverPhotoRepository = roverPhotoRepository
        }

        mViewModel.fetchRoverPhotos()

        observeRoverPhotos()
    }

    private fun observeRoverPhotos() {
        mViewModel.roverPhotos.observe(this, Observer {
            with(mBinding.cardGridView) {
                val currentAdapter: RoverPhotoAdapter? = (adapter as? RoverPhotoAdapter)

                if (currentAdapter == null) {
                    adapter = RoverPhotoAdapter(it, activity as? MainActivity)
                } else {
                    currentAdapter.photos = it
                    currentAdapter.notifyDataSetChanged()
                }
            }
        })
    }
}
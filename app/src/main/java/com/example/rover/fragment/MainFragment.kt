package com.example.rover.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringDef
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rover.R
import com.example.rover.activity.MainActivity
import com.example.rover.activity.navigateToDetailFragment
import com.example.rover.api.CURIOSITY
import com.example.rover.api.OPPORTUNITY
import com.example.rover.api.Rover
import com.example.rover.api.SPIRIT
import com.example.rover.api.repository.RoverPhotoRepository
import com.example.rover.databinding.FragmentMainBinding
import com.example.rover.databinding.ListItemRoverCardBinding
import com.example.rover.util.enableBackButton
import com.example.rover.util.enableFilterRoverList
import com.example.rover.util.roverComponent
import com.example.rover.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import javax.inject.Inject

@StringDef(ONLINE, OFFLINE)
@Retention(AnnotationRetention.SOURCE)
annotation class NetworkState

private const val ONLINE = "online"
private const val OFFLINE = "offline"

class MainFragment : Fragment(), RoverPhotoAdapterViewListener, RoverPickerFragment.RoverPickerCallback {
    private lateinit var mBinding: FragmentMainBinding
    private lateinit var mViewModel: MainViewModel
    private lateinit var mNetworkCallback: NetworkCallback

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    override var canLoadMorePhotos: Boolean = true
        get() {
            return try {
                connectivityManager.activeNetworkInfo?.isConnected == true && field
            } catch (e: Exception) {
                false
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        roverComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? MainActivity)?.supportActionBar?.title = getString(
            when (mViewModel.currentRover) {
                CURIOSITY -> R.string.text_curiosity
                OPPORTUNITY -> R.string.text_opportunity
                SPIRIT -> R.string.text_spirit
                else -> R.string.app_name
            }
        )
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
        enableFilterRoverList(true)
        prepareNetwork()
    }

    override fun onPause() {
        super.onPause()
        connectivityManager.unregisterNetworkCallback(mNetworkCallback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.filter) {
            val roverPickerDialog = RoverPickerFragment()
            roverPickerDialog.show(requireFragmentManager(), "Rover Menu", this)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRoverSelected(@Rover rover: String) {
        mViewModel.roverPhotos.value?.clear()
        mBinding.cardGridView.adapter = null
        mBinding.cardGridView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mViewModel.fetchMostRecentRoverPhotos(rover)
        (requireActivity() as? MainActivity)?.supportActionBar?.title = getString(
            when (rover.toLowerCase()) {
                CURIOSITY -> R.string.text_curiosity
                OPPORTUNITY -> R.string.text_opportunity
                SPIRIT -> R.string.text_spirit
                else -> R.string.app_name
            }
        )
    }

    @Inject
    fun configureViewModel(roverPhotoRepository: RoverPhotoRepository) {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java).apply {
            this.roverPhotoRepository = roverPhotoRepository
            this.connectivityManager = this@MainFragment.connectivityManager
        }

        mViewModel.fetchMostRecentRoverPhotos()

        observeRoverPhotos()
    }

    override fun onClick(v: View?) {
        v?.let {
            val binding = DataBindingUtil.getBinding<ViewDataBinding>(v)
            when (binding) {
                is ListItemRoverCardBinding -> activity?.navigateToDetailFragment(
                    binding.roverPhoto?.imgSrc,
                    binding.roverTextView.text.toString()
                )
                else -> {}
            }
        }
    }

    override fun onListFinished() {
        mViewModel.currentSol.takeUnless { it == 0 }?.let {
            canLoadMorePhotos = false
            mViewModel.fetchRoverPhotos(sol = it - 1, blockScreen = false).invokeOnCompletion {
                canLoadMorePhotos = true
            }
        }
    }

    private fun observeRoverPhotos() {
        mViewModel.roverPhotos.observe(this, Observer {
            with(mBinding.cardGridView) {
                val currentAdapter: RoverPhotoAdapter? = (adapter as? RoverPhotoAdapter)

                if (currentAdapter == null) {
                    adapter = RoverPhotoAdapter(it, this@MainFragment)
                } else {
                    currentAdapter.photos.addAll(it)
                    currentAdapter.photos.distinct()
                    currentAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun prepareNetwork() {
        val networkRequest = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
        mNetworkCallback = NetworkCallback()
        connectivityManager.registerNetworkCallback(networkRequest, mNetworkCallback)
    }

    inner class NetworkCallback : ConnectivityManager.NetworkCallback() {
        @NetworkState
        private var previousState: String = if (connectivityManager.activeNetworkInfo?.isConnected != true) {
            OFFLINE
        } else {
            ONLINE
        }

        override fun onLost(network: Network?) {
            super.onLost(network)
            if (previousState != OFFLINE) {
                Snackbar.make(mBinding.layoutMainFragment, "Internet connection lost.", Snackbar.LENGTH_LONG).show()
                previousState = OFFLINE
            }
        }

        override fun onAvailable(network: Network?) {
            super.onAvailable(network)
            if (previousState != ONLINE) {
                Snackbar.make(mBinding.layoutMainFragment, "Back online!", Snackbar.LENGTH_LONG).show()
                if (mViewModel.roverPhotos.value.isNullOrEmpty()) {
                    mViewModel.fetchMostRecentRoverPhotos()
                } else {
                    mBinding.cardGridView.adapter?.itemCount?.let {
                        runBlocking(Dispatchers.Main) {
                            mBinding.cardGridView.adapter?.notifyItemChanged(it - 1)
                        }
                    }
                }
                previousState = ONLINE
            }
        }
    }
}
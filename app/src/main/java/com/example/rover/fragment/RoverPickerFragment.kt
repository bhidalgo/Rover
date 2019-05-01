package com.example.rover.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.example.rover.R
import com.example.rover.api.Rover
import com.example.rover.databinding.FragmentRoverPickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RoverPickerFragment : BottomSheetDialogFragment() {
    interface RoverPickerCallback {
        fun onRoverSelected(@Rover rover: String)
    }

    private var mCallback: RoverPickerCallback? = null

    private lateinit var mBinding: FragmentRoverPickerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rover_picker, container, false)
        mBinding.fragment = this
        return mBinding.root
    }

    fun show(manager: FragmentManager, tag: String?, callback: RoverPickerCallback?) {
        mCallback = callback
        super.show(manager, tag)
    }

    fun selectRover(@Rover rover: String) {
        dismiss()
        mCallback?.onRoverSelected(rover)
    }
}
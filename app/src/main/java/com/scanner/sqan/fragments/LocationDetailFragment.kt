package com.scanner.sqan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.scanner.sqan.adapter.DevicesAdapter
import com.scanner.sqan.databinding.LocationDetailFragmentBinding
import com.scanner.sqan.models.Progress
import com.scanner.sqan.viewModels.LocationsViewModel

class LocationDetailFragment : Fragment() {
    private lateinit var binding : LocationDetailFragmentBinding
    private val args : LocationDetailFragmentArgs by navArgs()
    private val viewModel: LocationsViewModel by activityViewModels()
    private val devicesAdapter = DevicesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LocationDetailFragmentBinding.inflate(inflater)
        viewModel.getAllRegisteredDevices()
        setViews()
        addObservers()
        addClickListener()

        return binding.root
    }

    private fun setViews() {
        binding.apply {
            tvLocationName.text = args.location.locationName
            tvLocationInfo.text = args.location.locationInfo
            rvRegisteredDevices.adapter = devicesAdapter
        }
    }

    private fun addClickListener() {
        devicesAdapter.onDeviceItemClickListener {
            Toast.makeText(activity,it.deviceDocId,Toast.LENGTH_LONG).show()
        }
    }

    private fun addObservers() {
        binding.apply {
            viewModel.locationsDownloadingProgress.observe(viewLifecycleOwner, {
                when (it) {
                    is Progress.Loading -> {
                        progressBar.isVisible = true
                    }
                    is Progress.Error -> {
                        progressBar.isVisible = false
                        Toast.makeText(activity, it.error, Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        progressBar.isVisible = false
                    }
                }
            })
            viewModel.registeredDevices.observe(viewLifecycleOwner,{list->
                val filteredList =  list.filter {
                    it.deviceLocationId == args.location.fbDocumentId
                }
                devicesAdapter.submitList(filteredList)
            })
        }

    }
}
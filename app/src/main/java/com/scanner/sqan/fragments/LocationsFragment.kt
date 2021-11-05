package com.scanner.sqan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.scanner.sqan.R
import com.scanner.sqan.adapter.LocationAdapter
import com.scanner.sqan.databinding.LocationFragmentBinding
import com.scanner.sqan.models.Progress
import com.scanner.sqan.viewModels.LocationsViewModel

class LocationsFragment:Fragment() {
    private lateinit var binding: LocationFragmentBinding
    private val viewModel : LocationsViewModel by activityViewModels()
    private val locationsAdapter = LocationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LocationFragmentBinding.inflate(inflater)
        binding.rvLocations.apply {
            setHasFixedSize(true)
            adapter = locationsAdapter
        }
        viewModel.getAllLocations()
        addObservers()
        addClickListeners()

        return binding.root
    }

    private fun addClickListeners() {
        locationsAdapter.onLocationItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("location",it)
            }
            findNavController().navigate(R.id.action_locationsFragment_to_locationDetailFragment,bundle)
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
            viewModel.locationsList.observe(viewLifecycleOwner,{
                locationsAdapter.submitList(it)
                tvInfo.isVisible = it.isEmpty()
            })
        }
    }

}
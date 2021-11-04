package com.scanner.sqan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.scanner.sqan.databinding.AddLocationFragmentBinding
import com.scanner.sqan.models.Progress
import com.scanner.sqan.viewModels.LocationsViewModel

class AddLocationFragment : Fragment() {
    private lateinit var binding: AddLocationFragmentBinding
    private val viewModel: LocationsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddLocationFragmentBinding.inflate(inflater)
        addObservers()

        binding.btnAddLocation.setOnClickListener {
            addLocation()
        }

        return binding.root
    }

    private fun addLocation() {
        binding.apply {
            viewModel.addNewLocation(
                etLocation.text.toString(),
                etInfo.text.toString()
            )
        }
    }

    private fun addObservers() {
        binding.apply {
            viewModel.locationAddingProgress.observe(viewLifecycleOwner, {
                when (it) {
                    is Progress.Loading -> {
                        btnAddLocation.isEnabled = false
                        progressBar.isVisible = true
                    }
                    is Progress.Error -> {
                        btnAddLocation.isEnabled = true
                        progressBar.isVisible = false
                        Toast.makeText(activity, it.error, Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        btnAddLocation.isEnabled = true
                        progressBar.isVisible = false
                        Toast.makeText(activity, "Location Added", Toast.LENGTH_LONG).show()
                        etInfo.text?.clear()
                        etLocation.text?.clear()
                    }
                }
            })
        }
    }

}
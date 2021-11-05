package com.scanner.sqan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigationrail.NavigationRailView
import com.scanner.sqan.R
import com.scanner.sqan.databinding.DashboardFragmentBinding
import java.nio.file.DirectoryIteratorException

class DashboardFragment : Fragment() {
    private lateinit var binding: DashboardFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DashboardFragmentBinding.inflate(inflater)
        registerBackPressedCallBack()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            binding.btnAddLocation.setOnClickListener {
                this@DashboardFragment
                    .findNavController()
                    .navigate(DashboardFragmentDirections.actionDashboardFragmentToAddLocationFragment())
            }
        }

        return binding.root
    }

    private fun registerBackPressedCallBack() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
        object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

}
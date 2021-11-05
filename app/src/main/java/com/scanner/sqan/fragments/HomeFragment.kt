package com.scanner.sqan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.scanner.sqan.R
import com.scanner.sqan.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater)

        val navHost = childFragmentManager.findFragmentById(binding.homeFragmentContainer.id) as NavHostFragment
        val controller = navHost.navController
        NavigationUI.setupWithNavController(binding.drawerNavView,controller)

        return binding.root
    }

}
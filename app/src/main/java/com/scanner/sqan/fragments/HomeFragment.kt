package com.scanner.sqan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        val dashboardFragment = DashboardFragment()
        val qrFragment = QrFragment()
        val locationsFragment = LocationsFragment()
        val logsFragment = LogsFragment()
        val settingsFragment = SettingsFragment()


        binding.railNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuDashboard -> {
                    replaceFragment(dashboardFragment)
                    true
                }
                R.id.menuQr ->  {
                    replaceFragment(qrFragment)
                    true
                }
                R.id.menuLocation ->  {
                    replaceFragment(locationsFragment)
                    true
                }
                R.id.menuLogs ->  {
                    replaceFragment(logsFragment)
                    true
                }
                R.id.menuSettings ->  {
                    replaceFragment(settingsFragment)
                    true
                }
                else -> {
                    true
                }
            }
        }

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.homeFragmentContainer, fragment)
            .commit()
    }

}
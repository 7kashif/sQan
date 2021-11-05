package com.scanner.sqan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.scanner.sqan.adapter.LogsAdapter
import com.scanner.sqan.databinding.LogsFragmentBinding
import com.scanner.sqan.models.Progress
import com.scanner.sqan.viewModels.LogsViewModel

class LogsFragment:Fragment() {
    private lateinit var binding: LogsFragmentBinding
    private val viewModel: LogsViewModel by activityViewModels()
    private val logsAdapter= LogsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LogsFragmentBinding.inflate(inflater)
        binding.rvLogs.adapter = logsAdapter
        addObservers()

        return binding.root
    }

    private fun addObservers() {
        binding.apply {
            viewModel.logsList.observe(viewLifecycleOwner,{
                logsAdapter.submitList(it)
            })
            viewModel.progress.observe(viewLifecycleOwner,{
                when(it) {
                    is Progress.Loading -> {
                        progressBar.isVisible= true
                    }
                    is Progress.Error -> {
                        progressBar.isVisible= false
                        Toast.makeText(activity,it.error,Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        progressBar.isVisible= false
                    }
                }
            })
        }
    }

}
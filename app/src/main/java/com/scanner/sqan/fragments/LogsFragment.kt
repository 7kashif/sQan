package com.scanner.sqan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.scanner.sqan.databinding.LogsFragmentBinding

class LogsFragment:Fragment() {
    private lateinit var binding: LogsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LogsFragmentBinding.inflate(inflater)

        return binding.root
    }

}
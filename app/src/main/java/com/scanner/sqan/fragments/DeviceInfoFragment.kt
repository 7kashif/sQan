package com.scanner.sqan.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.scanner.sqan.R
import com.scanner.sqan.databinding.DeviceInfoFragmentBinding
import com.scanner.sqan.models.Progress
import com.scanner.sqan.viewModels.DeviceViewModel
import java.util.*

class DeviceInfoFragment : Fragment() {
    private lateinit var binding: DeviceInfoFragmentBinding
    private val viewModel: DeviceViewModel by activityViewModels()
    private val args: DeviceInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DeviceInfoFragmentBinding.inflate(inflater)
        setViews()
        addObservers()

        return binding.root
    }

    private fun addObservers() {
        binding.apply {
            viewModel.locationName.observe(viewLifecycleOwner,{
                locationId.text = it
            })
            viewModel.deviceUpdatingProgress.observe(viewLifecycleOwner,{
                when(it) {
                    is Progress.Loading -> {
                        progressBar.isVisible = true
                        btnUpdate.isEnabled = false
                    }
                    is Progress.Error -> {
                        progressBar.isVisible = false
                        Toast.makeText(activity,it.error,Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        progressBar.isVisible = false
                        btnUpdate.isEnabled = true
                        Toast.makeText(activity, "Device updated successfully", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    private fun setViews() {
        binding.apply {
            deviceName.text = args.device.deviceName
            etRepairDate.text = args.device.lastRepaired
            etReport.setText(getString(R.string.set_editable_text, args.device.deviceReport))
            etRepairDate.setOnClickListener {
                showDatePickerDialog()
            }
            btnUpdate.setOnClickListener {
                updateDevice()
            }
        }
    }

    private fun updateDevice() {
        binding.apply {
            if (!(etRepairDate.text.isNullOrEmpty() || etReport.text.isNullOrEmpty()))
                viewModel.updateDeviceInfo(etRepairDate.text.toString(), etReport.text.toString())
            else
                Toast.makeText(activity,"Can not update empty fields.",Toast.LENGTH_LONG).show()
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            requireActivity(),
            { _, year, month, dayOfMonth ->
                val monthString = when (month + 1) {
                    1 -> "Jan"
                    2 -> "Fab"
                    3 -> "Mar"
                    4 -> "Apr"
                    5 -> "May"
                    6 -> "Jun"
                    7 -> "Jul"
                    8 -> "Aug"
                    9 -> "Sep"
                    10 -> "Oct"
                    11 -> "Nov"
                    else -> "dec"
                }
                val date = "$dayOfMonth-$monthString-$year"
                binding.etRepairDate.setText(getString(R.string.set_editable_text, date))
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }
}
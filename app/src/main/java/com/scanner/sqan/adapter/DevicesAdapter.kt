package com.scanner.sqan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scanner.sqan.databinding.LocationsListItemBinding
import com.scanner.sqan.models.DeviceModel

class DevicesAdapter: ListAdapter<DeviceModel, DevicesAdapter.DeviceViewHolder>(diffCallBack) {

    inner class DeviceViewHolder(val binding: LocationsListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<DeviceModel>() {
            override fun areItemsTheSame(oldItem: DeviceModel, newItem: DeviceModel): Boolean {
                return oldItem.deviceDocId == newItem.deviceDocId
            }

            override fun areContentsTheSame(oldItem: DeviceModel, newItem: DeviceModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(
            LocationsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.apply {
            holder.itemView.apply {
                tvLocationName.text = item.deviceName
                root.setOnClickListener {
                    deviceClickListener?.let {
                        it(item)
                    }
                }
            }
        }
    }

    private var deviceClickListener: ((DeviceModel) -> Unit)? = null
    fun onDeviceItemClickListener(listener: (DeviceModel) -> Unit) {
        deviceClickListener = listener
    }

}
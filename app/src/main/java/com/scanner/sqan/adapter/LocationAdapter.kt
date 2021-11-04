package com.scanner.sqan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scanner.sqan.databinding.LocationsListItemBinding
import com.scanner.sqan.models.LocationModel

class LocationAdapter : ListAdapter<LocationModel, LocationAdapter.LocationViewHolder>(diffCallBack) {

    inner class LocationViewHolder(val binding: LocationsListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<LocationModel>() {
            override fun areItemsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
                return oldItem.timeStamp == newItem.timeStamp
            }

            override fun areContentsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LocationsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.apply {
            holder.itemView.apply {
                tvLocationName.text = item.locationName
                root.setOnClickListener {
                    locationClickListener?.let {
                        it(item)
                    }
                }
            }
        }
    }

    private var locationClickListener: ((LocationModel) -> Unit)? = null
    fun onLocationItemClickListener(listener: (LocationModel) -> Unit) {
        locationClickListener = listener
    }

}
package com.scanner.sqan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scanner.sqan.R
import com.scanner.sqan.databinding.LogsListItemBinding
import com.scanner.sqan.models.LogsModel
import java.security.acl.LastOwnerException

class LogsAdapter:ListAdapter<LogsModel,LogsAdapter.LogsViewHolder>(diffCallBack) {
    inner class LogsViewHolder(val binding: LogsListItemBinding):RecyclerView.ViewHolder(binding.root)

    companion object {
        private val diffCallBack= object: DiffUtil.ItemCallback<LogsModel>() {
            override fun areItemsTheSame(oldItem: LogsModel, newItem: LogsModel): Boolean {
                return oldItem.docId==newItem.docId
            }

            override fun areContentsTheSame(oldItem: LogsModel, newItem: LogsModel): Boolean {
                return oldItem==newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsViewHolder {
        return LogsViewHolder(
            LogsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LogsViewHolder, position: Int) {
        val item=getItem(position)

        holder.binding.apply {
            holder.itemView.apply {
                tvLog.text = resources.getString(R.string.set_log,item.deviceName,item.locationName,item.userName,item.time)
            }
        }
    }
}
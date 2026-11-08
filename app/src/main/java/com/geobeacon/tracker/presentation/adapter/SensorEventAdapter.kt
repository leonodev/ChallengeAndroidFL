package com.geobeacon.tracker.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geobeacon.tracker.databinding.ItemSensorEventBinding
import com.geobeacon.tracker.presentation.model.SensorEventUiModel

class SensorEventAdapter : ListAdapter<SensorEventUiModel, SensorEventAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemSensorEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SensorEventUiModel) {
            binding.tvTime.text = item.formattedTime
            binding.tvMac.text = item.macAddress
            binding.tvRssi.text = item.rssiText
            binding.tvLatLon.text = item.latLonText
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SensorEventUiModel>() {
        override fun areItemsTheSame(oldItem: SensorEventUiModel, newItem: SensorEventUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SensorEventUiModel, newItem: SensorEventUiModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSensorEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

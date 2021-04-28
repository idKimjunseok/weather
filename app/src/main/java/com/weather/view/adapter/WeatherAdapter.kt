package com.weather.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weather.databinding.ItemHeaderBinding
import com.weather.databinding.ItemWeatherBinding
import com.weather.model.Location

class WeatherAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    private var items = ArrayList<Location>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        return when (viewType) {
            TYPE_HEADER -> HeardViewHolder(ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_ITEM -> CardViewHolder(ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> CardViewHolder(ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is HeardViewHolder -> {}
            is CardViewHolder -> holder.bind(items[position - 1])
        }


    }

    override fun getItemCount(): Int = if (items.size <= 0 ) 0 else items.size + 1

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    fun setItems(items: ArrayList<Location>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    inner class CardViewHolder(
            private val binding: ItemWeatherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Location) {
            binding.apply {
                data = item
            }
        }
    }

    inner class HeardViewHolder(
            private val binding: ItemHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {}

}
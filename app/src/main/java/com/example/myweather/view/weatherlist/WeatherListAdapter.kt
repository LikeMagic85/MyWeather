package com.example.myweather.view.weatherlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.databinding.FragmentWeatherListItemBinding
import com.example.myweather.repository.Weather

class WeatherListAdapter(private var data: List<Weather> = listOf()):RecyclerView.Adapter<WeatherListAdapter.CityHolder>() {

    fun seData(data: List<Weather>){
        this.data = data
        notifyDataSetChanged()
    }

    var onItemClick: ((Weather) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHolder {
        val binding = FragmentWeatherListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CityHolder, position: Int) {

        with(holder){
            bind(data[position])
            itemView.setOnClickListener{
                onItemClick?.invoke(data[position])
            }
        }
    }

    override fun getItemCount() = data.size

    class CityHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(weather: Weather){
            val binding = FragmentWeatherListItemBinding.bind(itemView)
            binding.tvCityName.text = weather.city.name

        }
    }
}
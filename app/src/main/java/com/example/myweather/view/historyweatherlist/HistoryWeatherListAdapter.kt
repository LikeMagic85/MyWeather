package com.example.myweather.view.historyweatherlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.databinding.FragmentHistoryWeatherListRecyclerItemBinding
import com.example.myweather.repository.Weather
import com.example.myweather.view.details.loadSvg

class HistoryWeatherListAdapter(private var data: List<Weather> = listOf()):RecyclerView.Adapter<HistoryWeatherListAdapter.CityHolder>() {

    fun seData(data: List<Weather>){
        this.data = data
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHolder {
        val binding = FragmentHistoryWeatherListRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CityHolder, position: Int) {

        with(holder){
            bind(data[position])
        }
    }

    override fun getItemCount() = data.size

    class CityHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(weather: Weather){
            val binding = FragmentHistoryWeatherListRecyclerItemBinding.bind(itemView)
            binding.tvCityName.text = weather.city.name
            binding.tvTemperature.text = weather.temperature.toString()
            binding.tvFeelsLike.text = weather.feelsLike.toString()
            binding.historyIcon.loadSvg(("https://yastatic.net/weather/i/icons/blueye/color/svg/${weather.icon}.svg"))

        }
    }
}
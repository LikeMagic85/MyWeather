package com.example.myweather.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myweather.databinding.FragmentDetailsBinding
import com.example.myweather.repository.OnResponseServer
import com.example.myweather.repository.Weather
import com.example.myweather.repository.WeatherDTO
import com.example.myweather.repository.WeatherLoader
import com.example.myweather.utils.KEY_BUNDLE_WEATHER

class DetailsFragment : Fragment(), OnResponseServer {

    private var _binding:FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
    get(){
        return _binding!!
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    lateinit var currentCityName:String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<Weather>(KEY_BUNDLE_WEATHER)?.let{
            currentCityName = it.city.name

            WeatherLoader(this@DetailsFragment).loadWeather(it.city.lat, it.city.lon)


        }

    }

    private fun renderData(weatherDTO: WeatherDTO){
        with(binding){
            loadingLayout.visibility = View.GONE
            cityName.text = currentCityName
            temperatureValue.text = weatherDTO.factDTO.temp.toString()
            feelsLikeValue.text = weatherDTO.factDTO.feelsLike.toString()
            cityCoordinates.text = "${weatherDTO.infoDTO.lat} ${weatherDTO.infoDTO.lon}"
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onResponse(weatherDTO: WeatherDTO) {
            renderData(weatherDTO)
    }
}
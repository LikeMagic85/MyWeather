package com.example.myweather.view.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import com.example.myweather.databinding.FragmentDetailsBinding
import com.example.myweather.repository.Weather
import com.example.myweather.utils.KEY_BUNDLE_WEATHER
import com.example.myweather.viewmodel.AppError
import com.example.myweather.viewmodel.DetailsState
import com.example.myweather.viewmodel.DetailsViewModel
import com.google.android.material.snackbar.Snackbar


class DetailsFragment : Fragment() {

    private var _binding:FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
    get(){
        return _binding!!
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }


    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this)[DetailsViewModel::class.java]
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLiveData().observe(viewLifecycleOwner) { weather -> renderData(weather) }
        arguments?.getParcelable<Weather>(KEY_BUNDLE_WEATHER)?.let{
            viewModel.getWeather(it.city)
        }
    }

    private fun renderData(detailsState: DetailsState){
       when(detailsState){
           is DetailsState.Error -> {
               if(detailsState.error is AppError.Error1){
                    Snackbar.make(binding.root, "Ошибка у клиента", Snackbar.LENGTH_SHORT).show()
                    binding.loadingLayout.visibility = View.GONE
               }else if(detailsState.error is AppError.Error2){
                   Snackbar.make(binding.root, "Ошибка на сервере", Snackbar.LENGTH_SHORT).show()
                   binding.loadingLayout.visibility = View.GONE
               }
           }
           DetailsState.Loading -> {
               binding.loadingLayout.visibility = View.VISIBLE
           }
           is DetailsState.Success -> {
               with(binding){
                   val weather = detailsState.weather
                   loadingLayout.visibility = View.GONE
                   cityName.text = weather.city.name
                   temperatureValue.text = weather.temperature.toString()
                   feelsLikeValue.text = weather.feelsLike.toString()
                   cityCoordinates.text = "${weather.city.lat}   ${weather.city.lon}"
                   timeValue.text = weather.time

                   /*Glide.with(requireContext())
                        .load("https://freepngimg.com/thumb/city/36275-3-city-hd.png")
                        .into(headerIcon)*/

                   /* Picasso.get()?.load("https://freepngimg.com/thumb/city/36275-3-city-hd.png")
                        ?.into(headerIcon)*/

                   headerCityIcon.load("https://freepngimg.com/thumb/city/36275-3-city-hd.png")
                   icon.loadSvg("https://yastatic.net/weather/i/icons/blueye/color/svg/${weather.icon}.svg")
               }
           }
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

}


fun ImageView.loadSvg(url:String){
    val imageLoader = ImageLoader.Builder(this.context)
        .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
        .build()
    val request = ImageRequest.Builder(this.context)
        .crossfade(true)
        .crossfade(500)
        .data(url)
        .target(this)
        .build()
    imageLoader.enqueue(request)
}

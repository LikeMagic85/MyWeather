package com.example.myweather

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myweather.databinding.FragmentMapsMainBinding
import com.example.myweather.repository.City
import com.example.myweather.repository.Weather
import com.example.myweather.utils.KEY_BUNDLE_WEATHER
import com.example.myweather.view.details.DetailsFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class MapsFragment : Fragment() {

    private val markers: ArrayList<Marker> = arrayListOf()
    private lateinit var map: GoogleMap
    lateinit var searchText:String
    private var _binding: FragmentMapsMainBinding? = null
    private val binding: FragmentMapsMainBinding
        get() {
            return _binding!!
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private lateinit  var weatherButton: FloatingActionButton

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.setOnMapLongClickListener {
            addMarkerToArray(it)
            drawLine()
        }
        searchText = "Moscow"
        map.uiSettings.isZoomControlsEnabled = true
        val moscow = LatLng(55.755826, 37.617299900000035)
        googleMap.addMarker(MarkerOptions().position(moscow).title("Marker in Moscow"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(moscow,5f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsMainBinding.inflate(inflater, container, false)
        weatherButton = binding.floatingActionButton
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        findCity()
        weatherButton.visibility = View.GONE
    }

    private fun findCity() {
        binding.buttonSearch.setOnClickListener { v ->
            v.hideKeyboard()
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            searchText = binding.searchAddress.text.toString()
            if(searchText.isNotBlank()){
                if(searchText.all{it.isLetter()}){
                    if(geocoder.getFromLocationName(searchText, 1).size!=0){
                        geocoder.getFromLocationName(searchText, 1)[0]?.let {
                            val lat = it.latitude
                            val lon = it.longitude
                            map.addMarker(
                                MarkerOptions().position(LatLng(lat, lon)).title(searchText)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                            )
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon),10f))
                            weatherButton.visibility = View.VISIBLE
                            getWeatherFromMap(searchText,lat,lon)
                        }
                    }else{Toast.makeText(requireContext(),"Ничего не найдено",Toast.LENGTH_SHORT).show()
                        binding.searchAddress.text.clear()
                    }
                }else{ Toast.makeText(requireContext(),"Только буквы",Toast.LENGTH_SHORT).show()
                    binding.searchAddress.text.clear()
                }
            }else{ Toast.makeText(requireContext(),"Не может быть пустой запрос",Toast.LENGTH_SHORT).show()
                binding.searchAddress.text.clear()
            }
        }

    }

    private fun getWeatherFromMap(name:String,lat:Double,lon:Double){
        weatherButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putParcelable(KEY_BUNDLE_WEATHER, Weather(
                City(name,lat,lon)
            ))
            requireActivity().supportFragmentManager
                .beginTransaction()
                .addToBackStack("")
                .add(R.id.main_container, DetailsFragment.newInstance(bundle))
                .commit()
            weatherButton.visibility = View.GONE
        }

    }

    private fun addMarkerToArray(location: LatLng) {
        val marker = setMarker(location, markers.size.toString(), R.drawable.ic_map_pin)
        markers.add(marker)
    }

    private fun drawLine(){
        var previousMarker: Marker? = null
        markers.forEach { current->
            previousMarker?.let{  previous->
                map.addPolyline(
                    PolylineOptions().add(previous.position,current.position)
                        .color(Color.RED)
                        .width(5f))
            }
            previousMarker = current
        }
    }




    private fun setMarker(
        location: LatLng,
        searchText: String,
        resourceId: Int
    ): Marker {
        return map.addMarker(
            MarkerOptions()
                .position(location)
                .title(searchText)
                .icon(BitmapDescriptorFactory.fromResource(resourceId))
        )!!
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}
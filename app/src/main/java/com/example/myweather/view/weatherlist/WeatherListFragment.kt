package com.example.myweather.view.weatherlist

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.R
import com.example.myweather.databinding.FragmentWeatherListBinding
import com.example.myweather.repository.City
import com.example.myweather.repository.Weather
import com.example.myweather.utils.COUNTRY
import com.example.myweather.utils.IS_RUSSIAN
import com.example.myweather.utils.KEY_BUNDLE_WEATHER
import com.example.myweather.view.details.DetailsFragment
import com.example.myweather.viewmodel.AppState
import com.example.myweather.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*

class WeatherListFragment : Fragment() {

    private var _binding: FragmentWeatherListBinding? = null
    private val binding: FragmentWeatherListBinding
        get() {
            return _binding!!
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private val adapter = WeatherListAdapter()

    private var isRussian = true

    private lateinit var sp:SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = requireActivity().getSharedPreferences(COUNTRY, Context.MODE_PRIVATE)
        editor = sp.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val country = sp.getBoolean(IS_RUSSIAN, false)
        isRussian = country
        binding.recyclerView.adapter = adapter
        val observer = Observer<AppState> { data -> renderData(data) }
        setContent(country, observer)
        setupFub()
        setupLoc()
        setOnAdapterClick()
    }

    private fun setupLoc() {
        binding.mainFragmentFABLocation.setOnClickListener{
            checkPermission()
        }
    }

    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            getLocation()
        }else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
            explain()
        }else {
            mRequestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 998){
            for(i in permissions.indices){
                if(permissions[i]==Manifest.permission.ACCESS_FINE_LOCATION&&grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    getLocation()
                }else{
                    explain()
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }


    private val locationListener = LocationListener {
        getAddressByLocation(it)
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        context?.let {
            val locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                val providerGPS = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                providerGPS?.let {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        10000L,
                        100f,
                        locationListener
                    )
                }
            }
        }
    }

    private fun getAddressByLocation(location: Location){
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        Thread{
            val addressText = geocoder.getFromLocation(location.latitude, location.longitude, 1)[0].getAddressLine(0)
            requireActivity().runOnUiThread{
                showAddressDialog(addressText, location)
            }
        }.start()

    }

    private fun explain(){
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_rationale_title)
            .setMessage(R.string.dialog_rationale_message)
            .setPositiveButton(R.string.dialog_rationale_give_access){_, _ ->
                mRequestPermission()
            }
            .setNegativeButton(R.string.dialog_rationale_decline){dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun mRequestPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),998)
    }



    private fun setupFub() {
        binding.floatingActionButton.setOnClickListener {
            isRussian = !isRussian
            if (isRussian) {
                editor.putBoolean(IS_RUSSIAN, true)
                editor.apply()
                viewModel.getWeatherRussian()
                binding.floatingActionButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_russia
                    )
                )
            } else {
                viewModel.getWeatherWorld()
                editor.putBoolean(IS_RUSSIAN, false)
                editor.apply()
                binding.floatingActionButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_earth
                    )
                )
            }
        }
    }

    private fun setOnAdapterClick() {
        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putParcelable(KEY_BUNDLE_WEATHER, it)
            requireActivity().supportFragmentManager
                .beginTransaction()
                .addToBackStack("")
                .add(R.id.main_container, DetailsFragment.newInstance(bundle))
                .commit()
        }
    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.Error -> {
                with(binding) {
                    loadingLayout.visibility = View.GONE
                    root.showSnackBar("Не получилось ${data.error}")
                }
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Success -> {
                binding.loadingLayout.visibility = View.GONE
                adapter.seData(data.weatherData)
            }
        }


    }

    private fun View.showSnackBar(s: String) {
        Snackbar.make(this, s, Snackbar.LENGTH_LONG).show()
    }

    private fun setContent(country: Boolean, observer: Observer<AppState>){
        with(viewModel) {
            getData().observe(viewLifecycleOwner, observer)
            if (country){
                getWeatherRussian()
                binding.floatingActionButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_russia
                    )
                )
            }else {
                getWeatherWorld()
                binding.floatingActionButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_earth
                    )
                )
            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = WeatherListFragment()
    }

    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_address_title))
                .setMessage(address)
                .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ ->
                    openDetailsFragment(
                        Weather(
                            City(
                                address.substringAfter(','),
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                }
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun openDetailsFragment(weather: Weather) {
        val bundle = Bundle()
        bundle.putParcelable(KEY_BUNDLE_WEATHER, weather)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .addToBackStack("")
            .add(R.id.main_container, DetailsFragment.newInstance(bundle))
            .commit()
    }
}
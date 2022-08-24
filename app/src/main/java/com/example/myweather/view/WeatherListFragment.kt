package com.example.myweather.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.R
import com.example.myweather.databinding.FragmentWeatherListBinding
import com.example.myweather.utils.KEY_BUNDLE_WEATHER
import com.example.myweather.viewmodel.AppState
import com.example.myweather.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class WeatherListFragment : Fragment() {

    private var _binding: FragmentWeatherListBinding? = null
    private val binding: FragmentWeatherListBinding
        get() {
            return _binding!!
        }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val adapter = WeatherListAdapter()
    private var isRussian = true

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
        _binding = FragmentWeatherListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        val observer = Observer<AppState> { data -> renderData(data) }
        with(viewModel) {
            getData().observe(viewLifecycleOwner, observer)
            getWeatherRussian()
        }
        setupFub()
        setOnAdapterClick()
    }

    private fun setupFub() {
        binding.floatingActionButton.setOnClickListener {
            isRussian = !isRussian
            if (isRussian) {
                viewModel.getWeatherRussian()
                binding.floatingActionButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_russia
                    )
                )
            } else {
                viewModel.getWeatherWorld()
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

    companion object {

        @JvmStatic
        fun newInstance() = WeatherListFragment()
    }
}
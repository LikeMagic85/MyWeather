package com.example.myweather.view.historyweatherlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.databinding.FragmentHistoryWeatherListBinding
import com.example.myweather.viewmodel.AppState
import com.example.myweather.viewmodel.HistoryViewModel
import com.google.android.material.snackbar.Snackbar

class HistoryWeatherListFragment : Fragment() {

    private var _binding: FragmentHistoryWeatherListBinding? = null
    private val binding: FragmentHistoryWeatherListBinding
        get() {
            return _binding!!
        }

    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    private val adapter = HistoryWeatherListAdapter()


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
        _binding = FragmentHistoryWeatherListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        val observer = Observer<AppState> { data -> renderData(data) }
        viewModel.getData().observe(viewLifecycleOwner, observer)
        viewModel.getAll()

    }

    private fun renderData(data: AppState) {
        when (data) {
            is AppState.Error -> {
                with(binding) {
                    root.showSnackBar("Не получилось ${data.error}")
                }
            }
            is AppState.Loading -> {

            }
            is AppState.Success -> {
                adapter.seData(data.weatherData)
            }
        }


    }

    private fun View.showSnackBar(s: String) {
        Snackbar.make(this, s, Snackbar.LENGTH_LONG).show()
    }



    companion object {

        @JvmStatic
        fun newInstance() = HistoryWeatherListFragment()
    }
}
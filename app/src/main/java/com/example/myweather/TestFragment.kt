package com.example.myweather

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startForegroundService


class TestFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val launcherLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                requireActivity().supportFragmentManager.popBackStack()
                startForegroundService(requireContext(),Intent(requireContext(), MyGeofensService::class.java))
            }
        }
        launcherLocation.launch(ACCESS_BACKGROUND_LOCATION)
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() = TestFragment()
    }
}
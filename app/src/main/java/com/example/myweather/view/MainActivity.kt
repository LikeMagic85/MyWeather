package com.example.myweather.view

import android.Manifest
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myweather.*
import com.example.myweather.fcm.NotificationService
import com.example.myweather.view.historyweatherlist.HistoryWeatherListFragment
import com.example.myweather.view.weatherlist.WeatherListFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_container, WeatherListFragment.newInstance())
                .addToBackStack("")
                .commit()
        }
        launcherStorage.launch(WRITE_EXTERNAL_STORAGE)
        checkPermission()

        /**получение токена Fire BaseMessaging**/

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("mylogs_push", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("mylogs_push", "$token")
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_history->{
                supportFragmentManager
                    .beginTransaction()
                    .addToBackStack("")
                    .add(R.id.main_container, HistoryWeatherListFragment.newInstance())
                    .commit()
            }
            R.id.action_WWCP->{
                supportFragmentManager
                    .beginTransaction()
                    .addToBackStack("")
                    .add(R.id.main_container, WorkWithContentProviderFragment.newInstance())
                    .commit()
            }
            R.id.action_menu_google_maps->{
                supportFragmentManager
                    .beginTransaction()
                    .addToBackStack("")
                    .add(R.id.main_container, MapsFragment())
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NewApi")
    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, MyGeofensService::class.java))
            }
        }else if(shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
            explain()
        }
        else {
            requestPermissionFineLocation()
        }
    }

    private fun explain(){
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_rationale_title)
            .setMessage(R.string.dialog_rationale_message)
            .setPositiveButton(R.string.dialog_rationale_give_access){_, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissionFineLocation()
                }
            }
            .setNegativeButton(R.string.dialog_rationale_decline){dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }



    val launcherLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                supportFragmentManager
                    .beginTransaction()
                    .addToBackStack("")
                    .add(R.id.main_container, TestFragment.newInstance())
                    .commit()
            }
        }
    }
    private fun requestPermissionFineLocation() {
        launcherLocation.launch(ACCESS_FINE_LOCATION)
    }


        val launcherStorage = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                requestPermissionFineLocation()
            }
        }


}
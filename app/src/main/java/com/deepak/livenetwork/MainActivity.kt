package com.deepak.livenetwork

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NetworkLiveData.observe(this, Observer {
            if (it) {
                Toast.makeText(this, "Connection available", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Connection gone", Toast.LENGTH_SHORT).show()
            }
        })
        Log.d("connected", NetworkLiveData.isNetworkAvaiable().toString())
        Log.d("connectiontype", NetworkLiveData.getConnectionType().toString())
        CoroutineScope(Dispatchers.IO).launch {
            // runs on UI thread
            Log.d(
                    "is connection Reachable",
                    NetworkLiveData.isInternetReachable("https://www.google.com").toString()
            )
        }
    }
}

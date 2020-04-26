# LiveNetwork

A small network utility library which help you to detect changes in connection using livedata

Just download a code and add livenetwork as glide module in application 

to use this 

```
//To detect network changes
NetworkLiveData.observe(this, Observer {
    if (it) {
        Toast.makeText(this, "Connection available", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, "Connection gone", Toast.LENGTH_SHORT).show()
    }
})
//To get current network status 
Log.d(“connected”, NetworkLiveData.isNetworkAvaiable().toString())
//To get current network type
Log.d(“connection type”,NetworkLiveData.getConnectionType().toString)
//To check connected network is reachable or not
//Add this dependencies
/*implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5"*/
CoroutineScope(Dispatchers.IO).launch {
 // runs on UI thread
 Log.d(
 “is connection Reachable”,
 NetworkLiveData.isInternetReachable(“https://www.google.com").toString()
 )
}
```

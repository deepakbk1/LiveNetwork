package com.deepak.livenetwork

import android.app.Application

class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        NetworkLiveData.init(this)
    }

}

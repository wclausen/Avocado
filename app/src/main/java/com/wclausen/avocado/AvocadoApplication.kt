package com.wclausen.avocado

import android.app.Application
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxViewModelConfigFactory

class AvocadoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MvRx.viewModelConfigFactory = MvRxViewModelConfigFactory(this)
    }
}
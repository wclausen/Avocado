package com.wclausen.avocado.base

import android.app.Application
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRxViewModelConfigFactory
import com.wclausen.avocado.di.AppComponent

class AvocadoApplication : Application() {

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        MvRx.viewModelConfigFactory = MvRxViewModelConfigFactory(this)
        INSTANCE = this
    }

    companion object {
        private lateinit var INSTANCE: AvocadoApplication

        fun injector() = INSTANCE.component
    }
}
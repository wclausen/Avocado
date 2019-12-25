package com.wclausen.avocado

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as AvocadoApplication).component = DaggerAppComponent
            .builder()
            .androidModule(AndroidModule(this))
            .build()
        setContentView(R.layout.activity_main)
    }
}

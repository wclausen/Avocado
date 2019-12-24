package com.wclausen.avocado

import android.content.Context
import com.airbnb.mvrx.BaseMvRxFragment

abstract class InjectedFragment(layoutRes: Int) : BaseMvRxFragment(layoutRes) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject()
    }

    abstract fun inject()

}
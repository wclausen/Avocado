package com.wclausen.avocado

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState

open class AvocadoViewModel<S : MvRxState>(state: S) : BaseMvRxViewModel<S>(state)
package com.wclausen.avocado.base

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState

open class AvocadoViewModel<S : MvRxState>(state: S) : BaseMvRxViewModel<S>(state)
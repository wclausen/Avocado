package com.wclausen.avocado

import com.airbnb.mvrx.test.MvRxTestRule
import org.junit.ClassRule

open class MvRxTest {

    companion object {
        @JvmField
        @ClassRule
        val mvrxRule = MvRxTestRule()
    }

}

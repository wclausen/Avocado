package com.wclausen.avocado

import com.airbnb.mvrx.*
import kotlinx.android.synthetic.main.contacts_fragment.*

data class Person(val name: String)
data class ContactsState(val contacts: Async<List<Person>> = Uninitialized) : MvRxState

class ContactsViewModel(initialState: ContactsState) :
    AvocadoViewModel<ContactsState>(initialState) {
}

class ContactsFragment : BaseMvRxFragment(R.layout.contacts_fragment) {
    private val viewModel: ContactsViewModel by fragmentViewModel()
    override fun invalidate() = withState(viewModel) { state ->
        helloWorld.text = state.contacts()?.get(0)?.name
    }

}
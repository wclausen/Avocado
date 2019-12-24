package com.wclausen.avocado

import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.wclausen.avocado.AvocadoApplication.Companion.injector
import kotlinx.android.synthetic.main.contacts_fragment.*
import javax.inject.Inject

data class Person(val name: String)
data class ContactsState(val contacts: Async<List<Person>> = Uninitialized) : MvRxState

class ContactsViewModel @AssistedInject constructor(@Assisted initialState: ContactsState, private val contactsRepo: ContactsRepository) :
    AvocadoViewModel<ContactsState>(initialState) {

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: ContactsState): ContactsViewModel
    }

    companion object : MvRxViewModelFactory<ContactsViewModel, ContactsState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: ContactsState
        ): ContactsViewModel? {
            val fragment =
                (viewModelContext as FragmentViewModelContext).fragment<ContactsFragment>()
            return fragment.viewModelFactory.create(state)
        }
    }
}

class ContactsFragment : InjectedFragment(R.layout.contacts_fragment) {
    @Inject
    lateinit var viewModelFactory: ContactsViewModel.Factory
    private val viewModel: ContactsViewModel by fragmentViewModel()

    override fun inject() {
        injector().inject(this)
    }


    override fun invalidate() = withState(viewModel) { state ->
        helloWorld.text = state.contacts()?.get(0)?.name
    }

}
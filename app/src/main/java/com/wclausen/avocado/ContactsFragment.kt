package com.wclausen.avocado

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.afollestad.assent.Permission
import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.wclausen.avocado.AvocadoApplication.Companion.injector
import kotlinx.android.synthetic.main.contacts_fragment.*
import javax.inject.Inject

data class Person(val name: String)
data class ContactsState(val contacts: Async<List<Person>> = Uninitialized) : MvRxState

class ContactsViewModel @AssistedInject constructor(
    @Assisted initialState: ContactsState,
    private val contactsRepo: ContactsRepository,
    private val permissionManager: PermissionManager
) :
    AvocadoViewModel<ContactsState>(initialState) {

    fun fetchContacts(searchName: String) {
        if (permissionManager.hasPermission(Permission.READ_CONTACTS)) {
            contactsRepo.fetchContacts(searchName).execute { copy(it) }
        } else {
            permissionManager.requestPermission(Permission.READ_CONTACTS) {
                if (it.grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contactsRepo.fetchContacts(searchName).execute { copy(it) }
                } else {
                    setState { copy(contacts = Fail<List<Person>>(IllegalStateException("Unable to search contacts, need permission"))) }
                }
            }
        }
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        search_button.setOnClickListener {
            viewModel.fetchContacts(search_text_field.text.toString())
        }
    }

    override fun invalidate() = withState(viewModel) { state ->
        helloWorld.text = state.contacts()?.get(0)?.name
    }

}
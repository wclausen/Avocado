package com.wclausen.avocado.addcontact

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.afollestad.assent.Permission
import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.wclausen.avocado.ContactsRepository
import com.wclausen.avocado.R
import com.wclausen.avocado.base.AvocadoApplication.Companion.injector
import com.wclausen.avocado.base.AvocadoViewModel
import com.wclausen.avocado.base.InjectedFragment
import com.wclausen.avocado.permissions.PermissionManager
import kotlinx.android.synthetic.main.contacts_fragment.*
import kotlinx.android.synthetic.main.contacts_list.*
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
        contacts_list.adapter = ArrayAdapter<String>(
            activity!!,
            R.layout.contacts_list_item,
            R.id.contact_name
        )
    }

    override fun invalidate() = withState(viewModel) { state ->
        when (state.contacts) {
            Uninitialized -> {
            }
            is Loading -> {
            }
            is Success -> {
                contacts_list.adapter = ArrayAdapter<String>(
                    view!!.context,
                    R.layout.contacts_list_item,
                    R.id.contact_name,
                    state.contacts()!!.map { it.name })
            }
            is Fail -> {
            }
        }
    }

}
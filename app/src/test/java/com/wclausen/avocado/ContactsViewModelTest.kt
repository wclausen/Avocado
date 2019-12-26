package com.wclausen.avocado

import android.content.pm.PackageManager
import com.afollestad.assent.AssentResult
import com.afollestad.assent.Permission
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.withState
import com.google.common.truth.Truth.assertThat
import com.wclausen.avocado.addcontact.ContactsState
import com.wclausen.avocado.addcontact.ContactsViewModel
import com.wclausen.avocado.addcontact.Person
import com.wclausen.avocado.permissions.PermissionManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Single
import org.junit.Test

class ContactsViewModelTest : MvRxTest() {

    private val initialState = ContactsState()
    private val contactsRepository = mockk<ContactsRepository>(relaxed = true)
    private val permissionManager = mockk<PermissionManager>(relaxed = true)
    private val contactsViewModel =
        ContactsViewModel(
            initialState,
            contactsRepository,
            permissionManager
        )

    @Test
    fun `GIVEN read contacts permission not granted WHEN fetching contacts THEN request permission`() {
        every { permissionManager.hasPermission(any()) } returns false

        contactsViewModel.fetchContacts("")

        verify { permissionManager.requestPermission(eq(Permission.READ_CONTACTS), any()) }
    }

    @Test
    fun `GIVEN read contacts permission is granted WHEN fetching contacts THEN fetch contacts`() {
        every { contactsRepository.fetchContacts(any()) } returns Single.just(listOf("Elon").map {
            Person(
                it
            )
        })
        every { permissionManager.hasPermission(any()) } returns true

        contactsViewModel.fetchContacts("Elon")

        verify { contactsRepository.fetchContacts("Elon") }

        withState(contactsViewModel) {
            assertThat(it.contacts.invoke()!![0].name).isEqualTo("Elon")
        }
    }

    @Test
    fun `GIVEN read contacts permission denied WHEN fetching contacts THEN output fail state`() {
        val captureCallback = slot<(AssentResult) -> Unit>()
        every { permissionManager.hasPermission(any()) } returns false
        every { permissionManager.requestPermission(any(), capture(captureCallback)) } answers {
            captureCallback.captured(
                AssentResult(
                    listOf(Permission.READ_CONTACTS),
                    intArrayOf(PackageManager.PERMISSION_DENIED)
                )
            )
        }

        contactsViewModel.fetchContacts("Elon")

        withState(contactsViewModel) {
            assertThat(it.contacts).isInstanceOf(Fail::class.java)
        }

    }
}
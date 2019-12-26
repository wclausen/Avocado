package com.wclausen.avocado

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.wclausen.avocado.addcontact.Person
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.collections.HashSet


/*
 * Defines an array that contains column names to move from
 * the Cursor to the ListView.
 */
@SuppressLint("InlinedApi")
private val FROM_COLUMNS: Array<String> = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

/*
 * Defines an array that contains resource ids for the layout views
 * that get the Cursor column contents. The id is pre-defined in
 * the Android framework, so it is prefaced with "android.R.id"
 */
private val TO_IDS: IntArray = intArrayOf(R.id.contact_name)

@SuppressLint("InlinedApi")
private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
)

// The column index for the _ID column
private const val CONTACT_ID_INDEX: Int = 0
// The column index for the CONTACT_KEY column
private const val CONTACT_KEY_INDEX: Int = 1
private const val CONTACT_NAME_INDEX: Int = 2

// Defines the text expression
@SuppressLint("InlinedApi")
private val SELECTION: String =
    "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"


class ContactsRepository @Inject constructor(
    val activityProvider: Provider<MainActivity>
) {

    private val contactsObservers: MutableSet<SingleEmitter<List<Person>>> =
        Collections.synchronizedSet(HashSet<SingleEmitter<List<Person>>>())
    // Defines the array to hold values that replace the ?
    private val selectionArgs = arrayOf("")

    companion object {
        private const val LOADER_ID = 787
    }

    fun fetchContacts(searchName: String): Single<List<Person>> {
        selectionArgs[0] = "%$searchName%"
        @Suppress("DEPRECATION")
        activityProvider.get().supportLoaderManager.restartLoader<Cursor>(LOADER_ID, null,
            object : LoaderManager.LoaderCallbacks<Cursor> {
                override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
                    return CursorLoader(
                        activityProvider.get(),
                        ContactsContract.Contacts.CONTENT_URI,
                        PROJECTION,
                        SELECTION,
                        selectionArgs,
                        null
                    )
                }

                override fun onLoaderReset(loader: Loader<Cursor>) {
                }

                override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
                    val names = mutableListOf<String>()
                    if (cursor.moveToFirst()) {
                        do {
                            names.add(cursor.getString(CONTACT_NAME_INDEX))
                        } while (cursor.moveToNext())
                    }
                    contactsObservers.map {
                        it.onSuccess(names.map {
                            Person(
                                it
                            )
                        })
                    }
                }
            })

        return Single.create<List<Person>> { it ->
            it.setCancellable {
                contactsObservers.remove(it)
            }
            contactsObservers.add(it)
        }
    }

}

package com.wclausen.avocado

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.afollestad.assent.Permission
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit
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
    val activityProvider: Provider<MainActivity>,
    val permissionsManager: PermissionManager
) {

    private val contactsObservers: MutableSet<ObservableEmitter<List<Person>>> =
        Collections.synchronizedSet(HashSet<ObservableEmitter<List<Person>>>())
    private val contactsObservable = Observable.create<List<Person>> { observer ->
        contactsObservers.add(observer)
        observer.setCancellable { contactsObservers.remove(observer) }
    }
    // Defines the array to hold values that replace the ?
    private val selectionArgs = arrayOf("")

    companion object {
        private const val LOADER_ID = 787
    }

    init {
        if (permissionsManager.hasPermission(Permission.READ_CONTACTS)) {
            activityProvider.get().supportLoaderManager.initLoader<Cursor>(LOADER_ID, null,
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
                        cursor.close()
                        contactsObservers.map {
                            it.onNext(names.map { Person(it) })
                        }
                    }

                })
        }
    }


    fun fetchContacts(searchName: String): Single<List<Person>> {
        if (!permissionsManager.hasPermission(Permission.READ_CONTACTS)) {
            return Single.just(emptyList())
        }
        return Single.just(listOf("Elon Musk", "Abraham Lincoln").map { Person(it) })
            .delay(2, TimeUnit.SECONDS)
    }


}

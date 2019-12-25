package com.wclausen.avocado

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import javax.inject.Inject


class PermissionManager @Inject constructor(val activity: MainActivity) {

    enum class Permission(val permissionName: String, val requestCode: Int) {
        READ_CONTACTS(Manifest.permission.READ_CONTACTS, 23232)
    }

    enum class PermissionRequestResult {
        PERMISSION_GRANTED,
        PERMISSION_DENIED,
        SHOW_EXPLANATION,
        AWAITING_RESPONSE
    }

    fun hasPermission(permissionName: String) = ContextCompat.checkSelfPermission(
        activity,
        permissionName
    ) == PackageManager.PERMISSION_GRANTED

    fun requestPermission(
        permission: Permission,
        permissionRequestCallback: (Array<out String>, IntArray) -> Unit
    ): PermissionRequestResult {
        return PermissionRequestResult.PERMISSION_DENIED
    }

}

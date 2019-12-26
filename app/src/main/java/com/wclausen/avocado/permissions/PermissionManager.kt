package com.wclausen.avocado.permissions

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.afollestad.assent.AssentResult
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.wclausen.avocado.MainActivity
import javax.inject.Inject


class PermissionManager @Inject constructor(val activity: MainActivity) {

    fun hasPermission(permission: Permission) = ContextCompat.checkSelfPermission(
        activity,
        permission.value
    ) == PackageManager.PERMISSION_GRANTED

    fun requestPermission(
        permission: Permission,
        permissionRequestCallback: (AssentResult) -> Unit
    ) {
        activity.askForPermissions(permission) { result -> permissionRequestCallback(result) }
    }

}

package dev.krishna.bluetoothchatapplication.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

fun hasPermission(context: Context,permission: String): Boolean{
    val isPermissionGranted = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    Log.d("Permission Handler","$permission granted = $isPermissionGranted")
    return isPermissionGranted
}
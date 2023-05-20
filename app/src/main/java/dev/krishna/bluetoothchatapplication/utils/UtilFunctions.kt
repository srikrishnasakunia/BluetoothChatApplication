package dev.krishna.bluetoothchatapplication.utils

import android.content.Context
import android.content.pm.PackageManager

fun hasPermission(context: Context,permission: String): Boolean{
    return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}
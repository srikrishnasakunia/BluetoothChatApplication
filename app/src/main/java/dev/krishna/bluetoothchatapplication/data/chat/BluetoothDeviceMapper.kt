package dev.krishna.bluetoothchatapplication.data.chat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain{
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}
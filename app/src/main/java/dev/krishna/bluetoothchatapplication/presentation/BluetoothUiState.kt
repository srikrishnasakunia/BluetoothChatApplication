package dev.krishna.bluetoothchatapplication.presentation

import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothDeviceDomain

data class BluetoothUiState(
    val scannedDevice: List<BluetoothDeviceDomain> = emptyList(),
    val pairedDevice: List<BluetoothDeviceDomain> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null
)

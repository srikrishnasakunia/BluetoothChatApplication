package dev.krishna.bluetoothchatapplication.domain.chat

data class BluetoothMessage(
    val message: String,
    val senderName: String,
    val isFromLocalSender: Boolean,
    val timestamp: String
)

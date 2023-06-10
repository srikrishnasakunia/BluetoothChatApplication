package dev.krishna.bluetoothchatapplication.presentation.components

import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothMessage

object BluetoothSampleMessage {
    val messages = listOf<BluetoothMessage>(
        getMessage(1), getSecondMessage(2), getMessage(3), getSecondMessage(4),
        getMessage(5), getSecondMessage(6), getMessage(7), getSecondMessage(8),
        getMessage(9), getSecondMessage(10), getMessage(11), getSecondMessage(12),
        getMessage(13), getSecondMessage(14), getMessage(15), getSecondMessage(16))

    fun getMessage(no: Int): BluetoothMessage{
        return BluetoothMessage(
            "Hello There $no",
            "Krishna",
            true,
            "1685902490408"
        )
    }

    fun getSecondMessage(no: Int): BluetoothMessage{
        return BluetoothMessage(
            "Hi $no",
            "lavanya",
            false,
            "1685902490408"
        )
    }
}
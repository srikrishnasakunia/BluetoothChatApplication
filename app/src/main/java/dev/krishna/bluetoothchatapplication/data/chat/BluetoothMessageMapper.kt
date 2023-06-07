package dev.krishna.bluetoothchatapplication.data.chat

import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothMessage
import java.text.SimpleDateFormat
import java.util.Date

fun String.toBluetoothMessage(isFromLocalSender: Boolean): BluetoothMessage{
    val name = substringBefore(" # ")
    val message = substringAfter(" # ")
    val timestamp = substringAfterLast(" # ")
    return BluetoothMessage(
        message = message,
        senderName = name,
        isFromLocalSender = isFromLocalSender,
        timestamp = timestamp
    )
}

fun BluetoothMessage.toByteArray(): ByteArray{
    return "$senderName # $message # $timestamp".toByteArray()
}

fun convertToTime(timeStamp: String): String{
    val simpleDateFormat = SimpleDateFormat("hh:mm:ss")
    return simpleDateFormat.format(Date(timeStamp.toLong()))
}
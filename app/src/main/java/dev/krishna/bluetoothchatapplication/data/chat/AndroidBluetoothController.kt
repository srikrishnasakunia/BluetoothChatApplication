package dev.krishna.bluetoothchatapplication.data.chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothController
import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothDeviceDomain
import dev.krishna.bluetoothchatapplication.domain.chat.BluetoothMessage
import dev.krishna.bluetoothchatapplication.domain.chat.ConnectionResult
import dev.krishna.bluetoothchatapplication.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class AndroidBluetoothController(private val context: Context) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var bluetoothDataTransferService: BluetoothDataTransferService? = null

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevice: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevice: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit(BLUETOOTH_NON_PAIRED_DEVICES_ERROR)
            }
        }
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    init {
        updatePairedDevices()
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )
    }

    override fun startDiscovery() {
        if (!hasPermission(context, Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
        updatePairedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermission(context, Manifest.permission.BLUETOOTH_CONNECT))
            return

        bluetoothAdapter?.cancelDiscovery()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow<ConnectionResult> {
            checkBluetoothPermission()

            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                SERVICE_NAME,
                UUID.fromString(SERVICE_UUID)
            )

            var shouldLoop = true
            while (shouldLoop) {
                currentClientSocket = try {
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    null
                }
                emit(ConnectionResult.ConnectionEstablished)
                currentClientSocket?.let {
                    currentServerSocket?.close()
                    val service = BluetoothDataTransferService(it)
                    bluetoothDataTransferService = service

                    emitAll(
                        service
                            .listenForIncomingMessages()
                            .map {
                                ConnectionResult.TransferSucceeded(it)
                            }
                    )
                }
            }
        }.onCompletion {
            closeTheConnectedConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            checkBluetoothPermission()

            val connectedBluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)

            currentClientSocket = connectedBluetoothDevice
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID)
                )
            stopDiscovery()
            currentClientSocket?.let { socket ->
                try {
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished)

                    BluetoothDataTransferService(socket).also {
                        bluetoothDataTransferService = it
                        emitAll(
                            it.listenForIncomingMessages()
                                .map { bluetoothMessage ->
                                    ConnectionResult.TransferSucceeded(bluetoothMessage)
                                }
                        )
                    }

                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error(BLUETOOTH_CONNECTION_ERROR_MESSAGE))
                }
            }
        }.onCompletion {
            closeTheConnectedConnection()
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun sendMessage(message: String): BluetoothMessage? {
        if ((!hasPermission(context,Manifest.permission.BLUETOOTH_CONNECT)) ||
            (bluetoothDataTransferService == null)){
            return null
        }
        val bluetoothMessage = BluetoothMessage(
            message = message,
            senderName = bluetoothAdapter?.name?: "Unknown Name",
            isFromLocalSender = true,
            timestamp = System.currentTimeMillis().toString()
        )

        bluetoothDataTransferService?.sendMessage(bluetoothMessage.toByteArray())
        return bluetoothMessage
    }

    override fun closeTheConnectedConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun releaseDevices() {
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        closeTheConnectedConnection()
    }

    private fun updatePairedDevices() {
        if (!hasPermission(context, Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { device ->
                _pairedDevices.update { device }
            }

    }

    private fun checkBluetoothPermission() {
        if (!hasPermission(context, Manifest.permission.BLUETOOTH_CONNECT)) {
            throw SecurityException(BLUETOOTH_PERMISSION_ERROR_MESSAGE)
        }
    }
}
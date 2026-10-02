package com.vaultcam.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Broadcast de alertas de incidente via BLE (~100m offline).
 * Devices próximos recebem a notificação e bloqueiam seus vídeos recentes.
 */
class BluetoothAlertService(context: Context) {

    companion object {
        val VAULTCAM_SERVICE_UUID: UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        private val PARCEL_UUID = ParcelUuid(VAULTCAM_SERVICE_UUID)
        private const val MANUFACTURER_ID = 0x5643  // "VC" VaultCam
    }

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
    private val scanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    private val _incomingAlerts = MutableSharedFlow<IncidentAlert>(extraBufferCapacity = 10)
    val incomingAlerts: SharedFlow<IncidentAlert> = _incomingAlerts.asSharedFlow()

    private var currentAdvertiseCallback: AdvertiseCallback? = null
    private var isScanningAlerts = false

    fun broadcastIncident(alert: IncidentAlert) {
        val advertiser = advertiser ?: return
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .setTimeout(30_000)  // 30s broadcast
            .build()

        val payload = encodeAlert(alert)
        val data = AdvertiseData.Builder()
            .addServiceUuid(PARCEL_UUID)
            .addManufacturerData(MANUFACTURER_ID, payload)
            .setIncludeDeviceName(false)
            .build()

        currentAdvertiseCallback?.let { advertiser.stopAdvertising(it) }
        val callback = object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                // BLE not available on this device — not critical
            }
        }
        currentAdvertiseCallback = callback
        advertiser.startAdvertising(settings, data, callback)
    }

    fun startScanningForAlerts() {
        val scanner = scanner ?: return
        if (isScanningAlerts) return

        val filter = ScanFilter.Builder()
            .setServiceUuid(PARCEL_UUID)
            .build()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()

        scanner.startScan(listOf(filter), settings, scanCallback)
        isScanningAlerts = true
    }

    fun stopScanningForAlerts() {
        if (!isScanningAlerts) return
        scanner?.stopScan(scanCallback)
        isScanningAlerts = false
    }

    fun stopBroadcast() {
        currentAdvertiseCallback?.let { advertiser?.stopAdvertising(it) }
        currentAdvertiseCallback = null
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val record: ScanRecord = result.scanRecord ?: return
            val mfgData = record.getManufacturerSpecificData(MANUFACTURER_ID) ?: return
            decodeAlert(mfgData)?.let { _incomingAlerts.tryEmit(it) }
        }
    }

    private fun encodeAlert(alert: IncidentAlert): ByteArray {
        val buf = ByteBuffer.allocate(20)
        buf.putLong(alert.timestampMs)
        buf.putFloat(alert.latitude?.toFloat() ?: 0f)
        buf.putFloat(alert.longitude?.toFloat() ?: 0f)
        buf.putFloat(alert.gForce)
        return buf.array()
    }

    private fun decodeAlert(data: ByteArray): IncidentAlert? {
        if (data.size < 20) return null
        return runCatching {
            val buf = ByteBuffer.wrap(data)
            IncidentAlert(
                timestampMs = buf.getLong(),
                latitude = buf.getFloat().toDouble().takeIf { it != 0.0 },
                longitude = buf.getFloat().toDouble().takeIf { it != 0.0 },
                gForce = buf.getFloat()
            )
        }.getOrNull()
    }
}

data class IncidentAlert(
    val timestampMs: Long,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val gForce: Float = 0f
)

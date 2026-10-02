package com.vaultcam.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.math.sqrt

/**
 * Detecta paradas bruscas via TYPE_LINEAR_ACCELERATION.
 * G-force > 2.5G = possível colisão → emite ImpactEvent.
 */
class ImpactDetectorService(context: Context) : SensorEventListener {

    companion object {
        private const val IMPACT_THRESHOLD_G = 2.5f
        private const val GRAVITY = 9.81f
        private const val COOLDOWN_MS = 3_000L
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    private val _impacts = MutableSharedFlow<ImpactEvent>(extraBufferCapacity = 10)
    val impacts: SharedFlow<ImpactEvent> = _impacts.asSharedFlow()

    private var lastImpactMs = 0L
    private var isListening = false

    fun startListening() {
        if (isListening) return
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
            isListening = true
        }
    }

    fun stopListening() {
        if (!isListening) return
        sensorManager.unregisterListener(this)
        isListening = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_LINEAR_ACCELERATION) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val magnitude = sqrt(x * x + y * y + z * z)
        val gForce = magnitude / GRAVITY

        val now = System.currentTimeMillis()
        if (gForce >= IMPACT_THRESHOLD_G && (now - lastImpactMs) > COOLDOWN_MS) {
            lastImpactMs = now
            _impacts.tryEmit(
                ImpactEvent(
                    gForce = gForce,
                    timestampMs = now,
                    x = x,
                    y = y,
                    z = z
                )
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}

data class ImpactEvent(
    val gForce: Float,
    val timestampMs: Long,
    val x: Float,
    val y: Float,
    val z: Float
)

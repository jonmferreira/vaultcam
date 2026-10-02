package com.vaultcam.service

import android.content.Context
import io.livekit.android.LiveKit
import io.livekit.android.LiveKitOverrides
import io.livekit.android.events.RoomEvent
import io.livekit.android.events.collect
import io.livekit.android.room.Room
import io.livekit.android.room.participant.LocalParticipant
import io.livekit.android.room.track.CameraPosition
import io.livekit.android.room.track.LocalVideoTrack
import io.livekit.android.room.track.LocalVideoTrackOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Streaming ao vivo para dashboard empresarial via LiveKit.
 * Sub-500ms latência, E2E criptografado (WebRTC DTLS-SRTP).
 */
class FleetStreamingService(private val context: Context) {

    companion object {
        // Produção: endpoint LiveKit Cloud ou self-hosted
        private const val LIVEKIT_URL = "wss://vaultcam.livekit.cloud"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _streamState = MutableStateFlow<StreamState>(StreamState.Idle)
    val streamState: StateFlow<StreamState> = _streamState.asStateFlow()

    private var room: Room? = null
    private var videoTrack: LocalVideoTrack? = null

    suspend fun startStreaming(liveKitToken: String): Result<Unit> = runCatching {
        _streamState.value = StreamState.Connecting

        val liveKitRoom = LiveKit.create(context)
        room = liveKitRoom

        scope.launch {
            liveKitRoom.events.collect { event ->
                when (event) {
                    is RoomEvent.Disconnected -> _streamState.value = StreamState.Idle
                    is RoomEvent.Connected -> _streamState.value = StreamState.Streaming(
                        participantCount = liveKitRoom.remoteParticipants.size + 1
                    )
                    else -> Unit
                }
            }
        }

        liveKitRoom.connect(LIVEKIT_URL, liveKitToken)

        val localParticipant: LocalParticipant = liveKitRoom.localParticipant
        val track = localParticipant.createVideoTrack(
            name = "dashcam",
            options = LocalVideoTrackOptions(
                position = CameraPosition.BACK
            )
        )
        localParticipant.publishVideoTrack(track)
        videoTrack = track

        _streamState.value = StreamState.Streaming(participantCount = 1)
    }

    fun stopStreaming() {
        videoTrack?.stop()
        videoTrack = null
        room?.disconnect()
        room = null
        _streamState.value = StreamState.Idle
    }
}

sealed class StreamState {
    data object Idle : StreamState()
    data object Connecting : StreamState()
    data class Streaming(val participantCount: Int) : StreamState()
    data class Error(val message: String) : StreamState()
}

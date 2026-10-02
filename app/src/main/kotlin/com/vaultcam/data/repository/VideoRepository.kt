package com.vaultcam.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vaultcam.data.model.VideoRecord
import com.vaultcam.data.model.VideoStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vaultcam_videos")

class VideoRepository(private val context: Context) {

    companion object {
        private val KEY_VIDEOS = stringPreferencesKey("videos_json")
        const val MAX_LOCAL_VIDEOS = 10
    }

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, VideoRecord::class.java)
    private val adapter = moshi.adapter<List<VideoRecord>>(listType)

    val videos: Flow<List<VideoRecord>> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_VIDEOS] ?: "[]"
        adapter.fromJson(json) ?: emptyList()
    }

    suspend fun saveVideo(record: VideoRecord) {
        context.dataStore.edit { prefs ->
            val current = adapter.fromJson(prefs[KEY_VIDEOS] ?: "[]")?.toMutableList()
                ?: mutableListOf()
            val existing = current.indexOfFirst { it.id == record.id }
            if (existing >= 0) {
                current[existing] = record
            } else {
                current.add(0, record)
                enforceLocalLimit(current)
            }
            prefs[KEY_VIDEOS] = adapter.toJson(current)
        }
    }

    suspend fun updateVideo(record: VideoRecord) {
        context.dataStore.edit { prefs ->
            val current = adapter.fromJson(prefs[KEY_VIDEOS] ?: "[]")?.toMutableList()
                ?: mutableListOf()
            val idx = current.indexOfFirst { it.id == record.id }
            if (idx >= 0) current[idx] = record
            prefs[KEY_VIDEOS] = adapter.toJson(current)
        }
    }

    suspend fun deleteVideo(videoId: String) {
        context.dataStore.edit { prefs ->
            val current = adapter.fromJson(prefs[KEY_VIDEOS] ?: "[]")?.toMutableList()
                ?: mutableListOf()
            val record = current.find { it.id == videoId }
            record?.localPath?.let { File(it).delete() }
            current.removeAll { it.id == videoId }
            prefs[KEY_VIDEOS] = adapter.toJson(current)
        }
    }

    suspend fun getVideo(videoId: String): VideoRecord? {
        return videos.first().find { it.id == videoId }
    }

    private fun enforceLocalLimit(videos: MutableList<VideoRecord>) {
        val localNotLocked = videos.filter {
            !it.isLocked && it.status == VideoStatus.LOCAL && it.localPath != null
        }.sortedBy { it.timestampMs }

        val toDelete = localNotLocked.take(
            maxOf(0, videos.count { !it.isLocked && it.status == VideoStatus.LOCAL } - MAX_LOCAL_VIDEOS)
        )
        toDelete.forEach { record ->
            record.localPath?.let { File(it).delete() }
            videos.removeAll { it.id == record.id }
        }
    }

    fun getVideoDir(): File {
        val dir = File(context.filesDir, "videos")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
}

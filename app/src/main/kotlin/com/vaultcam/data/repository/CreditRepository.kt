package com.vaultcam.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.creditStore by preferencesDataStore(name = "vaultcam_credits")

class CreditRepository(private val context: Context) {

    companion object {
        private val KEY_CREDITS = intPreferencesKey("balance")
        const val CREDITS_PER_CONTRIBUTION = 3  // share 1 video → unlock 3
    }

    val balance: Flow<Int> = context.creditStore.data.map { it[KEY_CREDITS] ?: 0 }

    suspend fun addCredits(amount: Int) {
        context.creditStore.edit { prefs ->
            prefs[KEY_CREDITS] = (prefs[KEY_CREDITS] ?: 0) + amount
        }
    }

    suspend fun spendCredit(): Boolean {
        var success = false
        context.creditStore.edit { prefs ->
            val current = prefs[KEY_CREDITS] ?: 0
            if (current > 0) {
                prefs[KEY_CREDITS] = current - 1
                success = true
            }
        }
        return success
    }
}

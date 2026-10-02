package com.vaultcam

import android.app.Application
import com.vaultcam.service.IrysService
import com.vaultcam.service.LitProtocolService
import com.vaultcam.service.PaymentService
import com.vaultcam.service.WalletService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class VaultCamApp : Application() {

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()
    }

    val litProtocolService: LitProtocolService by lazy {
        LitProtocolService(okHttpClient)
    }

    val irysService: IrysService by lazy {
        IrysService(okHttpClient)
    }

    val walletService: WalletService by lazy {
        WalletService(this)
    }

    val paymentService: PaymentService by lazy {
        PaymentService(this, walletService)
    }
}

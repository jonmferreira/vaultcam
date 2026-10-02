package com.vaultcam

import android.app.Application
import android.net.Uri
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.vaultcam.service.BluetoothAlertService
import com.vaultcam.service.FleetStreamingService
import com.vaultcam.service.IrysService
import com.vaultcam.service.LitProtocolService
import com.vaultcam.service.PaymentService
import com.vaultcam.service.RewardService
import com.vaultcam.service.SolanaTokenService
import com.vaultcam.service.WakuService
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
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()
    }

    val walletAdapter: MobileWalletAdapter by lazy {
        MobileWalletAdapter(
            connectionIdentity = ConnectionIdentity(
                identityUri = Uri.parse("https://vaultcam.app"),
                iconUri = Uri.parse("https://vaultcam.app/icon.png"),
                identityName = "VaultCam"
            )
        )
    }

    val litProtocolService: LitProtocolService by lazy { LitProtocolService(okHttpClient) }
    val irysService: IrysService by lazy { IrysService(okHttpClient) }
    val walletService: WalletService by lazy { WalletService(this) }
    val solanaTokenService: SolanaTokenService by lazy { SolanaTokenService(okHttpClient) }

    val paymentService: PaymentService by lazy {
        PaymentService(this, walletService, solanaTokenService, walletAdapter)
    }

    val rewardService: RewardService by lazy {
        RewardService(this, walletService, solanaTokenService, walletAdapter)
    }

    val bluetoothAlertService: BluetoothAlertService by lazy { BluetoothAlertService(this) }
    val wakuService: WakuService by lazy { WakuService(okHttpClient) }
    val fleetStreamingService: FleetStreamingService by lazy { FleetStreamingService(this) }
}

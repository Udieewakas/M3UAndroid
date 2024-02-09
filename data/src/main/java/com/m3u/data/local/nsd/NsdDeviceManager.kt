package com.m3u.data.local.nsd

import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.flow.Flow

interface NsdDeviceManager {
    fun search(): Flow<List<NsdServiceInfo>>
    fun broadcast(
        name: String = "M3U_BROADCAST",
        port: Int,
        pin: Int,
        metadata: Map<String, Any> = emptyMap()
    ): Flow<NsdServiceInfo?>

    companion object {
        const val SERVICE_TYPE = "_m3u-server._tcp."
        const val META_DATA_PORT = "port"
        const val META_DATA_ADDRESS = "address"
        const val META_DATA_PIN = "pin"
    }
}

package com.tpstreams.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.DrmConfiguration
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class TPStreamsPlayer(context: Context) : Player by createExoPlayer(context) {

    companion object {
        private var organizationId: String? = null

        fun init(orgId: String) {
            organizationId = orgId
        }

        /**
         * Fetches asset metadata and builds the MediaItem with the real video + license URLs.
         */
        suspend fun buildMediaItem(
            assetId: String,
            accessToken: String
        ): MediaItem = withContext(Dispatchers.IO) {
            val org = organizationId
                ?: throw IllegalStateException("TPStreamsPlayer.init(orgId) must be called first")

            val assetApiUrl =
                "https://app.tpstreams.com/api/v1/$org/assets/$assetId/?access_token=$accessToken"

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(assetApiUrl)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw Exception("Failed to fetch asset metadata: ${response.code}")
            }

            val body = response.body?.string()
                ?: throw Exception("Empty response from asset metadata endpoint")

            val json = JSONObject(body)
            val dashUrl = json
                .getJSONObject("video")
                .getString("dash_url")

            val licenseUrl =
                "https://app.tpstreams.com/api/v1/$org/assets/$assetId/drm_license/?access_token=$accessToken"

            val drmHeaders = mapOf("Authorization" to "Bearer $accessToken")

            val drmConfig = DrmConfiguration.Builder(C.WIDEVINE_UUID)
                .setLicenseUri(licenseUrl)
                .setLicenseRequestHeaders(drmHeaders)
                .setMultiSession(true)
                .build()

            MediaItem.Builder()
                .setUri(dashUrl)
                .setDrmConfiguration(drmConfig)
                .build()
        }

        @OptIn(UnstableApi::class)
        private fun createExoPlayer(context: Context): ExoPlayer {
            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("TPStreamsPlayer")
                .setAllowCrossProtocolRedirects(true)

            val mediaSourceFactory = DefaultMediaSourceFactory(context)
                .setDataSourceFactory(dataSourceFactory)

            return ExoPlayer.Builder(context)
                .setMediaSourceFactory(mediaSourceFactory)
                .build()
        }
    }
}
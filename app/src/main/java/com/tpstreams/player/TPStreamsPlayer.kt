package com.tpstreams.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.DrmConfiguration
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

class TPStreamsPlayer(context: Context) : Player by createExoPlayer(context) {

    companion object {
        private var organizationId: String? = null

        fun init(orgId: String) {
            organizationId = orgId
        }

        fun buildMediaItem(assetId: String, accessToken: String): MediaItem {
            val org = organizationId
                ?: throw IllegalStateException("TPStreamsPlayer.init(orgId) must be called first")

            val videoUri =
                "https://app.tpstreams.com/api/v1/$org/assets/$assetId/?access_token=$accessToken"

            Log.d("TPStreamsPlayer", "videoUri: $videoUri")
            val licenseUri =
                "https://app.tpstreams.com/api/v1/$org/assets/$assetId/drm_license/?access_token=$accessToken"

            Log.i("TPStreamsPlayer", "licenseUri: $licenseUri")

            val drmHeaders = mapOf(
                "Authorization" to "Bearer $accessToken"
            )

            val drmConfig = DrmConfiguration.Builder(C.WIDEVINE_UUID)
                .setLicenseUri(licenseUri)
                .setLicenseRequestHeaders(drmHeaders)
                .setMultiSession(true)
                .build()

            return MediaItem.Builder()
                .setUri(videoUri)
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

package com.blundell.polkiemon.ui

import android.content.Context
import coil.ImageLoader
import coil.util.DebugLogger
import com.blundell.polkiemon.BuildConfig

class ImageLoaderFactory(private val context: Context) : coil.ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        val application = context.applicationContext
        return ImageLoader.Builder(application)
            // This loads the image from cache if we have it
            // saving server load
            .respectCacheHeaders(false)
            .also {
                if (BuildConfig.DEBUG) {
                    it.logger(DebugLogger())
                }
            }
            .build()
    }
}

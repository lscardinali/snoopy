package com.cortinadev.snoopy.data.service

import android.util.Log
import com.cortinadev.snoopy.data.repository.SettingsRepository
import com.cortinadev.snoopy.util.ConversionFormat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AutoChd"

/**
 * "Compress disc images to CHD" (Settings → Downloads): every finished download whose disc image
 * converts to CHD (PlayStation, PS2, PSP, Saturn, Dreamcast, Sega CD, PC Engine CD… whether it
 * came as a bare image or unpacked from an archive) is queued in [RomConversionService] once the
 * steps after a download are done with it ([MultiDiscService.settled]: a multi-disc game is
 * converted in its `<game>.m3u/` folder). It runs in the background, one at a time, with the
 * conversion's own notification; the CHD is checked against the image and only then are the
 * originals deleted for good ([ConversionOrigin.FREE_SPACE]). Anything that cannot be converted,
 * or would save too little, is left as it is.
 */
@Singleton
class AutoChdService @Inject constructor(
    multiDiscService: MultiDiscService,
    private val conversions: RomConversionService,
    private val settings: SettingsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            multiDiscService.settled.collect { outcome ->
                try {
                    convert(outcome.fileName)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(TAG, "${outcome.fileName}: not queued for CHD: ${e.message}")
                }
            }
        }
    }

    private suspend fun convert(fileName: String) {
        if (!settings.compressToChd.first()) return
        // Cheap, by names: skips everything that is not a CHD system's disc image.
        if (conversions.likelyFormatForDownload(fileName) != ConversionFormat.CHD) return
        val target = conversions.targetForDownload(fileName)?.takeIf { it.format == ConversionFormat.CHD } ?: return
        Log.i(TAG, "$fileName: ${target.main.name} queued for CHD")
        conversions.enqueue(listOf(target), ConversionOrigin.FREE_SPACE)
    }
}

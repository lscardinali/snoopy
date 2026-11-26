package com.cortinadev.snoopy.data.service

import android.content.Context
import android.util.Log
import com.cortinadev.snoopy.R
import com.cortinadev.snoopy.data.local.entity.DownloadableFileEntity
import com.cortinadev.snoopy.data.model.Console
import com.cortinadev.snoopy.data.model.UrlEntry
import com.cortinadev.snoopy.data.state.RescanStateHolder
import com.cortinadev.snoopy.util.FileParsingUtils
import com.cortinadev.snoopy.util.RommSource
import com.cortinadev.snoopy.util.ScanProgressMath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "RommScrapingService"

/**
 * Indexes a `romm://<slug>` source: every ROM RomM lists for that platform becomes a library
 * row whose download URL is the server's `/api/roms/{id}/content/…` endpoint.
 */
@Singleton
class RommScrapingService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val rommClient: RommClient,
    private val rescanStateHolder: RescanStateHolder
) {
    /** Lists the platform's ROMs and hands them to [sink], which writes them only if they changed. */
    suspend fun scrapeAndInsert(urlEntry: UrlEntry, console: Console, sink: SourceRowsSink): SourceWrite = withContext(Dispatchers.IO) {
        val slug = RommSource.slugOf(urlEntry.url) ?: throw Exception(context.getString(R.string.scrape_romm_invalid_source, urlEntry.url))
        val base = rommClient.configuredBaseUrl().ifEmpty { throw Exception(context.getString(R.string.scrape_romm_not_configured)) }
        rescanStateHolder.setTorrentFetchProgress(context.getString(R.string.scrape_listing_romm, slug))
        try {
            val platform = rommClient.platforms().firstOrNull { it.slug.equals(slug, true) || it.fsSlug.equals(slug, true) || it.id.toString() == slug }
                ?: throw Exception(context.getString(R.string.scrape_romm_no_platform, slug))
            val roms = rommClient.roms(platform.id)
            if (roms.isEmpty()) Log.i(TAG, "No ROMs on RomM for $slug")
            rescanStateHolder.sourceProgress(RescanStateHolder.sourceKey(console.id, urlEntry.url), ScanProgressMath.PHASE_FETCHED)
            val files = ArrayList<DownloadableFileEntity>(roms.size)
            val tags = ArrayList<List<String>>(roms.size)
            val contentTypeTag = FileParsingUtils.normalizeTag(urlEntry.contentType.name)
            roms.forEach { rom ->
                val (cleanName, tagStrings) = FileParsingUtils.extractNameAndTags(rom.fsName.substringBeforeLast('.', rom.fsName))
                files += DownloadableFileEntity(
                    name = cleanName.ifBlank { rom.name.ifBlank { rom.fsName } },
                    fileName = rom.fsName,
                    consoleId = console.id,
                    downloadUrl = RommSource.downloadUrl(base, rom.id, rom.fsName),
                    fileSize = rom.fsSizeBytes,
                    fileExtension = rom.fsName.substringAfterLast('.', "").let { if (it.isEmpty()) "" else ".$it" }
                )
                tags += (tagStrings + contentTypeTag).distinct()
            }
            val write = sink(files, tags)
            Log.i(TAG, "Indexed ${files.size} ROM(s) from RomM platform $slug" + if (write.unchanged) " (unchanged)" else "")
            write
        } finally {
            rescanStateHolder.setTorrentFetchProgress("")
        }
    }
}

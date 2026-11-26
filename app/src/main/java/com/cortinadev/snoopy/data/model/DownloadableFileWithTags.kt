package com.cortinadev.snoopy.data.model

import com.cortinadev.snoopy.data.local.entity.DownloadableFileEntity

data class DownloadableFileWithTags(
    val file: DownloadableFileEntity,
    val tags: List<String>
)

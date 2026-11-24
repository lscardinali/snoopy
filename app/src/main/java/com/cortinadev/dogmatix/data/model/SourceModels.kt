package com.cortinadev.dogmatix.data.model

data class Manufacturer(
    val id: String,
    val name: String,
    val consoles: List<Console> = emptyList()
)

data class Console(
    val id: String,
    val name: String,
    val urls: List<UrlEntry> = emptyList(),
    /** Configured chip label; empty = built-in default. */
    val shortName: String = "",
    /** Configured download-folder aliases; empty = built-in defaults. */
    val folderAliases: List<String> = emptyList(),
    /** Configured chip colour (`#RRGGBB`); empty = the default of its family. */
    val color: String = "",
    /** Whether archives downloaded for this console are unpacked; [UnpackMode.DEFAULT] follows "Auto-unzip files". */
    val unpack: UnpackMode = UnpackMode.DEFAULT
)

/**
 * A console's override of "Auto-unzip files": some sets must stay zipped (Neo Geo, arcade and
 * other MAME-style ROM sets are read as .zip by their emulators).
 */
enum class UnpackMode {
    DEFAULT,
    ALWAYS,
    NEVER;

    /** Whether an archive of a console in this mode is unpacked, given the global [autoUnzip]. */
    fun shouldUnpack(autoUnzip: Boolean): Boolean = when (this) {
        DEFAULT -> autoUnzip
        ALWAYS -> true
        NEVER -> false
    }

    /** The value kept in the `unpack` column and the sources JSON; empty for [DEFAULT]. */
    val stored: String get() = if (this == DEFAULT) "" else name

    companion object {
        /** [stored] back; anything unknown or empty is [DEFAULT]. */
        fun parse(value: String?): UnpackMode =
            value?.trim()?.uppercase()?.let { v -> entries.firstOrNull { it.name == v } } ?: DEFAULT
    }
}

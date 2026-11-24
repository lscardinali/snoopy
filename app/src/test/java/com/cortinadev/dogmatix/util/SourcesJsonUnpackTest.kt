package com.cortinadev.dogmatix.util

import com.cortinadev.dogmatix.data.model.UnpackMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SourcesJsonUnpackTest {

    @Test
    fun `a console's unpack choice survives export and import, left out when default`() {
        val doc = listOf(
            SourceManufacturer(
                "snk", "SNK", listOf(
                    SourceConsole("snk_neo_geo", "Neo Geo", emptyList(), unpack = UnpackMode.NEVER),
                    SourceConsole("snk_neo_geo_cd", "Neo Geo CD", emptyList())
                )
            )
        )
        val json = SourcesJson.serializeDocument(doc)
        val parsed = SourcesJson.parseDocument(json).single().consoles

        assertEquals(listOf(UnpackMode.NEVER, UnpackMode.DEFAULT), parsed.map { it.unpack })
        assertEquals(1, Regex("\"unpack\"").findAll(json).count())
    }

    @Test
    fun `stored values parse back and unknown ones follow the setting`() {
        UnpackMode.entries.forEach { assertEquals(it, UnpackMode.parse(it.stored)) }
        assertEquals(UnpackMode.NEVER, UnpackMode.parse("never"))
        assertEquals(UnpackMode.DEFAULT, UnpackMode.parse("sometimes"))
        assertEquals(UnpackMode.DEFAULT, UnpackMode.parse(null))
    }

    @Test
    fun `the console choice overrides auto-unzip`() {
        assertTrue(UnpackMode.DEFAULT.shouldUnpack(autoUnzip = true))
        assertFalse(UnpackMode.DEFAULT.shouldUnpack(autoUnzip = false))
        assertFalse(UnpackMode.NEVER.shouldUnpack(autoUnzip = true))
        assertTrue(UnpackMode.ALWAYS.shouldUnpack(autoUnzip = false))
    }
}

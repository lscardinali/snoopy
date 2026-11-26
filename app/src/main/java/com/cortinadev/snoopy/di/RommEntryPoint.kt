package com.cortinadev.snoopy.di

import com.cortinadev.snoopy.data.service.RommCatalogService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Lets list rows and images (composables, no ViewModel) reach the RomM catalogue. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface RommEntryPoint {
    fun rommCatalogService(): RommCatalogService
}

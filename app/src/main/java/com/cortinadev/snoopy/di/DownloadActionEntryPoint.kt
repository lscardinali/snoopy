package com.cortinadev.snoopy.di

import com.cortinadev.snoopy.data.repository.DownloadRepository
import com.cortinadev.snoopy.data.service.DownloadNotifications
import com.cortinadev.snoopy.data.service.DownloadQueueService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Lets the notification buttons ([com.cortinadev.snoopy.data.service.DownloadActionReceiver]) reach the downloads. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface DownloadActionEntryPoint {
    fun downloadRepository(): DownloadRepository
    fun downloadQueue(): DownloadQueueService
    fun downloadNotifications(): DownloadNotifications
}

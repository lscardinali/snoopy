package com.cortinadev.snoopy.di

import com.cortinadev.snoopy.data.state.RescanStateHolder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RescanStateEntryPoint {
    fun rescanStateHolder(): RescanStateHolder
}

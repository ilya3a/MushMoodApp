package com.yoyo.mushmoodapp.player.di

import android.content.Context
import com.yoyo.mushmoodapp.player.DefaultPlayerRepository
import com.yoyo.mushmoodapp.player.PlayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun providePlayerRepository(
        @ApplicationContext context: Context
    ): PlayerRepository = DefaultPlayerRepository(context)
}


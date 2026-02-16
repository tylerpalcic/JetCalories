package com.tylerpalcic.fittrack.di

import android.content.Context
import com.tylerpalcic.core.data.data_store.DefaultUserDataStore
import com.tylerpalcic.core.domain.data_store.UserDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesUserDataStore(
        @ApplicationContext context: Context
    ): UserDataStore = DefaultUserDataStore(context)
}
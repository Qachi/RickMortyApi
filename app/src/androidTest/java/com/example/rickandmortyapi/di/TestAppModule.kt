package com.example.rickandmortyapi.di

import android.content.Context
import androidx.room.Room
import com.example.rickandmortyapi.database.RickMortyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryDB(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(
            context,
            RickMortyDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
}

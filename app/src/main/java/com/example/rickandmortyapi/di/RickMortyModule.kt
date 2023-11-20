package com.example.rickandmortyapi.di

import android.content.Context
import androidx.room.Room
import com.example.rickandmortyapi.api.RickMortyApi
import com.example.rickandmortyapi.dao.RickMortyDao
import com.example.rickandmortyapi.database.RickMortyDatabase
import com.example.rickandmortyapi.repositories.RickMortyRepository
import com.example.rickandmortyapi.repositories.RickMortyRepositoryImpl
import com.example.rickandmortyapi.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
object RickMortyModule {

    private val interceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
    }.build()

    @[Singleton Provides]
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @[Singleton Provides]
    fun provideRickMortyAPi(retrofit: Retrofit): RickMortyApi =
        retrofit.create(RickMortyApi::class.java)

    @[Singleton Provides]
    fun provideRickMortyDataBase(@ApplicationContext context: Context): RickMortyDatabase =
        Room.databaseBuilder(context,
            RickMortyDatabase::class.java,
            Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @[Singleton Provides]
    fun provideRickMortyRepositoryImpl(
        rickMortyDao: RickMortyDao,
        rickMortyApi: RickMortyApi,
        rickMortyDatabase: RickMortyDatabase
    ) = RickMortyRepositoryImpl(
        rickMortyDao,
        rickMortyApi,
        rickMortyDatabase
    ) as RickMortyRepository

    @[Singleton Provides]
    fun provideRickyMortyDao(rickMortyDatabase: RickMortyDatabase): RickMortyDao =
        rickMortyDatabase.getRickMortyDao()
}

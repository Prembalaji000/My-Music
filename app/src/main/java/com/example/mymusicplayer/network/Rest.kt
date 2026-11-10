package com.example.mymusicplayer.network

import com.example.mymusicplayer.model.AudiusArtwork
import com.example.mymusicplayer.model.AudiusArtworkDeserializer
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class Rest {

    private val baseUrl = "https://api.audius.co/v1/"
    private val lyricBaseUrl = "https://lrclib.net/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder().build()
                )
            }
            .addInterceptor(httpLoggingInterceptor())
            .connectTimeout(40, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @AudiusRetrofit
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @LyricsRetrofit
    fun provideLyricRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(lyricBaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(
        @AudiusRetrofit retrofit: Retrofit
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLyricsApiService(
        @LyricsRetrofit retrofit: Retrofit
    ): LyricsApiService {
        return retrofit.create(LyricsApiService::class.java)
    }

    private fun httpLoggingInterceptor() =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AudiusRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LyricsRetrofit
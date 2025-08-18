package com.lancar.tugasakhir.di

import android.content.Context
import androidx.room.Room
import com.lancar.tugasakhir.data.AppDatabase
import com.lancar.tugasakhir.data.BookDao
import com.lancar.tugasakhir.data.NotificationHistoryDao
import com.lancar.tugasakhir.data.SessionExpiredNotifier
import com.lancar.tugasakhir.data.UserPreferencesRepository
import com.lancar.tugasakhir.network.ApiService
import com.lancar.tugasakhir.network.AuthInterceptor
import com.lancar.tugasakhir.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository =
        UserPreferencesRepository(context)

    @Provides @Singleton
    fun provideSessionExpiredNotifier(): SessionExpiredNotifier = SessionExpiredNotifier()

    @Provides @Singleton
    fun provideAuthInterceptor(
        prefsRepository: UserPreferencesRepository,
        sessionNotifier: SessionExpiredNotifier
    ): AuthInterceptor = AuthInterceptor(prefsRepository, sessionNotifier)

    @Provides @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(authInterceptor).build()

    @Provides @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService =
        Retrofit.Builder()
            .baseUrl("https://satuperpustakaanku.my.id/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    @Provides @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideNotificationHistoryDao(db: AppDatabase): NotificationHistoryDao = db.notificationHistoryDao()
    @Provides fun provideBookDao(db: AppDatabase): BookDao = db.bookDao()

    @Provides @Singleton
    fun provideAppRepository(
        api: ApiService,
        bookDao: BookDao,
        notificationHistoryDao: NotificationHistoryDao
    ): AppRepository = AppRepository(api, bookDao, notificationHistoryDao)
}

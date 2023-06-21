package com.example.android.hilt.di

import android.content.Context
import androidx.room.Room
import com.example.android.hilt.data.AppDatabase
import com.example.android.hilt.data.LogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    /**
     * Every time Hilt needs an instance of +LogDao+ this function will be called.
     *
     * Note on this example here: there is a transitive dependency on +AppDatabase+.
     * That's why we use another +@Provides+ function (+provideDatabase+) that will
     * return an instance of +AppDatabase+ (see next function).
     */
    @Provides
    fun provideLogDao(database: AppDatabase): LogDao {
        return database.logDao()
    }

    /**
     * Every time Hilt needs an instance of +AppDatabase+ this function will be called.
     *
     * Since we want that the same instance will always be returned, we annotate
     * with +@Singleton+ too.
     *
     * But, as we can see here, the +AppDatabase+ has a transitive dependency
     * to +Context+ type/instance. How can we tell Hilt how to instanctiate
     * the +Context+ argument? We are using the default binding +@ApplicationContext+.
     * Ref to default bindings: https://developer.android.com/training/dependency-injection/hilt-android#component-default
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "logging.db"
        ).build()
    }
}
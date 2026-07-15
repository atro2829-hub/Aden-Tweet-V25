package com.adentweets.app.di

import android.content.Context
import androidx.room.Room
import com.adentweets.app.data.local.AdenTweetDatabase
import com.adentweets.app.data.local.dao.MessageDao
import com.adentweets.app.data.local.dao.PostDao
import com.adentweets.app.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AdenTweetDatabase {
        return Room.databaseBuilder(
            context,
            AdenTweetDatabase::class.java,
            "adentweet_database"
        ).build()
    }

    @Provides
    fun providePostDao(database: AdenTweetDatabase): PostDao = database.postDao()

    @Provides
    fun provideUserDao(database: AdenTweetDatabase): UserDao = database.userDao()

    @Provides
    fun provideMessageDao(database: AdenTweetDatabase): MessageDao = database.messageDao()
}
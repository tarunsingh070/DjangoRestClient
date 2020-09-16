/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */
package tarun.djangorestclient.com.djangorestclient.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tarun.djangorestclient.com.djangorestclient.model.entity.Header
import tarun.djangorestclient.com.djangorestclient.model.entity.Request
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Request::class, Header::class], version = 1)
@TypeConverters(Converters::class)
abstract class RequestRoomDatabase : RoomDatabase() {
    //    Note: When you modify the database schema, you'll need to update the version number and define a migration strategy
    //
    //    For a sample, a destroy and re-create strategy can be sufficient. But, for a real app,
    //    you must implement a migration strategy. See Understanding migrations with Room.
    // Todo: To verify DB migration later, you can change the timestamp type from Date to Long or vice versa after once running the app.
    abstract fun requestDao(): RequestDao

    companion object {
        private const val REQUEST_DB = "request_database.db"

        @Volatile
        private lateinit var INSTANCE: RequestRoomDatabase
        private const val NUMBER_OF_THREADS = 4
        @JvmField
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        /**
         * Gets the instance of [RequestRoomDatabase]
         *
         * @param context An instance of [Context]
         * @return The instance of [RequestRoomDatabase]
         */
        @JvmStatic
        fun getDatabase(context: Context): RequestRoomDatabase {
            if (!this::INSTANCE.isInitialized) {
                synchronized(RequestRoomDatabase::class.java) {
                    if (!this::INSTANCE.isInitialized) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                RequestRoomDatabase::class.java, REQUEST_DB)
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}
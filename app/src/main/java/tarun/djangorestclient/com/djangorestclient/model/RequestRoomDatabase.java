/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */

package tarun.djangorestclient.com.djangorestclient.model;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import tarun.djangorestclient.com.djangorestclient.model.entity.Header;
import tarun.djangorestclient.com.djangorestclient.model.entity.Request;

@Database(entities = {Request.class, Header.class}, version = 1, exportSchema = true)
@TypeConverters({Converters.class})
public abstract class RequestRoomDatabase extends RoomDatabase {

    private static final String REQUEST_DB = "request_database.db";

//    Note: When you modify the database schema, you'll need to update the version number and define a migration strategy
//
//    For a sample, a destroy and re-create strategy can be sufficient. But, for a real app,
//    you must implement a migration strategy. See Understanding migrations with Room.

    // Todo: To verify DB migration later, you can change the timestamp type from Date to Long or vice versa after once running the app.

    public abstract RequestDao requestDao();

    private static volatile RequestRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Gets the instance of {@link RequestRoomDatabase}
     *
     * @param context An instance of {@link Context}
     * @return The instance of {@link RequestRoomDatabase}
     */
    static RequestRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RequestRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RequestRoomDatabase.class, REQUEST_DB)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

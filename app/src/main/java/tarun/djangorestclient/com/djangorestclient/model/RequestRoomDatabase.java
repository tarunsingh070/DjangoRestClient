package tarun.djangorestclient.com.djangorestclient.model;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import tarun.djangorestclient.com.djangorestclient.model.entity.Header;
import tarun.djangorestclient.com.djangorestclient.model.entity.Request;

@Database(entities = {Request.class, Header.class}, version = 1, exportSchema = true)
@TypeConverters({Converters.class})
public abstract class RequestRoomDatabase extends RoomDatabase {

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

    private static Callback roomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // Populate the database in the background on the first time db is created.

//                    RequestDao requestDao = INSTANCE.requestDao();
//
//                    requestDao.deleteAllRequestsAndHeaders();
//
//                    Request request1 = new Request("https://google.com", Request.RequestType.PUT,
//                            "Here's a body text!", false, true, new Date());
//
//                    Header header1 = new Header("Accept", "21");
//                    Header header2 = new Header("Authorization (Basic)", "Basic dXNyOnB3ZA==");
//                    Header header3 = new Header("custom name", "custom value");
//                    List<Header> headers = new ArrayList<>();
//                    headers.add(header1);
//                    headers.add(header2);
//                    headers.add(header3);
//
//                    request1.getHeaders().addAll(headers);
//
//                    Request request2 = new Request("https://imdb.com", Request.RequestType.GET,
//                            null, false, true, new Date());
//
//                    requestDao.insertRequestWithHeaders(request1);
//                    requestDao.insertRequestWithHeaders(request2);
                }
            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // Populate the database in the background on the first time db is opened.

//                    RequestDao requestDao = INSTANCE.requestDao();
//
//                    requestDao.deleteAllRequestsAndHeaders();
//
//                    Request request1 = new Request("https://google.com", Request.RequestType.PUT,
//                            "Here's a body text!", true, false, new Date());
//
//                    Header header1 = new Header("Accept", "21");
//                    Header header2 = new Header("Authorization (Basic)", "Basic dXNyOnB3ZA==");
//                    Header header3 = new Header("custom name", "custom value");
//                    List<Header> headers = new ArrayList<>();
//                    headers.add(header1);
//                    headers.add(header2);
//                    headers.add(header3);
//
//                    request1.getHeaders().addAll(headers);
//
//                    Request request2 = new Request("https://imdb.com", Request.RequestType.GET,
//                            null, false, true, new Date());
//
//                    requestDao.insertRequestWithHeaders(request1);
//                    requestDao.insertRequestWithHeaders(request2);
                }
            });
        }
    };

    static RequestRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RequestRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RequestRoomDatabase.class, "request_database")
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

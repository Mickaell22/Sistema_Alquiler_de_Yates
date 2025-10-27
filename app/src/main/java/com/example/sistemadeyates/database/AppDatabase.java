package com.example.sistemadeyates.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.sistemadeyates.database.dao.ActivityLogDao;
import com.example.sistemadeyates.database.dao.UserDao;
import com.example.sistemadeyates.models.ActivityLog;
import com.example.sistemadeyates.models.User;
import com.example.sistemadeyates.utils.ConfigManager;

@Database(entities = {User.class, ActivityLog.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract ActivityLogDao activityLogDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            ConfigManager config = ConfigManager.getInstance(context);
            String dbName = config.get("DB_NAME", "yates_db");

            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    dbName
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }

    public static void destroyInstance() {
        if (instance != null && instance.isOpen()) {
            instance.close();
        }
        instance = null;
    }
}

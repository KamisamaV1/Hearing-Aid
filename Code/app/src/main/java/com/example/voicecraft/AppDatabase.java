package com.example.voicecraft;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Calibration.class},version=35)
public abstract class AppDatabase extends RoomDatabase {
    private static final String dbName="AppDB";
    private static AppDatabase appDatabase;
    public static synchronized AppDatabase getAppDatabase(Context context)
    {
        if(appDatabase==null)
        {
            appDatabase= Room.databaseBuilder(context,AppDatabase.class,dbName).fallbackToDestructiveMigration().build();
        }
        return appDatabase;
    }
    public abstract UserDao userDao();
    public abstract CalibrationDao calibrationDao();
}

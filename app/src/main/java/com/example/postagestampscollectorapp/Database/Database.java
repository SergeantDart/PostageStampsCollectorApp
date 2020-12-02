package com.example.postagestampscollectorapp.Database;

import androidx.room.RoomDatabase;

import com.example.postagestampscollectorapp.Data.PostageStamp;
import com.example.postagestampscollectorapp.Data.StampsCollection;
import com.example.postagestampscollectorapp.Data.User;

@androidx.room.Database(entities = {User.class, PostageStamp.class, StampsCollection.class}, version = 3, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract PostageStampDao postageStampDao();

    public abstract StampsCollectionDao stampsCollectionDao();
}

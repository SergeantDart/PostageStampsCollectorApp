package com.example.postagestampscollectorapp.Database;

import androidx.room.RoomDatabase;

import com.example.postagestampscollectorapp.PostageStamp;
import com.example.postagestampscollectorapp.PostageStampDao;
import com.example.postagestampscollectorapp.StampsCollection;
import com.example.postagestampscollectorapp.StampsCollectionDao;
import com.example.postagestampscollectorapp.User;
import com.example.postagestampscollectorapp.UserDao;

@androidx.room.Database(entities = {User.class, PostageStamp.class, StampsCollection.class}, version = 3, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract PostageStampDao postageStampDao();

    public abstract StampsCollectionDao stampsCollectionDao();
}

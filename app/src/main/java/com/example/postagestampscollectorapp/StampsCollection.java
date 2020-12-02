package com.example.postagestampscollectorapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "Collections")
public class StampsCollection implements Parcelable {

    //static int generatorNo = 10000;
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int collectionId;
    @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId")
    @ColumnInfo(name = "userId")
    int userId;
    @ColumnInfo(name = "name")
    String collectionName;
    @Ignore
    List<PostageStamp> stampsList;
    @ColumnInfo(name = "isPrivate")
    boolean isPrivate;
    @ColumnInfo(name = "description")
    String collectionDescription;


    protected StampsCollection(Parcel in) {
        this.collectionId = in.readInt();
        this.userId = in.readInt();
        this.collectionName = in.readString();
        this.stampsList = new ArrayList<>();
        in.readList(this.stampsList, PostageStamp.class.getClassLoader());
        this.isPrivate = in.readByte() != 0;
        this.collectionDescription = in.readString();
    }

    StampsCollection(String collectionName, int userId, List<PostageStamp> stampsList, boolean isPrivate, String collectionDescription) {
        this.userId = userId;
        this.collectionName = collectionName;
        this.stampsList = stampsList;
        this.isPrivate = isPrivate;
        this.collectionDescription = collectionDescription;
    }

    public StampsCollection(int userId, String collectionName, boolean isPrivate, String collectionDescription) {
        this.userId = userId;
        this.collectionName = collectionName;
        this.stampsList = null;
        this.isPrivate = isPrivate;
        this.collectionDescription = collectionDescription;
    }

    public static final Creator<StampsCollection> CREATOR = new Creator<StampsCollection>() {
        @Override
        public StampsCollection createFromParcel(Parcel in) {
            return new StampsCollection(in);
        }

        @Override
        public StampsCollection[] newArray(int size) {
            return new StampsCollection[size];
        }
    };

    @Override
    public String toString() {
        return "StampsCollection{" +
                "collectionId=" + collectionId +
                ", userId=" + userId +
                ", collectionName='" + collectionName + '\'' +
                ", stampsList=" + stampsList +
                ", isPrivate=" + isPrivate +
                ", collectionDescription='" + collectionDescription + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(collectionId);
        dest.writeInt(userId);
        dest.writeString(collectionName);
        dest.writeList(stampsList);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeString(collectionDescription);
    }
}
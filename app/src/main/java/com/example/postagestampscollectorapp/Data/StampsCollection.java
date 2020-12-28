package com.example.postagestampscollectorapp.Data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.api.Distribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity(tableName = "Collections")
public class StampsCollection implements Parcelable {

    //static int generatorNo = 10000;
    @PrimaryKey
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

    @Ignore
    public StampsCollection() {
        Random rand = new Random ();
        this.collectionId = ( rand.nextInt(999999) + rand.nextInt(10) - rand.nextInt(60 ))* (rand.nextInt(5) + 1);
        this.userId = 0;
        this.collectionName = "N/A";
        this.stampsList = null;
        this.isPrivate = false;
        this.collectionDescription = "N/A";
    }

    public StampsCollection(int userId, String collectionName, boolean isPrivate, String collectionDescription) {
        Random rand = new Random ();
        this.collectionId = ( rand.nextInt(999999) + collectionName.length() - collectionDescription.length() ) * (rand.nextInt(5) + 1);
        this.userId = userId;
        this.collectionName = collectionName;
        this.stampsList = null;
        this.isPrivate = isPrivate;
        this.collectionDescription = collectionDescription;
    }

    @Ignore
    public StampsCollection(int collectionId, int userId, String collectionName, List<PostageStamp> stampsList, boolean isPrivate, String collectionDescription) {
        this.collectionId = collectionId;
        this.userId = userId;
        this.collectionName = collectionName;
        this.stampsList = stampsList;
        this.isPrivate = isPrivate;
        this.collectionDescription = collectionDescription;
    }

    public StampsCollection(Parcel in) {
        this.collectionId = in.readInt();
        this.userId = in.readInt();
        this.collectionName = in.readString();
        this.stampsList = new ArrayList<>();
        in.readList(this.stampsList, PostageStamp.class.getClassLoader());
        this.isPrivate = in.readByte() != 0;
        this.collectionDescription = in.readString();
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

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<PostageStamp> getStampsList() {
        return stampsList;
    }

    public void setStampsList(List<PostageStamp> stampsList) {
        this.stampsList = stampsList;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getCollectionDescription() {
        return collectionDescription;
    }

    public void setCollectionDescription(String collectionDescription) {
        this.collectionDescription = collectionDescription;
    }
}
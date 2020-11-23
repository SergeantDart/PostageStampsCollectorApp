package com.example.postagestampscollectorapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class StampsCollection implements Parcelable {

    static int generatorNo = 10000;
    int collectionId;
    String collectionName;
    ArrayList<PostageStamp> stampsList;
    boolean isPrivate;
    String collectionDescription;

    protected StampsCollection(Parcel in) {
        this.collectionId = in.readInt();
        this.collectionName = in.readString();
        this.stampsList = new ArrayList<PostageStamp>();
        in.readList(this.stampsList, PostageStamp.class.getClassLoader());
        this.isPrivate = in.readByte() != 0;
        this.collectionDescription = in.readString();
    }

    StampsCollection(String collectionName, ArrayList<PostageStamp> stampsList, boolean isPrivate, String collectionDescription) {
        this.collectionId = generatorNo++;
        this.collectionName = collectionName;
        this.stampsList = stampsList;
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
                ", collectionName='" + collectionName + '\'' +
                ", stampCollection=" + stampsList +
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
        dest.writeString(collectionName);
        dest.writeList(stampsList);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeString(collectionDescription);
    }
}
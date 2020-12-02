package com.example.postagestampscollectorapp.Data;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.postagestampscollectorapp.Others.BitmapUtilities;

import java.io.ByteArrayOutputStream;


@Entity(tableName = "Stamps")
public
class PostageStamp implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int stampId;
    @ForeignKey(entity = StampsCollection.class, parentColumns = "id", childColumns = "stampId")
    @ColumnInfo(name = "collectionId")
    int collectionId;
    @ColumnInfo(name = "name")
    String name;
    @ColumnInfo(name = "pic")
    byte[] picBytes;
    @ColumnInfo(name = "year")
    int year;
    @ColumnInfo(name = "country")
    String country;
    @ColumnInfo(name = "description")
    String description;

    public PostageStamp(int collectionId, String name, byte[] picBytes, int year, String country, String description) {
        this.collectionId = collectionId;
        this.name = name;
        this.picBytes = picBytes;
        this.year = year;
        this.country = country;
        this.description = description;
    }

    protected PostageStamp(Parcel in) {
        stampId = in.readInt();
        collectionId = in.readInt();
        name = in.readString();
        int contentBytesLength = in.readInt();
        picBytes = new byte[contentBytesLength];
        in.readByteArray(picBytes);
        //pic = BitmapFactory.decodeByteArray(contentBytes, 0, contentBytes.length);
        year = in.readInt();
        country = in.readString();
        description = in.readString();
    }

    public static final Parcelable.Creator<PostageStamp> CREATOR = new Parcelable.Creator<PostageStamp>() {
        @Override
        public PostageStamp createFromParcel(Parcel in) {
            return new PostageStamp(in);
        }

        @Override
        public PostageStamp[] newArray(int size) {
            return new PostageStamp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stampId);
        dest.writeInt(collectionId);
        dest.writeString(name);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap pic = BitmapUtilities.getBitmap(picBytes);
        pic.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        byte[] byteArray = stream.toByteArray();
        dest.writeInt(byteArray.length);
        dest.writeByteArray(byteArray);
        dest.writeInt(year);
        dest.writeString(country);
        dest.writeString(description);
    }

    public int getStampId() {
        return stampId;
    }

    public void setStampId(int stampId) {
        this.stampId = stampId;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPicBytes() {
        return picBytes;
    }

    public void setPicBytes(byte[] picBytes) {
        this.picBytes = picBytes;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

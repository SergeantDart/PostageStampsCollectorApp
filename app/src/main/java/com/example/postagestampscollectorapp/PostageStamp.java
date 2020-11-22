package com.example.postagestampscollectorapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

public class PostageStamp implements Parcelable {
    static int generatorNo = 100;
    int id;
    String name;
    Bitmap pic;
    int year;
    String country;
    String description;


    PostageStamp(String name,  Bitmap pic, int year, String country, String description) {
        this.id = generatorNo++;
        this.name = name;
        this.pic = pic;
        this.year = year;
        this.country = country;
        this.description = description;
    }

    protected PostageStamp(Parcel in) {
        id = in.readInt();
        name = in.readString();
        final int contentBytesLength = in.readInt();
        byte[] contentBytes = new byte[contentBytesLength];
        in.readByteArray(contentBytes);
        pic = BitmapFactory.decodeByteArray(contentBytes, 0, contentBytes.length);
        year = in.readInt();
        country = in.readString();
        description = in.readString();
    }

    public static final Creator<PostageStamp> CREATOR = new Creator<PostageStamp>() {
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
    public String toString() {
        return "PostageStamp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pic=" + pic +
                ", year=" + year +
                ", country='" + country + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        byte[]byteArray = stream.toByteArray();
        dest.writeInt(byteArray.length);
        dest.writeByteArray(byteArray);
        dest.writeInt(year);
        dest.writeString(country);
        dest.writeString(description);
    }
}

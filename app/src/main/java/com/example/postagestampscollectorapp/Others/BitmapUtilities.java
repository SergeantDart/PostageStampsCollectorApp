package com.example.postagestampscollectorapp.Others;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

    //utility class
    public class BitmapUtilities {

        //convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
            return stream.toByteArray();
        }

        //convert from byte array to bitmap
        public static Bitmap getBitmap(byte[] image) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
}

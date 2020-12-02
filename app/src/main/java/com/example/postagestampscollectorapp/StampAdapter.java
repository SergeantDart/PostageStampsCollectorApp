
package com.example.postagestampscollectorapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class StampAdapter extends BaseAdapter {

    Context context;
    List<PostageStamp> stampsList;

    StampAdapter(Context context, List<PostageStamp> stampsList) {
        this.context = context;
        this.stampsList = stampsList;
    }

    @Override
    public int getCount() {
        return stampsList.size();
    }

    @Override
    public Object getItem(int position) {
        return stampsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PostageStamp stamp = (PostageStamp)getItem(position);
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View generatedView = layoutInflater.inflate(R.layout.stamp_listview_item,parent,false);

        TextView stampNameTextView = generatedView.findViewById(R.id.stampNameTextView);
        TextView stampYearTextView = generatedView.findViewById(R.id.stampYearTextView);
        TextView stampCountryTextView = generatedView.findViewById(R.id.stampCountryTextView);
        ImageView stampImageView = generatedView.findViewById(R.id.stampImageView);

        stampNameTextView.setText(stamp.name.toString());
        stampYearTextView.setText(String.valueOf(stamp.year));
        stampCountryTextView.setText(stamp.country.toString());

        Bitmap stampPic = BitmapUtilities.getBitmap(stamp.picBytes);
        stampImageView.setImageBitmap(stampPic);

        return generatedView;
    }
}

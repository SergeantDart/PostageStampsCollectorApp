
package com.example.postagestampscollectorapp.Others;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.postagestampscollectorapp.Data.PostageStamp;
import com.example.postagestampscollectorapp.Others.BitmapUtilities;
import com.example.postagestampscollectorapp.R;

import java.util.List;

//listview postage stamp adapter
public class StampAdapter extends BaseAdapter {

    Context context;
    List<PostageStamp> stampsList;

    public StampAdapter(Context context, List<PostageStamp> stampsList) {
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

    //the postage stamp's image will be decompressed at listview item level from the byte array
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PostageStamp stamp = (PostageStamp)getItem(position);
        //layout inflater is a helper tool designed to help us create a listview item
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        //generate the listview item based on a preset layout and populate it with data from the associated list
        View generatedView = layoutInflater.inflate(R.layout.stamp_listview_item,parent,false);

        TextView stampNameTextView = generatedView.findViewById(R.id.stampNameTextView);
        TextView stampYearTextView = generatedView.findViewById(R.id.stampYearTextView);
        TextView stampCountryTextView = generatedView.findViewById(R.id.stampCountryTextView);
        ImageView stampImageView = generatedView.findViewById(R.id.stampImageView);

        stampNameTextView.setText(stamp.getName().toString());
        stampYearTextView.setText(String.valueOf(stamp.getYear()));
        stampCountryTextView.setText(stamp.getCountry().toString());

        Bitmap stampPic = BitmapUtilities.getBitmap(stamp.getPicBytes());
        stampImageView.setImageBitmap(stampPic);

        return generatedView;
    }
}

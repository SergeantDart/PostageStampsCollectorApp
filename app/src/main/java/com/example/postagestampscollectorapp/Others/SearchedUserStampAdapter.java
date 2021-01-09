package com.example.postagestampscollectorapp.Others;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.postagestampscollectorapp.Activities.LoginActivity;
import com.example.postagestampscollectorapp.Data.PostageStamp;
import com.example.postagestampscollectorapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

//adapter for the searched user stamps, which are not stored in SQLite and are directly fetched and displayed from Firebase DB and Firestore
public class SearchedUserStampAdapter extends BaseAdapter {

    Context context;
    List<PostageStamp> postageStampsList;
    FirebaseDatabase fbDatabase;
    FirebaseStorage firebaseStorage;

    public SearchedUserStampAdapter(Context context, List<PostageStamp> postageStampsList, FirebaseDatabase fbDatabase, FirebaseStorage firebaseStorage){
        this.context = context;
        this.postageStampsList = postageStampsList;
        this.fbDatabase = fbDatabase;
        this.firebaseStorage = firebaseStorage;
    }
    @Override
    public int getCount() {
        return postageStampsList.size();
    }

    @Override
    public Object getItem(int position) {
        return postageStampsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

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

        //download the image from the Firebase Storage based on the stampPicUri ( image address in Firebase Storage )
        StorageReference storageReference = firebaseStorage.getReference(stamp.getStampPicUri());
        storageReference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            //we create the postage stamp object once the stamp picture is SUCCESFULLY downloaded from Firebase Storage
            @Override
            public void onSuccess(byte[] bytes) {
                byte[] picBytes = bytes;
                stampImageView.setImageBitmap(BitmapUtilities.getBitmap(picBytes));
            }
        });
        return generatedView;
    }
}

package com.example.postagestampscollectorapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.room.Room;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.postagestampscollectorapp.Data.PostageStamp;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.Database.PostageStampDao;
import com.example.postagestampscollectorapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChartActivity extends AppCompatActivity {



    //hash map used to store country-frequences as key-value
    HashMap<String, Float> byCountryFrequenciesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        byCountryFrequenciesMap = (HashMap<String, Float>) getIntent().getSerializableExtra("byCountryFrequenciesMap");

        setContentView(new StampDistributionByCountryChart(this));
    }

    protected class StampDistributionByCountryChart extends View {

        public StampDistributionByCountryChart(Context context) {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //used for multiple purposes: avoiding rebundancy, keeping track wether it's the first circle arc or not
            int currentIndex = 0;
            //used to hold the starting angle
            float temp = 0;
            //used to hold the previous angle value
            float previousValue = 0;

            Paint paint = new Paint();

            paint.setStyle(Paint.Style.FILL);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            RectF rectF = new RectF(220, 400, 920, 1100);

            //setting circle coordinates
            float centerX = (rectF.left + rectF.right) / 2;
            float centerY = (rectF.top + rectF.bottom) / 2;
            float radius = (rectF.right - rectF.left) / 2;

            //drawing each arc/portion of the chart
            for (Map.Entry<String, Float> pair : byCountryFrequenciesMap.entrySet()) {
                if (currentIndex > 0) {
                    temp += previousValue;
                }

                Toast.makeText(getApplicationContext(), pair.getKey() + " : " + pair.getValue(), Toast.LENGTH_SHORT).show();

                //generate a random color for each segment of the chart
                Random random = new Random();
                paint.setColor(Color.argb(200, random.nextInt(256), random.nextInt(256), random.nextInt(256)));


                canvas.drawArc(rectF, temp, pair.getValue(), true, paint);

                //set text style and color
                paint.setColor(Color.BLACK);
                paint.setTextSize(30);

                //this angle will place the text in the center of the arc.
                float medianAngle = (temp + (pair.getValue() / 2f)) * (float)Math.PI / 180f;
                canvas.drawText(pair.getKey(), (float)(centerX + (radius * Math.cos(medianAngle))), (float)(centerY + (radius * Math.sin(medianAngle))), paint);

                previousValue = pair.getValue();
                currentIndex++;
            }
        }
    }
}

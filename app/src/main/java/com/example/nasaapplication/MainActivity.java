package com.example.nasaapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String NASA_REQUEST_URL = "https://api.nasa.gov/planetary/apod?start_date=2020-01-01&end_date=2020-01-1&api_key=DEMO_KEY";
    private String NasaUrlBase = "https://api.nasa.gov/planetary/apod?start_date=";
    private String NasaUrlDate = "";

    Date currentDate;
    DateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd");

    TextView dateTextView;
    TextView imgTitleTextView;
    ImageView imageView;

    String getMyDate;
    String getMyTitle;
    String getMyUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentDate = Calendar.getInstance().getTime();
        NasaUrlDate = dateFormat.format(currentDate);
        Log.i(LOG_TAG, NasaUrlDate  + " yay!");


        dateTextView = (TextView) findViewById(R.id.myDate);
        imgTitleTextView = (TextView) findViewById(R.id.myImgTitle);
        imageView = (ImageView) findViewById(R.id.myImage);
        Button button = (Button) findViewById(R.id.myButton);

        ApodAsyncTask task = new ApodAsyncTask();
        task.execute(NasaUrlDate);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NasaUrlDate = getRandomMonthDate();

                ApodAsyncTask task1 = new ApodAsyncTask();
                task1.execute(NasaUrlDate);
            }
        });



    }

    private class ApodAsyncTask extends AsyncTask<String, Void, MyApod>{

        @Override
        protected MyApod doInBackground(String... myUrl){
            if (myUrl.length< 1 || myUrl[0] == null){
                return null;
            }

            MyApod result = QueryUtils.fetchNASAData(myUrl[0]);

            return result;
        }

        @Override
        protected void onPostExecute(MyApod myApod){

            getMyDate = myApod.getMyDate();
            getMyUrl = myApod.getMyURL();
            getMyTitle = myApod.getMyTitle();


            dateTextView.setText(getMyDate);
            imgTitleTextView.setText(getMyTitle);
            Picasso.with(getApplicationContext()).load(getMyUrl).into(imageView);
        }


    }

    private String getRandomMonthDate() {
        String myDate;

        Random randYr = new Random();
        int intYr = randYr.nextInt(25) + 1996;
        Random randMo = new Random();
        int intMo = randMo.nextInt(11) + 1;

        int upboundDate = 0;

        switch (intMo) {
            case 1: case 3: case 5:
            case 7: case 8: case 10:
            case 12:
                upboundDate = 30;
                break;

            case 4: case 6:
            case 9: case 11:
                upboundDate = 29;
                break;

            case 2:
                if (((intYr % 4 == 0) &&
                        !(intYr  % 100 == 0))
                        || (intYr  % 400 == 0))
                    upboundDate = 28;
                else
                    upboundDate = 27;
                break;
        }

        Random randDt = new Random();
        int intDt = randDt.nextInt(upboundDate) + 1;
        myDate = Integer.toString(intYr) + "-" + Integer.toString(intMo) + "-" + Integer.toString(intDt) ;

        return myDate;
    }

}
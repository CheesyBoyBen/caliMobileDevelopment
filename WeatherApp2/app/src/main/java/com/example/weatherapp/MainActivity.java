
/*  Starter project for Mobile Platform Development in main diet 2023/2024
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Ben Lockhart
// Student ID           S2030347
// Programme of Study   BSc (Hons) Computer Games (Software Development)
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.TimeZone;

import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private TextView currentDisplay;
    private TextView Day3Display1;
    private TextView Day3Display2;
    private Button TodayButton;
    private Button TomorrowButton;
    private Button LastButton;
    private TextView Day3Display3;
    private String day1 = "";
    private String day2 = "";
    private String day3 = "";
    private String url1="";
    private String location = "2648579";
    private String currenturlSource;
    private String day3urlSource;
    private Menu options;
    private ImageView image;
    private String thirdDay;

    TimeZone timeZone = TimeZone.getTimeZone("Etc/GMT");

    private String currentHeadline;
    private String dayHeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("MyTag", "onCreate ran");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        currentDisplay = (TextView)findViewById(R.id.currentDisplay);
        Day3Display1 = (TextView)findViewById(R.id.Day3Display1);
        Day3Display2 = (TextView)findViewById(R.id.Day3Display2);
        Day3Display3 = (TextView)findViewById(R.id.Day3Display3);

        image = (ImageView)findViewById(R.id.imageView);

        TodayButton = (Button)findViewById(R.id.Today);
        TomorrowButton = (Button)findViewById(R.id.Tomorrow);
        LastButton = (Button)findViewById(R.id.button3);
        TodayButton.setOnClickListener(this);
        TomorrowButton.setOnClickListener(this);
        LastButton.setOnClickListener(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        currenturlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/2648579";
        day3urlSource="https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2648579";
        // Glasgow Default

        startProgress();

    }

    public void onClick(View aview)
    {

        if (aview == TodayButton){
            if (Day3Display1.getVisibility() == View.VISIBLE){
                Day3Display1.setVisibility(View.INVISIBLE);
            }
            else if (Day3Display1.getVisibility() == View.INVISIBLE){
                Day3Display1.setVisibility(View.VISIBLE);
            }

        }
        if (aview == TomorrowButton){
            if (Day3Display2.getVisibility() == View.VISIBLE){
                Day3Display2.setVisibility(View.INVISIBLE);
            }
            else if (Day3Display2.getVisibility() == View.INVISIBLE){
                Day3Display2.setVisibility(View.VISIBLE);
            }
        }
        if (aview == LastButton){
            if (Day3Display3.getVisibility() == View.VISIBLE){
                Day3Display3.setVisibility(View.INVISIBLE);
            }
            else if (Day3Display3.getVisibility() == View.INVISIBLE){
                Day3Display3.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        options = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == R.id.glasgow) {
            Toast.makeText(this, "Glasgow", Toast.LENGTH_LONG).show();
            location = "2648579";
            timeZone = TimeZone.getTimeZone("Etc/GMT");
        } else if (itemId == R.id.london) {
            Toast.makeText(this, "London", Toast.LENGTH_LONG).show();
            location = "2643743";
            timeZone = TimeZone.getTimeZone("Etc/GMT");
        } else if (itemId == R.id.newyork) {
            Toast.makeText(this, "New York", Toast.LENGTH_LONG).show();
            location = "5128581";
            timeZone = TimeZone.getTimeZone("Etc/GMT-4");
        } else if (itemId == R.id.oman) {
            Toast.makeText(this, "Oman", Toast.LENGTH_LONG).show();
            location = "287286";
            timeZone = TimeZone.getTimeZone("Etc/GMT+4");
        } else if (itemId == R.id.mauritius) {
            Toast.makeText(this, "mauritius", Toast.LENGTH_LONG).show();
            location = "934154";
            timeZone = TimeZone.getTimeZone("Etc/GMT+4");
        } else if (itemId == R.id.bangladesh) {
            Toast.makeText(this, "bangladesh", Toast.LENGTH_LONG).show();
            location = "1185241";
            timeZone = TimeZone.getTimeZone("Etc/GMT+6");
        } else {
            return super.onOptionsItemSelected(item);
        }

        updateLocation();

        return true;
    }

    public void updateLocation(){
        currenturlSource = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/" + location;
        day3urlSource="https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + location;

        startProgress();
    }
    public void startProgress()
    {
        day1 = "";
        day2 = "";
        day3 = "";
        // Run network access on a separate thread;
        new Thread(new currentTask(currenturlSource)).start();
        new Thread(new day3Task(day3urlSource)).start();

    } //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class currentTask implements Runnable
    {
        private String url;
        private String currentresult;

        public currentTask(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            currentHeadline = "";

            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    currentresult = currentresult + inputLine;
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            Log.e("testing", currentresult);

            int i = currentresult.indexOf("<title>");
            currentresult = currentresult.substring(i+50);

            i = currentresult.indexOf("<title>");
            int j = currentresult.indexOf("</title>");

            currentHeadline = currentresult.substring(i+9, j);

            i = currentresult.indexOf("<description>T");
            currentresult = currentresult.substring(i+13);

            i = currentresult.indexOf("</");
            currentresult = currentresult.substring(0, i);
            Log.e("MyTag - cleaned",currentresult);


            Log.e("MyTag - cleaned",currentresult);

            currentresult = currentresult.replace(", ", "\n");

            currentresult += "\n\n\n\n";

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    //urrentDisplay.setText(result);
                    currentDisplay.setText(currentresult);

                    currentHeadline();
                }
            });
        }
    }


    private class day3Task implements Runnable
    {
        private String url;
        private String Day3result;
        private String currentTime;
        private String sunsetTime;
        public day3Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            dayHeadline = "";
            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    Day3result = Day3result + inputLine;
                    Log.e("MyTag",inputLine);


                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            Log.e("testing", Day3result);

            int i;
            int j;

            i = Day3result.indexOf("<pubDate>");
            j = Day3result.indexOf("</pubDate>");

            currentTime = Day3result.substring(i, j);

            i = Day3result.indexOf("Sunset");

            sunsetTime = Day3result.substring(i, i+13);


            i = Day3result.indexOf("<title>");
            Day3result = Day3result.substring(i+50);
            i = Day3result.indexOf("<title>");
            Day3result = Day3result.substring(i+50);

            i = Day3result.indexOf("<title>");
            j = Day3result.indexOf("</title>");

            dayHeadline += Day3result.substring(i+7, j);

            i = Day3result.indexOf("<description>M");
            Day3result = Day3result.substring(i);

            i = Day3result.indexOf("<title>");
            j = Day3result.indexOf("</title>");

            dayHeadline += Day3result.substring(i+7, j);

            i = Day3result.indexOf("<description>M");
            j = Day3result.indexOf("</description>");
            day1 += Day3result.substring(i+13, j);
            //day1 += "\n\n";
            Day3result = Day3result.substring(j+13);

            i = Day3result.indexOf("<title>");
            j = Day3result.indexOf("</title>");

            dayHeadline += Day3result.substring(i+7, j);

            i = Day3result.indexOf("<description>M");
            j = Day3result.indexOf("</description>");

            day2 += Day3result.substring(i+13, j);
            //day1 += "\n\n";
            Day3result = Day3result.substring(j+13);

            i = Day3result.indexOf("<description>M");
            j = Day3result.indexOf("</description>");

            day3 += Day3result.substring(i+13, j);



            Log.e("MyTag - cleaned",Day3result);


            day1 = day1.replace(", ", "\n\n");
            day2 = day2.replace(", ", "\n\n");
            day3 = day3.replace(", ", "\n\n");


            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    Day3Display1.setText(day1);
                    Day3Display2.setText(day2);
                    Day3Display3.setText(day3);

                    dayHeadline();
                    setImage(currentTime, sunsetTime);
                }
            });
        }

    }

    void currentHeadline(){
        if(currentHeadline != null) {
            currentHeadline = currentHeadline.substring(currentHeadline.indexOf(": "), currentHeadline.indexOf(","));

            //Log.e("headline", currentHeadline);
        }
    }

    void dayHeadline(){
        if(dayHeadline != null) {

            Log.e("headline", dayHeadline);


            String day1 = dayHeadline.substring(dayHeadline.indexOf(": ") + 1, dayHeadline.indexOf(","));
            dayHeadline = dayHeadline.substring(dayHeadline.indexOf(",") + 1);
            day1 += dayHeadline.substring(dayHeadline.indexOf("y: ") + 1, dayHeadline.indexOf(","));
            dayHeadline = dayHeadline.substring(dayHeadline.indexOf(",") + 1);
            day1 += dayHeadline.substring(dayHeadline.indexOf("y: ") + 1, dayHeadline.indexOf(","));

            dayHeadline = dayHeadline.substring(dayHeadline.indexOf(")") + 1);
            thirdDay = dayHeadline.substring(dayHeadline.indexOf(")") + 1, dayHeadline.indexOf("y") +1);

            LastButton.setText(thirdDay);

            dayHeadline = day1;

            //Log.e("headline", dayHeadline);
        }
    }


    void setImage(String curTime, String sunTime){
        int i;

        i = curTime.indexOf(":");
        curTime = curTime.substring(i-2, i+3);

        sunTime = sunTime.substring(8);

        curTime = curTime.replace(":", "");
        sunTime = sunTime.replace(":", "");

        int offset = timeZone.getOffset(1, 2018, 2, 2, 2, 300);
        offset /= -36000;


        if ((Integer.parseInt(curTime)) + offset > (Integer.parseInt(sunTime))){
            Log.e("Time", "sunset");
            image.setImageResource(R.drawable.night_clear);
        }
        else{
            Log.e("Time", "daylight");
            image.setImageResource(R.drawable.day_clear);
        }
    }
}
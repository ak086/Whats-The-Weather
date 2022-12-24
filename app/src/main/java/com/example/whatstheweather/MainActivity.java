package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    TextView editText;
    TextView resultText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=findViewById(R.id.editTextView);
        resultText=findViewById(R.id.textView2);
    }
    public void getWeather(View view) {
        try {
        DownloadTask task=new DownloadTask();
            String encodeURL= URLEncoder.encode(editText.getText().toString(),"UTF-8");
        task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodeURL + "&appid=51d58c207cc4430d4cf872da5b6624f6");

        InputMethodManager mgr=(InputMethodManager)  getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not found the weather",Toast.LENGTH_SHORT).show();
        }

    }
    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char ch = (char) data;
                    result += ch;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                JSONObject jsonObject=new JSONObject(s);
                String weatherInfo=jsonObject.getString("weather");
                Log.i("Weather Content",weatherInfo);
                JSONArray array=new JSONArray(weatherInfo);
                String message="";

                for(int i=0;i<array.length();i++){
                    JSONObject jsonPart=array.getJSONObject(i);
                    String main=jsonPart.getString("main");
                    String description=jsonPart.getString("description");
                    // Log.i("main",jsonPart.getString("main"));
                    // Log.i("description",jsonPart.getString("description"));
                    if(!main.equals("") && !description.equals("")) {
                        message+= main + ": " + description + "\r\n";
                    }
                }
                if(!message.equals("")){
                    resultText.setText(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
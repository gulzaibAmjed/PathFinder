package com.example.bc110403238.routeapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bc110403238.routeapp.Controller.SingltonClass;
import com.example.bc110403238.routeapp.Controller.SpeakOut;
import com.example.bc110403238.routeapp.Controller.secondSinglton;
import com.example.bc110403238.routeapp.Modal.MapDictionery;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.*;

public class MapsActivity extends FragmentActivity {

    private TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;
    private TextView txtSpeechInput;
    public Button btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String start = null;
    String end = null;
    private JSONObject jsonObject = null;
    MapDictionery mapDictionery = null;
    SpeakOut speakOut = null;
    Button b1, b2;
    GoogleMap map;
    boolean flag;
    ArrayList<LatLng> markerPoints;
    String TAG = "MAP";
    //private RadioGroup group;
    //RadioButton button;
    RadioButton rbDriving;
    RadioButton rbBiCycling;
    RadioButton rbWalking;
    String mode = "";

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        rbWalking = (RadioButton) findViewById(R.id.rb_walking);

        rbDriving = (RadioButton) findViewById(R.id.rb_driving);

        rbBiCycling = (RadioButton) findViewById(R.id.rb_bicycling);


        flag = isNetworkConnected();
        if (flag != true) {
            Toast.makeText(getApplicationContext(), "Please get  Internet connection",
                    Toast.LENGTH_LONG).show();
        }

        b1 = (Button) findViewById(R.id.camera);

        b1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(MapsActivity.this, facedetect.class);
                startActivity(intent);
            }
        });
        /*setContentView(R.layout.activity_maps);
        b2 = (Button) findViewById(R.id.speak1);
        b2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(MapsActivity.this,Pathparse.class);
                startActivity(intent);
            }
        });
*/


        // Initializing
        markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();

        if (map != null) {

            // Enable MyLocation Button in the Map
            map.setMyLocationEnabled(true);

            // Setting onclick event listener for the map
            map.setOnMapClickListener(new OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {

                    // Already two locations
                    if (markerPoints.size() > 1) {
                        markerPoints.clear();
                        map.clear();
                    }

                    // Adding new item to the ArrayList
                    markerPoints.add(point);

                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(point);

                    /**
                     * For the start location, the color of marker is GREEN and
                     * for the end location, the color of marker is RED.
                     */
                    if (markerPoints.size() == 1) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else if (markerPoints.size() == 2) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    // Add new marker to the Google Map Android API V2
                    map.addMarker(options);

                    // Checks, whether start and end locations are captured
                    if (markerPoints.size() >= 2) {
                        LatLng origin = markerPoints.get(0);
                        LatLng dest = markerPoints.get(1);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                }
            });
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";


        if (rbDriving.isChecked()) {
            mode = "mode=driving";
        }
        if (rbWalking.isChecked()) {
            mode = "mode=walking";
        }
        if (rbBiCycling.isChecked()) {
            mode = "mode=BiCycling";
        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        // Toast.makeText(getApplicationContext(), "this is my Toast message pappu!!! =)"+url,
        // Toast.LENGTH_LONG).show();

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            System.out.print("Exception while downloading url" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            String myStr = null;
            SingltonClass sc = null;
            sc = SingltonClass.getMyObject();
            secondSinglton sc2 = null;
            sc2 = secondSinglton.getMyObject();

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                jsonObject = new JSONObject(data);
                System.out.println(jsonObject);

                String json = jsonObject.optString("status");



                if (json.equals("OK")) {
                    JSONArray jsonArray = jsonObject.optJSONArray("routes");


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONArray myArr = jsonObject1.optJSONArray("legs");

                        for (int j = 0; j < myArr.length(); j++) {

                            JSONObject jsonObject2 = myArr.getJSONObject(j);
                            JSONArray secondArr = jsonObject2.optJSONArray("steps");

                            sc.duration =jsonObject2.getString("duration");
                            sc.start = jsonObject2.getString("start_address");
                            sc.end = jsonObject2.getString("end_address");
                            sc.distance =jsonObject2.getString("distance");
                            for (int k = 0; k < secondArr.length(); k++) {

                                JSONObject jsonObject3 = secondArr.getJSONObject(k);
                                start = jsonObject3.getString("duration");
                                sc2.duration = jsonObject3.getString("duration");
                                sc2.distance = jsonObject3.getString("distance");
                                sc2.html =  jsonObject3.getString("html_instructions");
                                System.out.println("ok");
                            }


                            start = jsonObject2.getString("start_address");
                            end = jsonObject2.getString("end_address");
                          /*  if (start != null ) {
                                System.out.println("hello" + start);
                                speakWords(start);
                            }
                            else
                            { System.out.println("bye" + end);
                                speakWords(end);
                            }
                          */
                            mapDictionery = new MapDictionery();
                            mapDictionery.setStart(start);
                            mapDictionery.setEnd(end);
                            myStr = mapDictionery.getStart();

/*
                            mapDictionery.setMy_currentAddrss(start);
                            mapDictionery.setMy_currentAddrss2(end);
                            myStr = mapDictionery.getMy_currentAddrss();
*/


                            System.out.print("okay" + myStr);




                            System.out.println(sc.myValue);
                            System.out.println();


                            //speakWords(sc.myValue);
                            //Intent intent = new Intent(MapsActivity.this, facedetect.class);

                            //startActivity(intent);


                        }




                        /*    mapDictionery = new MapDictionery();

                            mapDictionery.setMy_currentAddrss(curretn_address);
                            String a =mapDictionery.getMy_currentAddrss();
                            Toast.makeText(getApplicationContext(),a, Toast.LENGTH_SHORT).show();
                        */
                    }





                    /*JSONArray jsonArray = jsonObject.optJSONArray("routes");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String curretn_address = jsonObject.getString("summary");


                        mapDictionery = new MapDictionery();

                        mapDictionery.setMy_currentAddrss(curretn_address);
                        String a =mapDictionery.getMy_currentAddrss();
                        Toast.makeText(getApplicationContext(),a, Toast.LENGTH_SHORT).show();
                    }*/
                }


            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            } finally {
            }

            return data;

        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                //routes.toString();
//                System.out.println(routes.get(0).contains("lat"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);


                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //===============================================================================================================
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }

    }

    public void setBtnSpeak(Button btnSpeak) {
        this.btnSpeak = btnSpeak;
        btnSpeak = (Button) findViewById(R.id.speak1);
        onClick(btnSpeak);
        speakWords(start);
    }

    public void onClick(View v) {
        try {
            //    System.out.println("oye chachu kam kr lyyyyyyyyyyyyyy.....12345");
            //get the text entered
            //    EditText enteredText = (EditText) findViewById(R.id.txtSpeechInput);
            //    String words = enteredText.getText().toString();

            speakWords(start);
        } catch (Exception e) {
            //    System.out.println("oye chachu kam kr lyyyyyyyyyyyyyy"+ e);
        }
    }

    public void speakWords(String speech) {
        System.out.println("ooooooooooooooooooooooooooooooo");
        System.out.println(speech);   //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //System.out.println("oye chachu kam kr lyyyyyyyyyyyyyy");
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    ///          System.out.println("oye chachu kam kr lyyyyyyyyyyyyyy");
                }
                break;
            }
        }

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                // myTTS = new TextToSpeech(this, this);
            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            //Toast.makeText(getApplicationContext(),
            //  getString(R.string.speech_not_supported),
            // Toast.LENGTH_SHORT).show();
        }
    }
}
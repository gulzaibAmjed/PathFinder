package com.example.bc110403238.routeapp;
import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Locale;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.example.bc110403238.routeapp.Controller.SingltonClass;

@SuppressLint("InflateParams")
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class facedetect extends Activity implements SurfaceHolder.Callback, OnClickListener, OnInitListener{


    
    private TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;
    private TextView txtSpeechInput;
    private Button btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    boolean preview = false;
    private LayoutInflater layoutInflater = null;
    private Button btnTakePicture;
    private TextView txtFaceCount;
    private TextView txtImagePath;

    SingltonClass sc = SingltonClass.getMyObject();
    String str = sc.start;
    String str2 = sc.end;

    final int RESULT_SAVEIMAGE = 0;


    /** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_facedetect);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (Button) findViewById(R.id.btnSpeak);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camPreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        txtFaceCount = (TextView)findViewById(R.id.tvFaceCount);
        txtImagePath = (TextView)findViewById(R.id.tvImagePath);

        layoutInflater = LayoutInflater.from(getBaseContext());
        final View viewControl = layoutInflater.inflate(R.layout.picture_control, null);
        LayoutParams layoutParamsControl
                = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

        btnTakePicture = (Button)findViewById(R.id.takepicture);
        btnTakePicture.setOnClickListener(new Button.OnClickListener(){


            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                camera.takePicture(mShutterCallback,
                        mRawPictureCallback, mJPGPictureCallback);
               promptSpeechInput();
                System.out.println( "voice command");
            }});

        Button speakButton = (Button) findViewById(R.id.btnspeak);
        //listen for clicks
        speakButton.setOnClickListener(this);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        LinearLayout layoutBackground = (LinearLayout)findViewById(R.id.linearLayout);
        layoutBackground.setOnClickListener(new LinearLayout.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                btnTakePicture.setEnabled(false);
                camera.autoFocus(mAutoFocusCallback);
            }});


    }

    @SuppressLint("NewApi")
    FaceDetectionListener faceDetectionListener
            = new FaceDetectionListener(){

        @Override
        public void onFaceDetection(Face[] faces, Camera camera) {
            //speakWords("hello world");
            boolean flag;

            System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
            if (faces.length == 0){
                // Toast.makeText(getApplicationContext(), "No face detected..!", Toast.LENGTH_SHORT).show();

                txtFaceCount.setText("Number of Faces Detected:" + " " +String.valueOf(faces.length));
                //speakWords(str);


            }else{
                flag=true;
                txtFaceCount.setText("Number of Faces Detected:" + " " + String.valueOf(faces.length));
                speakWords(str + "ok");
                speakWords(str2);
                //speakWords("Lahore zoo , distance 0.5 kilo meter , ");
            }
        }};


    AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            btnTakePicture.setEnabled(true);
        }};

    ShutterCallback mShutterCallback = new ShutterCallback(){
        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }};

    PictureCallback mRawPictureCallback = new PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub

        }};

    PictureCallback mJPGPictureCallback = new PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub

            try {

                int imageNum = 0;

                Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "FaceDetection");
                if (!imagesFolder.exists()) {
                    imagesFolder.mkdirs();
                }

                String fileName = "image_" + String.valueOf(imageNum) + ".jpg";
                File output = new File(imagesFolder, fileName);

                while (output.exists()){
                    imageNum++;
                    fileName = "image_" + String.valueOf(imageNum) + ".jpg";
                    output = new File(imagesFolder, fileName);
                }

                Uri uriSavedImage = Uri.fromFile(output);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

                OutputStream imageFileOS;

                imageFileOS = getContentResolver().openOutputStream(uriSavedImage);
                imageFileOS.write(arg0);
                imageFileOS.flush();
                imageFileOS.close();

                txtImagePath.setText("Image saved to:" + " " +uriSavedImage.toString());

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            camera.startPreview();
            camera.startFaceDetection();
        }};

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
        // TODO Auto-generated method stub
        if(preview){
            camera.stopFaceDetection();
            camera.stopPreview();
            preview = false;
        }

        if (camera != null){
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                camera.startFaceDetection();
                preview = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera = Camera.open();
        camera.setFaceDetectionListener(faceDetectionListener);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopFaceDetection();
        camera.stopPreview();
        camera.release();
        camera = null;
        preview = false;
    }
    /*btnSpeak.setOnClickListener(new View.OnClickListener() {


    });*/

    @Override
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }

    }
    public void onClick(View v) {
        try {
            //    System.out.println("oye chachu kam kr lyyyyyyyyyyyyyy.....12345");
            //get the text entered
            //    EditText enteredText = (EditText) findViewById(R.id.txtSpeechInput);
            //    String words = enteredText.getText().toString();

            //    speakWords(words);
        } catch (Exception e) {
            //    System.out.println("oye chachu kam kr lyyyyyyyyyyyyyy"+ e);
        }
    }
    public void speakWords(String speech) {
        System.out.println("ooooooooooooooooooooooooooooooo");
        //speak straight away
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
                myTTS = new TextToSpeech(this, this);
            }
            else {
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
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
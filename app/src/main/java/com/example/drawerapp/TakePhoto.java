package com.example.drawerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.example.drawerapp.MainActivity.adapter;
import static com.example.drawerapp.MainActivity.tickets;


public class TakePhoto extends AppCompatActivity {

    SurfaceView mCameraView;
    TextView mTextView;
    Button mButton;
    CameraSource mCameraSource;
    String text ="";
    public static Bitmap pic;

    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);


        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.text_view);
        mButton = findViewById(R.id.getText);
        startCameraSource();
    }


    @Override
    public void onBackPressed()
    {
        finish();

    }

    public void saveText(View view){
        if (text == "") {
            Toast.makeText(TakePhoto.this, "You can't save an empty text", Toast.LENGTH_LONG).show();

        }else{

            final Ticket ticket = new Ticket();

            mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes) {
                    try {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        pic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                        Matrix matrix = new Matrix();

                        matrix.postRotate(90);

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(pic, pic.getWidth(), pic.getHeight(), true);

                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


                        ticket.setPic(rotatedBitmap);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(TakePhoto.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


            ticket.setName("UNKNOWN");
            ticket.setDate(new Date());
            ticket.setPrix(0);
            ticket.setType("UNKNOWN");

            ArrayList<String> str = new ArrayList<>();
            String mot="";

            for (int i = 0; i < text.length() ; i++) {
                mot = mot +text.charAt(i);
                if (text.charAt(i)==' ' || text.charAt(i) == '\n' || text.charAt(i)=='-' || text.charAt(i)==',' || text.charAt(i)==':' || text.charAt(i)=='.'){
                    str.add(mot.toLowerCase().trim());
                    mot ="";
                }
            }
            ticket.setName(str.get(0)+" "+str.get(1));



            if (str.indexOf("boulangerie") !=-1 || str.indexOf("pizzeria") !=-1 || str.indexOf("cremerie") !=-1)ticket.setType("Restaurant");
            else if (str.indexOf("cafe") != -1 || str.indexOf("café") != -1 || str.indexOf("kfe")!=-1 || str.indexOf("kfé") != -1 )ticket.setType("Café");

            ArrayList<Double> nmbrs = new ArrayList<>();
            for (int i = str.size()*75/100; i < str.size() ; i++) {
                if (isNumeric(str.get(i))){
                    nmbrs.add(Double.parseDouble(str.get(i)));
                }
            }
            for (int i = 0; i < nmbrs.size(); i++) {
                if (nmbrs.get(i)>ticket.getPrix()) ticket.setPrix(nmbrs.get(i));
            }


            tickets.add(ticket);
            adapter.notifyDataSetChanged();
            finish();
        }
    }
    public boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public void seeTickets(View view){
        finish();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(TakePhoto.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                mTextView.setText(stringBuilder.toString());
                                text = stringBuilder.toString();

                            }
                        });
                    }
                }
            });
        }
    }
}
package com.example.drawerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.drawerapp.Login.sharedPref;
import static com.example.drawerapp.MainActivity.adapter;
import static com.example.drawerapp.MainActivity.tickets;
import org.jibble.simpleftp.*;


public class TakePhoto extends AppCompatActivity {

    SurfaceView mCameraView;
    TextView mTextView;
    Button mButton;
    CameraSource mCameraSource;
    String text ="";
    public static Bitmap pic;
    final Ticket ticket = new Ticket();
    private FTPClientFunctions ftpclient = null;

    private static final String TAG = "TakePhoto";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);


        ftpclient = new FTPClientFunctions();

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
        DateFormat dateFormat = new SimpleDateFormat("H_mm_ss_dd_MM_yyyy");
        final String nameb= dateFormat.format(new Date())+".jpg";
        if (text == "") {
            Toast.makeText(TakePhoto.this, "You can't save an empty text", Toast.LENGTH_LONG).show();

        }else{

            mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes) {
                    Toast.makeText(TakePhoto.this, "Uploading ...", Toast.LENGTH_SHORT).show();
                    new sendPic(bytes).execute(nameb);


                }
            });


            int idUserr = sharedPref.getInt("idUser",-1);
            ticket.setIdUser(idUserr);
            ticket.setName("UNKNOWN");
            ticket.setDate(new Date());
            ticket.setPrix(0);
            ticket.setType("UNKNOWN");

            ArrayList<String> str = new ArrayList<>();
            String mot="";

            for (int i = 0; i < text.length() ; i++) {
                mot = mot + text.charAt(i);
                if (text.charAt(i) == ' ' || text.charAt(i) == '\n' || text.charAt(i) == '-' || text.charAt(i) == ',' || text.charAt(i) == ':' || text.charAt(i) == '.') {
                    str.add(mot.toLowerCase().trim());
                    mot = "";
                }
            }

            if (str.size()>=2) ticket.setName(str.get(0)+" "+str.get(1));



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

            new RetrieveData().execute(ticket.getName(),ticket.getType(),ticket.getDate(),ticket.getPrix()+"",ticket.getIdUser()+"","/phpAPI/pics/"+nameb);
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


    public class RetrieveData extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("https://tickets.fcpo.ma/phpAPI/ticket/addTicket.php?nom="+urls[0]+"&type="+urls[1]+"&date="+urls[2]+"&prix="+urls[3]+"&idUser="+urls[4]+"&picPath="+urls[5]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            int id=-1;

            try {
                JSONObject object = (JSONObject) new JSONTokener(s).nextValue();
                id =object.getInt("idTicket");
                if (id==-1) Toast.makeText(TakePhoto.this, "error while saving to server...", Toast.LENGTH_SHORT).show();
                else {
                    ticket.setId(id);
                    tickets.add(ticket);
                    //adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




    public class sendPic extends AsyncTask<String,Void,String> {

        byte[] mybytes;

        public sendPic(byte[] bytes){
            this.mybytes=bytes;
        }
        public sendPic() {
        }



        @Override
        protected String doInBackground(String... urls) {

            boolean h=false;

            File imageFile=new File(TakePhoto.this.getFilesDir(),"");

            try {

                new Thread(new Runnable() {
                    public void run() {
                        boolean status;
                        status = ftpclient.ftpConnect("ftp.fcpo.ma", "tickets@fcpo.ma", "FCPO2019@", 21);
                        if (status) {
                            Log.d(TAG, "Connection Success");
                        } else {
                            Log.d(TAG, "Connection failed");
                        }
                    }
                }).start();
            } catch(Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }



            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                pic = BitmapFactory.decodeByteArray(mybytes, 0, mybytes.length, options);

                Matrix matrix = new Matrix();

                matrix.postRotate(90);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(pic, pic.getWidth(), pic.getHeight(), true);

                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


                rotatedBitmap = ThumbnailUtils.extractThumbnail(rotatedBitmap, 500, 500);


                imageFile = new File(TakePhoto.this.getFilesDir(), urls[0]);

                ticket.setPicURL("/phpAPI/pics/"+urls[0]);

                OutputStream os;
                try {
                    os = new FileOutputStream(imageFile);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();

                } catch (Exception e) {
                    Log.d(TAG, "Error writing bitmap", e);
                }



            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(TakePhoto.this, e.toString(), Toast.LENGTH_SHORT).show();
            }



            h=ftpclient.ftpUpload(imageFile.getAbsolutePath(),imageFile.getName(),"/phpAPI/pics/",TakePhoto.this);
            return h+"";
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG,ticket.getPicURL());
            Toast.makeText(TakePhoto.this, "Uploaded", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();


        }
    }



}
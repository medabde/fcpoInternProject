package com.example.drawerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.example.drawerapp.MainActivity.adapter;
import static com.example.drawerapp.MainActivity.id101;
import static com.example.drawerapp.MainActivity.tickets;

public class ModifyText extends AppCompatActivity {


    EditText editText,editText2,editText3 ;
    TextView date;
    ImageView ticketPic;
    Ticket ticket = tickets.get(id101);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_text);



        editText = findViewById(R.id.name);
        editText.setText(ticket.getName());

        editText2 = findViewById(R.id.type);
        editText2.setText(ticket.getType());


        editText3 = findViewById(R.id.prix);
        editText3.setText(ticket.getPrix()+"");

        date = findViewById(R.id.date);
        date.setText(ticket.getDate());

        ticketPic = findViewById(R.id.ticketPic);
        Picasso.get()
                .load("https://tickets.fcpo.ma"+ticket.getPicURL())
                .placeholder(R.drawable.ic_photo_black_24dp)
                .resize(500,500)
                .into(ticketPic);



        setTitle("Ticket Editor");
    }
    @Override
    public void onBackPressed() {
        if (editText.getText().toString().isEmpty())ticket.setName(" ");
        else ticket.setName(editText.getText().toString());

        if (editText2.getText().toString().isEmpty()) ticket.setType(" ");
        else ticket.setType(editText2.getText().toString());

        if (editText3.getText().toString().isEmpty())ticket.setPrix((double)0);
        else ticket.setPrix(Double.parseDouble(editText3.getText().toString()));

        tickets.set(id101,ticket);
        new RetrieveData().execute(ticket.getName(),ticket.getType(),ticket.getDate(),ticket.getPrix()+"",ticket.getIdUser()+"",ticket.getId()+"");

        adapter.notifyDataSetChanged();
        finish();
    }




    public class RetrieveData extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("https://tickets.fcpo.ma/phpAPI/ticket/editTicket.php?nom="+urls[0]+"&type="+urls[1]+"&date="+urls[2]+"&prix="+urls[3]+"&idUser="+urls[4]+"&idTicket="+urls[5]);
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
                if (id==-1) Toast.makeText(ModifyText.this, "error while saving to server...", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(ModifyText.this, "edited", Toast.LENGTH_SHORT).show();
//                    ticket.setId(id);
//                    tickets.add(ticket);
//                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }












}

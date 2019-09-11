package com.example.drawerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
        ticketPic.setImageBitmap(ticket.getPic());

        setTitle("Ticket Editor");
    }
    @Override
    public void onBackPressed() {
        if (editText.getText().toString().isEmpty())ticket.setName("khra");
        else ticket.setName(editText.getText().toString());

        if (editText2.getText().toString().isEmpty()) ticket.setType("khra");
        else ticket.setType(editText2.getText().toString());

        if (editText3.getText().toString().isEmpty())ticket.setPrix((double)0);
        else ticket.setPrix(Double.parseDouble(editText3.getText().toString()));

        tickets.set(id101,ticket);
        adapter.notifyDataSetChanged();
        finish();
    }
}

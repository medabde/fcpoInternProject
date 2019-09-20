package com.example.drawerapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Ticket> {

    public CustomAdapter(Context context, ArrayList<Ticket> users) {
        super(context, 0, users);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Ticket ticket = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_list_view, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        TextView type = convertView.findViewById(R.id.type);
        TextView date = convertView.findViewById(R.id.date);
        TextView prix = convertView.findViewById(R.id.prix);
        ImageView image = convertView.findViewById(R.id.ticketPic);

        name.setText(ticket.getName());
        type.setText(ticket.getType());
        date.setText(ticket.getDate());
        prix.setText(ticket.getPrix()+"");


        Picasso.get()
                .load("https://tickets.fcpo.ma"+ticket.getPicURL())
                .placeholder(R.drawable.ic_photo_black_24dp)
                .resize(100,100)
                .into(image);

        return convertView;
    }
}

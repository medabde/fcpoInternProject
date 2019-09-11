package com.example.drawerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        name.setText(ticket.getName());
        type.setText(ticket.getType());
        date.setText(ticket.getDate());
        prix.setText(ticket.getPrix()+"");

        return convertView;
    }
}

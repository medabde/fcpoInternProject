package com.example.drawerapp.ui.mesTickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.drawerapp.MainActivity;
import com.example.drawerapp.R;
import com.example.drawerapp.TakePhoto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.drawerapp.MainActivity.fab;
import static com.example.drawerapp.MainActivity.listView;

public class MesTicketsFragment extends Fragment {

    private MesTicketsViewModel mesTicketsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mesTicketsViewModel =
                ViewModelProviders.of(this).get(MesTicketsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        //final FloatingActionButton fab = root.findViewById(R.id.fab);
        mesTicketsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);

                fab.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);



            }
        });








        return root;
    }
}
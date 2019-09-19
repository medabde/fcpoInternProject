package com.example.drawerapp.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.drawerapp.Login;
import com.example.drawerapp.MainActivity;
import com.example.drawerapp.R;
import com.example.drawerapp.SplashScreen;

import static com.example.drawerapp.Login.sharedPref;
import static com.example.drawerapp.MainActivity.fab;
import static com.example.drawerapp.MainActivity.listView;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        final Button button = root.findViewById(R.id.logoutBtn);
        profileViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove("isLoggedIn");
                        editor.remove("idUser");
                        editor.putInt("idUser",-1);
                        editor.putBoolean("isLoggedIn",false);
                        editor.commit();

                        Intent mainIntent = new Intent(getActivity(), SplashScreen.class);
                        getActivity().startActivity(mainIntent);
                        getActivity().finish();

                    }
                });

                fab.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
            }
        });
        return root;
    }
}
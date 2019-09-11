package com.example.drawerapp.ui.aboutUs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutUsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AboutUsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Jenji is the best solution for enterprises that need to control and optimize their expense management in realtime.\n" +
                "\n" +
                "Free your employees from the time wasted manually creating their monthly expense reports. Jenji's mobile and web applications empower them to concentrate on their mission instead of administrative, labor-intensive tasks.\n" +
                "\n" +
                "Give your managers efficient validation tools. With Jenji's collaborative platform, they can review expenses quickly and supervise efficiently their team spending.\n" +
                "\n" +
                "Give your Finance team a solution that helps them automate the expense process completely, from initial submission down to ERP integration.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
package com.macbitsgoa.comrades;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static com.macbitsgoa.comrades.HomeActivity.SETTINGS;

/**
 * @author aayush singla
 */
public class TutorialActivity extends AppIntro2 {
    public static final String COMPLETED_ON_BOARDING_PREF_NAME = "OnboardingCompleted4";
    private final ArrayList<String> descriptionArray = new ArrayList<>(0);
    private final ArrayList<Integer> imageArray = new ArrayList<>(0);
    private final ArrayList<Integer> backgroundColor = new ArrayList<>(0);
    private final ArrayList<String> titleArray = new ArrayList<>(0);

    public TutorialActivity() {
        titleArray.add("Hi Comrades!");
        descriptionArray.add("Welcome to the official file sharing platform of Bits Goa. A Single place to get all the files you need. Press the button on the right to know more about app and its functionality.");
        imageArray.add(R.mipmap.ic_launcher);
        backgroundColor.add(R.color.purple);

        titleArray.add("Add Course");
        descriptionArray.add("You can add any course of your choice or your interest. Just click on the Floating Button in the All Courses Section and type in the details.");
        imageArray.add(R.drawable.ic_add);
        backgroundColor.add(R.color.darkPink);

        titleArray.add("Powerful Search");
        descriptionArray.add("If you don't want to add a course, you can search among the already existing courses just by clicking on search button in toolbar.");
        imageArray.add(R.drawable.ic_search);
        backgroundColor.add(R.color.pinkRed);

        titleArray.add("Adding Files");
        descriptionArray.add("You can also add files to the existing courses just by going to the course and clicking on the floating button at Bottom of the screen.");
        imageArray.add(R.drawable.ic_cloud_upload);
        backgroundColor.add(R.color.orange);

        titleArray.add("Subscribing to courses");
        descriptionArray.add("If you are really interested in some course you can subscribe to it by clicking the bell icon on the right side of course name and you will start getting notifications whenever someone uploads a file to the course.Subscribed courses also appear in the My Courses Section on Home Screen.");
        imageArray.add(R.drawable.ic_notifications_active_black_24dp);
        backgroundColor.add(R.color.yellow);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFadeAnimation();
        for (int i = 0; i < titleArray.size(); i++) {
            SliderPage sliderPage = new SliderPage();
            sliderPage.setTitle(titleArray.get(i));
            sliderPage.setDescription(descriptionArray.get(i));
            sliderPage.setBgColor(ContextCompat.getColor(this, backgroundColor.get(i)));
            sliderPage.setImageDrawable(imageArray.get(i));
            addSlide(AppIntroFragment.newInstance(sliderPage));
        }

        showSkipButton(true);
        setProgressButtonEnabled(true);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(SETTINGS, true);
        edit.putBoolean(COMPLETED_ON_BOARDING_PREF_NAME, Boolean.TRUE);
        edit.apply();
    }
}

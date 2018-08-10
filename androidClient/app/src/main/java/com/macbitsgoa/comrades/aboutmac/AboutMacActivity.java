package com.macbitsgoa.comrades.aboutmac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.macbitsgoa.comrades.FbListener;
import com.macbitsgoa.comrades.FirebaseKeysKt;
import com.macbitsgoa.comrades.CHCKt;

import com.macbitsgoa.comrades.R;

import java.util.ArrayList;
import java.util.List;

public class AboutMacActivity extends AppCompatActivity implements View.OnClickListener {


    public static String ABOUT_US_FACEBOOK_URL = "https://www.facebook.com/MACBITSGoa";
    public static String ABOUT_US_FACEBOOK_PAGE_ID = "MACBITSGoa";
    public static String ABOUT_US_GITHUB_URL = "https://github.com/MobileApplicationsClub/";
    public static String ABOUT_US_LINKEDIN_URL = "https://www.linkedin.com/company/mobile-applications-club/";
    public static String ABOUT_US_MACWEBSITE_URL = "https://macbitsgoa.com/";
    public static String ABOUT_APP_TITLE = "About App";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_mac);

        Toolbar toolbar = findViewById(R.id.about_mac_toolbar);
        ImageButton facebookImgBtn = findViewById(R.id.content_about_us_fb_imgbtn);
        ImageButton googlePlayImgBtn = findViewById(R.id.content_about_us_google_play_imgbtn);
        ImageButton githubImgBtn = findViewById(R.id.content_about_us_github);
        ImageButton linkedinImgBtn = findViewById(R.id.content_about_us_linkedin);
        ImageButton macwebBtn = findViewById(R.id.content_about_us_macweb);
        RecyclerView contributorsRecyclerView = findViewById(R.id.content_about_us_rv);

        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ABOUT_APP_TITLE);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.mac_color));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.mac_color));


        contributorsRecyclerView.setHasFixedSize(false);
        contributorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Retrieval of data from firebase is handled within the adapter itself
        contributorsRecyclerView.setAdapter(new ContributorsAdapter());

        findViewById(R.id.content_about_us_app_name_tv).requestFocus();
        facebookImgBtn.setOnClickListener(this);
        googlePlayImgBtn.setOnClickListener(this);
        githubImgBtn.setOnClickListener(this);
        linkedinImgBtn.setOnClickListener(this);
        macwebBtn.setOnClickListener(this);

    }


    //method to get the right URL to use in the intent
    public static String getFacebookPageURL(final Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + ABOUT_US_FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + ABOUT_US_FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return ABOUT_US_FACEBOOK_URL; //normal web url
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.content_about_us_fb_imgbtn:
                try {
                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                    String facebookUrl = getFacebookPageURL(this);
                    facebookIntent.setData(Uri.parse(facebookUrl));
                    startActivity(facebookIntent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ABOUT_US_FACEBOOK_URL));
                    Toast.makeText(this, "Opening in browser", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                break;
            case R.id.content_about_us_google_play_imgbtn:
                Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW);
                googlePlayIntent.setData(Uri.parse("market://search?q=Mobile App Club - BITS Goa"));
                startActivity(googlePlayIntent);
                break;
            case R.id.content_about_us_github:

                    Intent githubIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(ABOUT_US_GITHUB_URL));
                    startActivity(githubIntent);
                    break;
            case R.id.content_about_us_linkedin:
                    Intent linkedinIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(ABOUT_US_LINKEDIN_URL));
                    startActivity(linkedinIntent);
                    break;
            case R.id.content_about_us_macweb:
                    Intent macwebIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(ABOUT_US_MACWEBSITE_URL));
                    startActivity(macwebIntent);
            default:
                break;
        }
    }




}

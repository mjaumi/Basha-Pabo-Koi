package com.example.bashapabokoi;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    TextView welcome,to,name,powered;
    View view;
    LottieAnimationView lottieSplash;

    private static final int pageNo = 3;

    SharedPreferences sharedPreferences;
    public static SharedPreferences sharedPref;

    Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        welcome = findViewById(R.id.welcome);
        to = findViewById(R.id.to);
        name = findViewById(R.id.name);
        powered = findViewById(R.id.powered);
        lottieSplash = findViewById(R.id.lottie_splash);
        view = findViewById(R.id.splash_screen);

        verifyPermissions();
    }

    private void setupViewPager(){

        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        welcome.setAnimation(fadeIn);
        to.setAnimation(fadeIn);

        Animation fadeIn2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in2);
        name.setAnimation(fadeIn2);
        powered.setAnimation(fadeIn2);

        int splashTime = 3500;
        new Handler().postDelayed(() -> {

            sharedPreferences = getSharedPreferences("SharedPref", MODE_PRIVATE);
            boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);

            if(isFirstTime){

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();

                view.animate().translationY(-5000).setDuration(1000).setStartDelay(4000);
                lottieSplash.animate().translationY(1600).setDuration(1000).setStartDelay(4000);
                welcome.animate().translationY(2200).setDuration(1000).setStartDelay(4000);
                to.animate().translationY(2200).setDuration(1000).setStartDelay(4000);
                name.animate().translationY(2200).setDuration(1000).setStartDelay(4000);
                powered.animate().translationY(2200).setDuration(1000).setStartDelay(4000);

                view.animate().alphaBy((float) -0.4).setDuration(1000).setStartDelay(4000);
                lottieSplash.animate().alphaBy((float) -0.4).setDuration(1000).setStartDelay(4000);
                welcome.animate().alphaBy((float) -0.4).setDuration(1000).setStartDelay(4000);
                to.animate().alphaBy((float) -0.4).setDuration(1000).setStartDelay(4000);
                name.animate().alphaBy((float) -0.4).setDuration(1000).setStartDelay(4000);
                powered.animate().alphaBy((float) -0.4).setDuration(1000).setStartDelay(4000);

                anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_splash);

                ViewPager viewPager = findViewById(R.id.pager);
                ScreenSliderPagerAdepter pagerAdepter = new ScreenSliderPagerAdepter(getSupportFragmentManager());
                viewPager.setAdapter(pagerAdepter);
                viewPager.setAnimation(anim);

            } else {

                sharedPref = getSharedPreferences("SharedPref", MODE_PRIVATE);

                Log.d("keepMeLoggedIn", "Value: "+ sharedPref.getBoolean("keepMeLoggedIn", false));

                if(sharedPref.getBoolean("keepMeLoggedIn", false)){

                    Intent intent = new Intent(SplashActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();

                } else{

                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

        }, splashTime);

    }

    private void verifyPermissions(){

        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[3]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[4]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[5]) == PackageManager.PERMISSION_GRANTED){

            welcome.setAlpha(1f);
            to.setAlpha(1f);
            name.setAlpha(1f);
            powered.setAlpha(1f);
            lottieSplash.setAlpha(1f);
            lottieSplash.playAnimation();
            setupViewPager();
        } else{

            ActivityCompat.requestPermissions(SplashActivity.this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        verifyPermissions();
    }

    private static class ScreenSliderPagerAdepter extends FragmentStatePagerAdapter {

        public ScreenSliderPagerAdepter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position){

            switch (position){

                case 0:
                    return new OnBoardingFragment1();

                case 1:
                    return new OnBoardingFragment2();

                case 2:
                    return new OnBoardingFragment3();
            }

            return null;
        }

        @Override
        public int getCount(){

            return pageNo;
        }
    }
}
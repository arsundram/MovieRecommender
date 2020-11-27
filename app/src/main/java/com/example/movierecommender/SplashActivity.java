package com.example.movierecommender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ScaleAnimation fade_in = new ScaleAnimation(0f, 1.1f, 0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(1000);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        findViewById(R.id.logo).startAnimation(fade_in);
        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ScaleAnimation fade_in2 = new ScaleAnimation(1.1f, 1f, 1.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                fade_in2.setDuration(500);     // animation duration in milliseconds
                fade_in2.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
                findViewById(R.id.logo).startAnimation(fade_in2);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fade_in.setDuration(500);
        findViewById(R.id.circle_top).startAnimation(fade_in);
        fade_in.setDuration(1500);
        findViewById(R.id.circle_bottom).startAnimation(fade_in);
        findViewById(R.id.circle_top).animate().alpha(1).setDuration(1000);
        findViewById(R.id.circle_bottom).animate().alpha(1).setDuration(1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },2000);
    }
}
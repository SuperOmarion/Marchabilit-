package com.sorbonne.isir.etudedelamarchabilite;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class Bienvenue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenue);
        fadeIn();

    }

    public void fadeIn(){
        ImageView image = (ImageView) findViewById(R.id.logos);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        image.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                final MediaPlayer mp = MediaPlayer.create(Bienvenue.this, R.raw.sound);
                mp.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent myIntent = new Intent(Bienvenue.this, Connection.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });


    }
}


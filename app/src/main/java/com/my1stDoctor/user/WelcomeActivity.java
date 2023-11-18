package com.my1stDoctor.user;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        firebaseAuth = FirebaseAuth.getInstance();
        MediaPlayer mp = MediaPlayer.create(WelcomeActivity.this, R.raw.splash_audio);

        Thread timer = new Thread() {
            public void run() {
                try {
                    mp.start();
                    sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                    Boolean resul = firebaseAuth.getCurrentUser() == null;

                    Log.d("CurrentUser", "" + resul);
                    if (firebaseAuth.getCurrentUser() == null) {
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
//                            mp.release();
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//                            mp.release();
                        startActivity(intent);
                        finish();
                    }

                }
            }
        };

        timer.start();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(WelcomeActivity.this, R.color.white));

        }
    }
}

//        new CountDownTimer(1000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//
//
//            }
//
//            @Override
//            public void onFinish() {
//
//
//                //welcome_PB.setVisibility(View.VISIBLE);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                        Boolean resul = firebaseAuth.getCurrentUser() == null;
//
//                        Log.d("CurrentUser", "" + resul);
//                        if (firebaseAuth.getCurrentUser() == null) {
//                            Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
////                            mp.release();
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
////                            mp.release();
//                            startActivity(intent);
//                            finish();
//                        }
//
//                    }
//                }, 5000);
//
//            }
//        }.start();
//    }


//}
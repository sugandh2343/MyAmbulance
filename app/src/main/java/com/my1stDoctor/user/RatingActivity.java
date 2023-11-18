package com.my1stDoctor.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class RatingActivity extends AppCompatActivity {
    RoundedImageView profilePic_IV;
    TextView driverName_TV,mobileNo_TV,carNo_TV,startpoint_tv,duration_tv,fare_tv,paymentFare_tv,paymentMode_tv,end_point_tv;
    EditText rating_ET;
    RatingBar ratingBar;
    ImageView back_btn;
    Button rating_submit_btn;
    String driver_name,driver_profile,driver_mobile,ambulance_no,start_point_add,end_point_add,duration,fare,timestamp;
    Float rating;
    ProgressDialog progressDialog;
    TextView Status_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        profilePic_IV = findViewById(R.id.profilePic_IV);
        driverName_TV = findViewById(R.id.driverName_TV);
        mobileNo_TV = findViewById(R.id.mobileNo_TV);
        carNo_TV = findViewById(R.id.carNo_TV);
        startpoint_tv = findViewById(R.id.startpoint_tv);
        duration_tv = findViewById(R.id.duration_tv);
        fare_tv = findViewById(R.id.fare_tv);
        rating_submit_btn = findViewById(R.id.rating_submit_btn);
        paymentFare_tv = findViewById(R.id.paymentFare_tv);
        paymentMode_tv = findViewById(R.id.paymentMode_tv);
        end_point_tv = findViewById(R.id.endpoint_tv);
        rating_ET = findViewById(R.id.rating_ET);
        driver_name = getIntent().getExtras().getString("Driver_name");
        driver_mobile = getIntent().getExtras().getString("Driver_mob");
        driver_profile = getIntent().getExtras().getString("Driver_Profile");
        ambulance_no = getIntent().getExtras().getString("Ambulance_no");
        start_point_add = getIntent().getExtras().getString("Start_Address");
        end_point_add = getIntent().getExtras().getString("End_Add");
        duration = getIntent().getExtras().getString("Duration");
        fare = getIntent().getExtras().getString("Fare");
        timestamp = getIntent().getExtras().getString("Timestamp");
        progressDialog = new ProgressDialog(this);
        back_btn=findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        showData();

        ratingBar = findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating=ratingBar.getRating();


                Toast.makeText(RatingActivity.this, "You Rated :"+String.valueOf(ratingBar.getRating()), Toast.LENGTH_SHORT).show();
            }
        });
        rating_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_child_layout);
                progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                progressDialog.setCancelable(false);


                Status_TV=progressDialog.findViewById(R.id.Status_TV);

                Status_TV.setText("Submitting Your FeedBack");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("rating", "" +rating );
                hashMap.put("rating_feedback", "" + rating_ET.getText().toString());
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Bookings");
                reference.child(timestamp).updateChildren(hashMap);
                Toast.makeText(RatingActivity.this, "Thank You For Your Feed Back....", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        startActivity(new Intent(RatingActivity.this,MainActivity.class));
                    }
                },2000);


            }
        });


    }

    private void showData() {
        try {

            Picasso.get().load(driver_profile).resize(400, 400).centerCrop().placeholder(R.drawable.profileimage).into(profilePic_IV);


        } catch (Exception e) {
            Log.e("Profile Error", e.getMessage());
            profilePic_IV.setImageResource(R.drawable.profileimage);


        }
        driverName_TV.setText(driver_name);
        mobileNo_TV.setText(driver_mobile);
        carNo_TV.setText(ambulance_no);
        startpoint_tv.setText(start_point_add);
        end_point_tv.setText(end_point_add);
        duration_tv.setText(duration);
        fare_tv.setText(fare);
        paymentFare_tv.setText(fare);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RatingActivity.this,MainActivity.class));
    }
}
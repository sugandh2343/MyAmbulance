package com.my1stDoctor.user;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class JoinUsActivity extends AppCompatActivity {
    ImageView back_btn;
    TextInputEditText name_ET,mobile_ET,email_ET,pinCode_ET,city_ET,state_ET;
    MaterialButton submit_BTN;
    String name,mobile,city,state;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us);

        back_btn=findViewById(R.id.back_btn);
        name_ET=findViewById(R.id.name_ET);
        mobile_ET=findViewById(R.id.mobile_ET);
        email_ET=findViewById(R.id.email_ET);
        pinCode_ET=findViewById(R.id.pinCode_ET);
        city_ET=findViewById(R.id.city_ET);
        state_ET=findViewById(R.id.state_ET);
        submit_BTN=findViewById(R.id.submit_BTN);

        progressDialog = new ProgressDialog(JoinUsActivity.this);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JoinUsActivity.super.onBackPressed();
            }
        });

        submit_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name=name_ET.getText().toString().trim();
                mobile=mobile_ET.getText().toString().trim();
                state=state_ET.getText().toString().trim();
                city=city_ET.getText().toString().trim();

                if (name_ET.getText().toString().isEmpty() || mobile_ET.getText().toString().isEmpty() ||
                        state_ET.getText().toString().isEmpty() || city_ET.getText().toString().isEmpty()){
                    Toast.makeText(JoinUsActivity.this, "Please fill the fields", Toast.LENGTH_LONG).show();
                } else {

                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_child_layout);
                    progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                    progressDialog.setCancelable(false);

                    String timestamp = "" + System.currentTimeMillis();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("timeStamp", "" + timestamp);
                    hashMap.put("Name", "" +name); //yaha pe jo enquiry ka name hoga wo string lena h
                    hashMap.put("Mobile", "" + mobile);
                    hashMap.put("City", "" + city);
                    hashMap.put("state", "" + state);


                    //Saving to database

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Enquiry");
                    ref.child(timestamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            startActivity(new Intent(JoinUsActivity.this,MainActivity.class));
                                            finish();
                                        }
                                    },2000);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            Toast.makeText(JoinUsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    },2000);

                                }
                            });
                }

            }
        });
    }

}
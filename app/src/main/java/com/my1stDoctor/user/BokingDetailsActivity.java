package com.my1stDoctor.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.model.Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BokingDetailsActivity extends AppCompatActivity {

    TextView startpoint_tv, destination_tv, duration_tv, kilometer_tv, fare_tv;
    MaterialButton confirmbooking_btn;
    ImageView back_btn;
    FirebaseAuth firebaseAuth;
    String token = null;
    String StartPointAddress, EndPointAddress, Fare, Duration, Distance, StartPoint_Lat, StartPoint_Lon, EndPoint_Lat,
            EndPoint_Lon;
    String user_name, user_mob;
    String imgurl = "https://www.drivers4me.com/static/assets/images/NewHomeArt.png";
    ArrayList<ModelDriver> driverArrayList;
    String Driver_Uid, Driver_latitude, Driver_longitude,Driver_mob;
    Integer shortest_distance;
    ArrayList<Float> distance_short;
    float shortdistance = 0;
    int pos;
    String status;
    String timestamp;
    ProgressDialog progressDialog;
    ModelBookingStatus modelBookingStatus;

    ImageView Booking_Sucess_IV;
    TextView Status_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boking_details);

        startpoint_tv = findViewById(R.id.startpoint_tv);
        destination_tv = findViewById(R.id.destination_tv);
        duration_tv = findViewById(R.id.duration_tv);
        kilometer_tv = findViewById(R.id.kilometer_tv);
        fare_tv = findViewById(R.id.fare_tv);
        confirmbooking_btn = findViewById(R.id.confirmbooking_btn);
        back_btn = findViewById(R.id.back_btn);

        progressDialog = new ProgressDialog(this);



        firebaseAuth = FirebaseAuth.getInstance();

        StartPointAddress = getIntent().getExtras().getString("StartPointAddress", "");
        EndPointAddress = getIntent().getExtras().getString("EndPointAddress", "");
        Fare = getIntent().getExtras().getString("Fare", "");
        Duration = getIntent().getExtras().getString("Duration", "");
        Distance = getIntent().getExtras().getString("Distance", "");
        StartPoint_Lat = getIntent().getExtras().getString("StartPoint_Lat", "");
        StartPoint_Lon = getIntent().getExtras().getString("StartPoint_Lon", "");
        EndPoint_Lat = getIntent().getExtras().getString("EndPoint_Lat", "");
        EndPoint_Lon = getIntent().getExtras().getString("EndPoint_Lon", "");

        startpoint_tv.setText(StartPointAddress);
        destination_tv.setText(EndPointAddress);
        duration_tv.setText(Duration);
        kilometer_tv.setText("( " + Distance + " )");
        fare_tv.setText(Fare);

        Log.e("StartPointAddress", StartPointAddress);
        Log.e("EndPointAddress", EndPointAddress);
        Log.e("Fare", Fare);
        Log.e("Duration", Duration);
        Log.e("StartPoint_Lat", StartPoint_Lat);
        Log.e("StartPoint_Lon", StartPoint_Lon);
        Log.e("EndPoint_Lat", EndPoint_Lat);
        Log.e("EndPoint_Lon", EndPoint_Lon);



        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BokingDetailsActivity.super.onBackPressed();
            }
        });
        confirmbooking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_child_layout);
                progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                progressDialog.setCancelable(false);
                Booking_Sucess_IV=progressDialog.findViewById(R.id.Booking_Sucess_IV);
                Status_TV=progressDialog.findViewById(R.id.Status_TV);

                Status_TV.setText("Please wait...");
                loadUserData();
            }
        });

    }

    private void loadUserData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            user_name = "" + ds.child("name").getValue();
                            user_mob = "" + ds.child("phone").getValue();
                            saveToDatabase();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void saveToDatabase() {
        timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("timeStamp", "" + timestamp);
        hashMap.put("StartPointAddress", "" + StartPointAddress);
        hashMap.put("EndPointAddress", "" + EndPointAddress);
        hashMap.put("Fare", "" + Fare);
        hashMap.put("Duration", "" + Duration);
        hashMap.put("StartPoint_Lat", "" + StartPoint_Lat);
        hashMap.put("StartPoint_Lon", "" + StartPoint_Lon);
        hashMap.put("EndPoint_Lat", "" + EndPoint_Lat);
        hashMap.put("EndPoint_Lon", "" + EndPoint_Lon);
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("Distance", "" + Distance);
        hashMap.put("passenger_name", "" + user_name);
        hashMap.put("passenger_mob", "" + user_mob);


        //Saving to database

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                            progressDialog.dismiss();
//                            startActivity(new Intent(BokingDetailsActivity.this,MainDriverActivity.class));
//                            finish();
                        UpdateBookingStatus();

//                        SendNotification();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
                        Toast.makeText(BokingDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(BokingDetailsActivity.this, MainActivity.class));
//                        finish();

                    }
                });

    }

    private void UpdateBookingStatus() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Status", "pending");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings");
        ref.child(timestamp).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        LoadDriverDetails();
//                        Log.e("Booking status","Booking status updated pending");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void LoadDriverDetails() {
        driverArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Drivers");
        ref
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Driver_Uid = "" + ds.child("uid").getValue();
                            Driver_mob=""+ds.child("phone").getValue();
                            Driver_latitude = "" + ds.child("latitude").getValue();
                            Driver_longitude = "" + ds.child("longitude").getValue();
                            calculateShortestDistance(Driver_mob, Driver_latitude, Driver_longitude);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void calculateShortestDistance(String DriverUid, String driver_latitude, String driver_longitude) {
        Double latitude = Double.parseDouble(driver_latitude);
        Double longitude = Double.parseDouble(driver_longitude);
        Double user_lat = Double.parseDouble(StartPoint_Lat);
        Double user_long = Double.parseDouble(StartPoint_Lon);
        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude, user_lat, user_long, results);
        float distance = results[0];
        float dist_in_km = distance / 1000;
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("distance",dist_in_km);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Drivers");
        ref.child(DriverUid).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nearestDriver();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void nearestDriver() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Drivers");
        reference.orderByChild("distance").limitToFirst(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            progressDialog.dismiss();
                            token = "" + ds.child("token").getValue();
                            String driverName=""+ds.child("name").getValue();
                            String driverMob=""+ds.child("phone").getValue();
                            SaveIntentValue();







                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });


    }

    private void SaveIntentValue() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Intent", "pending");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Bookings");
        ref.child(timestamp).child("Intent").updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SendNotification();
                        Intent intent=new Intent(BokingDetailsActivity.this,BookingStatusActivity.class);
                        intent.putExtra("Booking_Timestamp",timestamp);
                        intent.putExtra("Start_Lat",StartPoint_Lat);
                        intent.putExtra("Start_Lon",StartPoint_Lon);
                        intent.putExtra("Toatal_Fare",Fare);
                        intent.putExtra("Start_Point_Add",StartPointAddress);
                        intent.putExtra("End_Point_Add",EndPointAddress);
                        intent.putExtra("Duration",Duration);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }


    public void SendNotification() {
        Log.d("token : ", token);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject js = new JSONObject();
        try {
            JSONObject jsonobject_notification = new JSONObject();
            jsonobject_notification.put("title", "You Got A New Booking");
            jsonobject_notification.put("body", "Uid " + firebaseAuth.getUid());

            JSONObject jsonobject_data = new JSONObject();
            jsonobject_data.put("imgurl", imgurl);

            //JSONObject jsonobject = new JSONObject();

            js.put("to", token);
            js.put("notification", jsonobject_notification);
            js.put("data", jsonobject_data);

            //js.put("", jsonobject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("response", String.valueOf(response));

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error :  ", String.valueOf(error));

            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer AAAAih1qGGA:APA91bFkcAdVB2fE2g46Hc9sC3Iv7kLMUCNQnMZsptt1_1B_SY8Hk9tTyzyKgqchvnhQJOR2g9z-Z3VgKDsv4f07GVQZBI227PrYK3_ohiQvVfXLLqkdgQuU5O1GNqNJTBp04145_Gkg");
                //headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        requestQueue.add(jsonObjReq);

    }
}
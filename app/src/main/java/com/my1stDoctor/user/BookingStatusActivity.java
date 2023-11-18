package com.my1stDoctor.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookingStatusActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {
    GoogleMap googleMap;
    ImageView mapback_btn, profilePic_IV;
    TextView driverName_TV, mobileNo_TV, carNo_TV, driverStatus_TV;
    FirebaseAuth firebaseAuth;
    String timestamp;
    String driver_name, driver_mob, ambulance_no, driver_profile,Passenger_StartLat,Passenger_StartLon,Fare,
    Start_Point_Add,End_Point_Add,Duration;
    Double lat, lon;
    String MAPS_API_KEY;
    ApplicationInfo applicationInfo=null;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;

    private List<Polyline> polylines = null;
    LatLng latLngDriver,latlngPassenger;

    ImageView Booking_Sucess_IV;
    TextView Status_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);
        mapback_btn = findViewById(R.id.mapback_btn);
        profilePic_IV = findViewById(R.id.profilePic_IV);
        driverName_TV = findViewById(R.id.driverName_TV);
        mobileNo_TV = findViewById(R.id.mobileNo_TV);
        carNo_TV = findViewById(R.id.carNo_TV);
        driverStatus_TV = findViewById(R.id.driverStatus_TV);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);



        timestamp = getIntent().getExtras().getString("Booking_Timestamp");
        Passenger_StartLat = getIntent().getExtras().getString("Start_Lat");
        Passenger_StartLon = getIntent().getExtras().getString("Start_Lon");
        Fare = getIntent().getExtras().getString("Toatal_Fare");
        Start_Point_Add = getIntent().getExtras().getString("Start_Point_Add");
        End_Point_Add = getIntent().getExtras().getString("End_Point_Add");
        Duration = getIntent().getExtras().getString("Duration");
        try {
            applicationInfo=BookingStatusActivity.this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            MAPS_API_KEY=applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_child_layout);
        progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        progressDialog.setCancelable(false);

        Booking_Sucess_IV=progressDialog.findViewById(R.id.Booking_Sucess_IV);
        Status_TV=progressDialog.findViewById(R.id.Status_TV);

        Status_TV.setText("Finding Ambulance Near You");




//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bookings");

        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Bookings");
        reference1.keepSynced(true);


        DatabaseReference referenceIntent =FirebaseDatabase.getInstance().getReference("Bookings");

        referenceIntent.keepSynced(true);
        referenceIntent.child(timestamp).child("Intent").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    loadDriverDetails();



            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Bookings")
                .child(timestamp).child("status");
        reference.keepSynced(true);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {





            }



            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

//                Log.e("Snapshot==============",""+snapshot.child("d_latitude").getValue());
//                status=""+snapshot.getValue(String.class);
//                Log.e("Status",status);
//                if(status.equals("confirmed")){
//                    Intent intent=new Intent(BokingDetailsActivity.this,BookingStatusActivity.class);
//                    intent.putExtra("Booking_Timestamp",timestamp);
//                    startActivity(intent);
//                }
//                if(status.equals("reached")){
//                    Log.e("Cursor Reached Here","Cursor---------");
//                    DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Bookings");
//                    reference1.child(timestamp).removeValue();
//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    hashMap.put("uid", "" + firebaseAuth.getUid());
//                    hashMap.put("timeStamp", "" + timestamp);
//                    hashMap.put("StartPointAddress", "" + StartPointAddress);
//                    hashMap.put("EndPointAddress", "" + EndPointAddress);
//                    hashMap.put("Fare", "" + Fare);
//                    hashMap.put("Duration", "" + Duration);
//                    hashMap.put("StartPoint_Lat", "" + StartPoint_Lat);
//                    hashMap.put("StartPoint_Lon", "" + StartPoint_Lon);
//                    hashMap.put("EndPoint_Lat", "" + EndPoint_Lat);
//                    hashMap.put("EndPoint_Lon", "" + EndPoint_Lon);
//                    hashMap.put("uid", "" + firebaseAuth.getUid());
//                    hashMap.put("Distance", "" + Distance);
//                    hashMap.put("passenger_name", "" + user_name);
//                    hashMap.put("passenger_mob", "" + user_mob);
//                    hashMap.put("status","accepted");
////                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Drivers");
////                    ref.child(firebaseAuth.getCurrentUser().getUid()).child("Bookings")
////                            .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
////                        @Override
////                        public void onSuccess(Void unused) {
////                            progressDialog.dismiss();
////                            Toast.makeText(BokingDetailsActivity.this, "Your Booking is confirmed", Toast.LENGTH_SHORT).show();
////                            startActivity(new Intent(BokingDetailsActivity.this,BookingStatusActivity.class));
////
////                        }
////                    })
////                            .addOnSuccessListener(new OnSuccessListener<Void>() {
////                                @Override
////                                public void onSuccess(Void unused) {
////
////                                }
////                            });
//
//                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mapback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        reference1.child(timestamp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status= snapshot.child("Status").getValue(String.class);
                if(status.equals("Reached")) {
                    driverStatus_TV.setText("Driver has reached your location.....");
                }
                if(status.equals("Start_Ride")) {
                    driverStatus_TV.setText("Your ride has started.....");
                }
                if(status.equals("Dest_reached")) {
                    driverStatus_TV.setText("Your Destination is reached.....");
                    builder = new AlertDialog.Builder(BookingStatusActivity.this);
                    builder.setMessage("Make a Payment of Rs: "+Fare )
                            .setCancelable(false)
                            .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(BookingStatusActivity.this, "Payment Done", Toast.LENGTH_SHORT).show();

                                }
                            });
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Make Payment");
                    alert.show();
                }
                if(status.equals("Complete")) {
                   Intent intent= new Intent(BookingStatusActivity.this,RatingActivity.class);
                    intent.putExtra("Driver_name",driver_name);
                    intent.putExtra("Driver_mob",driver_mob);
                    intent.putExtra("Ambulance_no",ambulance_no);
                    intent.putExtra("Driver_Profile",driver_profile);
                    intent.putExtra("Start_Address",Start_Point_Add);
                    intent.putExtra("End_Add",End_Point_Add);
                    intent.putExtra("Duration",Duration);
                    intent.putExtra("Fare",Fare);
                    intent.putExtra("Timestamp",timestamp);

                   startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void Findroutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(BookingStatusActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(MAPS_API_KEY)  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    private void loadDriverDetails() {
        Log.e("TimestampStatus",timestamp);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Bookings");
        reference.child(timestamp).child("status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {
//                            progressDialog.dismiss();
                        if(ds.hasChild("driver_name")) {
                            driver_name = "" + ds.child("driver_name").getValue();
                            driver_mob = "" + ds.child("driver_mobile").getValue();
                            ambulance_no = "" + ds.child("ambulance_no").getValue();
                            driver_profile = "" + ds.child("driver_profile").getValue();
                            Log.e("driver_name", driver_name);
                            Log.e("snapshot",""+ds.hasChild("d_latitude"));
                            if(ds.hasChild("d_latitude")){
                                lat=Double.parseDouble(""+ds.child("d_latitude").getValue());
                                lon=Double.parseDouble(""+ds.child("d_longitude").getValue());
                                latLngDriver=new LatLng(lat,lon);
                                latlngPassenger=new LatLng(Double.parseDouble(Passenger_StartLat),Double.parseDouble(Passenger_StartLon));
                                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                supportMapFragment.getMapAsync(BookingStatusActivity.this);
                                Findroutes(latLngDriver,latlngPassenger);
                                Log.e("Latitude---------------------: ",""+lat);
                                Log.e("Longitude---------------------: ",""+lon);
                            }

                            loadData();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadData() {
        Status_TV.setText("Booking Confirmed");
        Booking_Sucess_IV.setVisibility(View.VISIBLE);

        driverName_TV.setText(driver_name);
        mobileNo_TV.setText(driver_mob);
        carNo_TV.setText(ambulance_no);
        try {

            Picasso.get().load(driver_profile).resize(400, 400).centerCrop().placeholder(R.drawable.profileimage).into(profilePic_IV);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            },2000);


        } catch (Exception e) {
            Log.e("Profile Error", e.getMessage());
            profilePic_IV.setImageResource(R.drawable.profileimage);


        }

    }



    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(25);
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.colorBlack));
                polyOptions.width(14);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = googleMap.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polylines.add(polyline);

            } else {

            }

        }

    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(latLngDriver, latlngPassenger);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap=googleMap;


        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latLngDriver).title("Driver Location"));
//        googleMap.addMarker(new MarkerOptions().position(latLngDriver).title("User Location"));
//            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
//                    .title("Your Current Location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLngDriver));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngDriver, 17));

    }
}
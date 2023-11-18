package com.my1stDoctor.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {
    GoogleMap googleMap;
    ImageView mapback_btn;
    private String[] locationPermissions;
    private static final int Location_Request_code = 200;
    private LocationManager locationManager;
    private double latitude, longitude, lat_dest, long_dest;
    int flag = 0;
    EditText et_starting_point, et_destination;
    TextView txt_distance, txt_amount_old, txt_amount_new, txt_mode, durationhour_tv, durationminuite_tv, duration_tv;
    private ProgressDialog progressDialog;
    LatLng latlng;
    LatLng latlng_destination;
    String stype = "";
    FusedLocationProviderClient fusedLocationProviderClient;
    public Location currentlocation;
    String address;
    Double lat, lon, lat1, lon1;
    LinearLayout traveldetails_ll;
    MaterialButton book_now;
    int statuscode;
    ProgressBar progressBar;
    Marker marker;
    ArrayList<ModelDriver> driverArrayList;
    ModelDriver modelDriver;


    private List<Polyline> polylines = null;

    ApplicationInfo applicationInfo=null;
    String MAPS_API_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(MapsActivity.this, R.color.statusbar));

        }

        try {
            applicationInfo=MapsActivity.this.getPackageManager().getApplicationInfo(this.getPackageName(),PackageManager.GET_META_DATA);
            MAPS_API_KEY=applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Initialize EditText
        et_destination = findViewById(R.id.et_destination);
        et_starting_point = findViewById(R.id.et_starting_point);
        txt_amount_new = findViewById(R.id.txt_amount_new);
        txt_distance = findViewById(R.id.txt_distance);
        txt_amount_old = findViewById(R.id.txt_amount_old);

        //durationhour_tv=findViewById(R.id.durationhour_tv);
        //durationminuite_tv=findViewById(R.id.durationminuite_tv);
        traveldetails_ll = findViewById(R.id.traveldetails_ll);
        duration_tv = findViewById(R.id.duration_tv);
        book_now = findViewById(R.id.book_now);
        progressBar = findViewById(R.id.progressBar);
        //txt_mode=findViewById(R.id.txt_mode);
        Places.initialize(getApplicationContext(), MAPS_API_KEY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //Permission Arrays
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        et_destination.setFocusable(false);
        et_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stype = "Destination";
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        , Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .build(getApplicationContext());
                startActivityForResult(intent, 100);

            }
        });
        et_starting_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_starting_point.getText().toString().trim())) {
                    return;
                } else {
                    stype = "Source";
                    et_starting_point.setFocusable(false);
                    List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                            , Place.Field.LAT_LNG, Place.Field.NAME);

                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                            .build(getApplicationContext());
                    startActivityForResult(intent, 100);
                }

            }
        });

        //Check LocationPermission
        if (checkLocationPermission()) {
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
            getLocation();
        } else {
            requestLocationPermission();
        }


        mapback_btn = findViewById(R.id.mapback_btn);
        mapback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MapsActivity.super.onBackPressed();
            }
        });


        book_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_destination.getText().toString().isEmpty()) {
                    Toast.makeText(MapsActivity.this, "Please Select the destination", Toast.LENGTH_SHORT).show();
                } else {
                    String StartPointAddress = et_starting_point.getText().toString();
                    String EndPointAddress = et_destination.getText().toString();
                    String Fare = txt_amount_new.getText().toString();
                    double StartPoint_Lat = latitude;
                    double StartPoint_Lon = longitude;
                    double EndPoint_Lat = lat;
                    double EndPoint_Lon = lon;
                    String Distance = txt_distance.getText().toString();
                    String Duration = duration_tv.getText().toString();


                    Intent intent = new Intent(MapsActivity.this, BokingDetailsActivity.class);
                    intent.putExtra("StartPointAddress", StartPointAddress);
                    intent.putExtra("EndPointAddress", EndPointAddress);
                    intent.putExtra("Fare", Fare);
                    intent.putExtra("StartPoint_Lat", String.valueOf(StartPoint_Lat));
                    intent.putExtra("StartPoint_Lon", String.valueOf(StartPoint_Lon));
                    intent.putExtra("EndPoint_Lat", String.valueOf(EndPoint_Lat));
                    intent.putExtra("EndPoint_Lon", String.valueOf(EndPoint_Lon));
                    intent.putExtra("Duration", Duration);
                    intent.putExtra("Distance", Distance);
                    startActivity(intent);


                }
            }
        });

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getDistanceAndDuration(double lat1, double lon1, double lat2, double lon2) {

        String origin = lat1 + "%2C" + lon1;
        String destination = lat2 + "%2C" + lon2;

        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + destination + "&key="+MAPS_API_KEY+"&departure_time=now&mode=driving";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.e("Cursor Here2",response);
                //Log.e("statuscode", String.valueOf(loginStatusCode));

                if (statuscode == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        Log.e("rows lenth", String.valueOf(jsonArray.length()));

                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("elements");
                        Log.e("element lenth", String.valueOf(jsonArray1.length()));

                        JSONObject jsonObject2 = jsonArray1.getJSONObject(0);

                        JSONObject distance_jsonObject = jsonObject2.getJSONObject("distance");

                        JSONObject duration_jsonObject = jsonObject2.getJSONObject("duration");

                        JSONObject duration_in_traffic_jsonObject = jsonObject2.getJSONObject("duration_in_traffic");

                        String Kilometer = distance_jsonObject.getString("text");
                        String Kilometer_inMeter = distance_jsonObject.getString("value");

                        String NormalDuration = duration_jsonObject.getString("text");
                        String NormalDuration_inSeconds = duration_jsonObject.getString("value");

                        String TarfficDuration = duration_in_traffic_jsonObject.getString("text");
                        String TarfficDuration_inSeconds = duration_in_traffic_jsonObject.getString("value");

                        progressBar.setVisibility(View.GONE);

                        double dist_in_meter = Double.parseDouble(Kilometer_inMeter);
                        int dist_in_km = (int) (dist_in_meter / 1000);

                        Log.e("dist_in_km", String.valueOf(dist_in_km - 1));

                        /*if (dist_in_km>=1){
                            traveldetails_ll.setVisibility(View.VISIBLE);
                            duration_tv.setText("");
                        }else*/
                        if (dist_in_km >= 0 && dist_in_km <= 10) {

                            traveldetails_ll.setVisibility(View.VISIBLE);
                            duration_tv.setText("");

                            txt_amount_new.setText("849");
                            double maxprice = 849 * 0.3;
                            double finaloldprice = 849 + maxprice;
                            txt_amount_old.setText(String.valueOf(finaloldprice));

                        } else if (dist_in_km >= 11 && dist_in_km <= 20) {

                            traveldetails_ll.setVisibility(View.VISIBLE);
                            duration_tv.setText("");

                            int finaldistance = dist_in_km - 10;
                            int finalfare = (finaldistance * 35) + 849;

                            double maxprice = finalfare * 0.3;
                            double finaloldprice = finalfare + maxprice;

                            txt_amount_new.setText(String.valueOf(finalfare));
                            txt_amount_old.setText(String.valueOf(finaloldprice));

                        } else if (dist_in_km >= 21 && dist_in_km <= 30) {

                            traveldetails_ll.setVisibility(View.VISIBLE);
                            duration_tv.setText("");

                            int finaldistance = dist_in_km - 10;
                            int finalfare = (finaldistance * 32) + 849;

                            double maxprice = finalfare * 0.3;
                            double finaloldprice = finalfare + maxprice;

                            txt_amount_new.setText(String.valueOf(finalfare));
                            txt_amount_old.setText(String.valueOf(finaloldprice));

                        } else {

                            traveldetails_ll.setVisibility(View.VISIBLE);
                            duration_tv.setText("");

                            int finaldistance = dist_in_km - 10;
                            int finalfare = (finaldistance * 30) + 849;

                            double maxprice = finalfare * 0.3;
                            double finaloldprice = finalfare + maxprice;

                            txt_amount_new.setText(String.valueOf(finalfare));
                            txt_amount_old.setText(String.valueOf(finaloldprice));

                        }
                        txt_distance.setText(String.valueOf(dist_in_km) + " KM");
                        duration_tv.setText(TarfficDuration);

                        Log.e("Kilometer", Kilometer);
                        Log.e("Kilometer_inMeter", Kilometer_inMeter);
                        Log.e("NormalDuration", NormalDuration);
                        Log.e("NormalDuration_inSeconds", NormalDuration_inSeconds);
                        Log.e("TarfficDuration", TarfficDuration);
                        Log.e("TarfficDuration_inSeconds", TarfficDuration_inSeconds);


                        Log.e("Cursor Here3", response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }) {
            @Nullable
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statuscode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Volley.newRequestQueue(MainActivity.this).add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("RequestCode", String.valueOf(requestCode));
        //Toast.makeText(this, "onactivity"+stype, Toast.LENGTH_SHORT).show();
        Log.e("ResultCode", "" + resultCode);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            if (stype == "Source") {
                if (et_destination.getText().toString().isEmpty()) {

                } else {
                    flag++;
                }

                marker.remove();
                et_starting_point.setText(place.getAddress());
                latlng = place.getLatLng();
                latitude = latlng.latitude;
                longitude = latlng.longitude;
                LatLng markerLoc = new LatLng(latitude, longitude);
                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(markerLoc)      // Sets the center of the map to Mountain View
                        .zoom(13)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   //

                googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user_location1)));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        return true;
                    }
                });

//                latlng = (String.valueOf(place.getLatLng()));
//                latlng = latlng.replaceAll("lat/long:", "");
//                latlng = latlng.replace("(", "");
//                latlng = latlng.replace(")", "");
//                String[] split = latlng.split(",");
//                latitude = Double.parseDouble(split[0]);
//                longitude = Double.parseDouble(split[1]);

            } else if (stype == "Destination") {
                flag++;
                et_destination.setText(place.getAddress());
                //Toast.makeText(this, place.getAddress(), Toast.LENGTH_SHORT).show();
                latlng_destination = (place.getLatLng());
                lat = latlng_destination.latitude;
                lon = latlng_destination.longitude;
                Findroutes(new LatLng(latitude, longitude), latlng_destination);
                /*latlng_destination = latlng_destination.replaceAll("lat/long:", "");
                latlng_destination = latlng_destination.replace("(", "");
                latlng_destination = latlng_destination.replace(")", "");*/
                //String[] split = latlng.split(",");
                //lat_dest = Double.parseDouble(split[0]);
                //long_dest = Double.parseDouble(split[1]);
                Log.e("Lat/lon", lat + "," + lon);

            }
            if (flag >= 1) {
                // Toast.makeText(this," "+latitude+" "+longitude+" "+lat+" "+lon,Toast.LENGTH_SHORT).show();
                //distance(latitude,longitude,lat,lon);
                getDistanceAndDuration(latitude, longitude, lat, lon);
                traveldetails_ll.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

            }
        } else if (resultCode == AutocompleteActivity.RESULT_OK) {
            Status status = Autocomplete.getStatusFromIntent(data);
            //Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void addDriverMarker() {
        driverArrayList= new ArrayList<>();
        ValueEventListener ref= FirebaseDatabase.getInstance().getReference("Drivers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelDriver modelDriver=ds.getValue(ModelDriver.class);
                            Log.e("Driver Latitude",""+modelDriver.getLatitude());
                            LatLng latLng=new LatLng(Double.parseDouble(modelDriver.getLatitude()),Double.parseDouble(modelDriver.getLongitude()));
                            googleMap.addMarker(new MarkerOptions().position(latLng).title(""+modelDriver.getName()).icon(bitmapDescriptorFromVector(MapsActivity.this,R.drawable.ic_location_ambulance)));


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, Location_Request_code);
    }

    //    private void detectLocation() {
//        Toast.makeText(this, "Detecting Location", Toast.LENGTH_LONG).show();
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Location_Request_code: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        //Permission Allowed
                        getLocation();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Location Permission Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;


    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        progressDialog.dismiss();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        currentlocation = location;

                        try {
                            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1


                            );
                            address = addresses.get(0).getAddressLine(0);
                            latitude = addresses.get(0).getLatitude();
                            longitude = addresses.get(0).getLongitude();
                            Log.e("SourceLat", "" + latitude);
                            et_starting_point.setText(address);
                            et_starting_point.setFocusable(false);
                            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            supportMapFragment.getMapAsync(MapsActivity.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                }
            });
        }else{
            Toast.makeText(this, "Your Location is Turned Off.", Toast.LENGTH_SHORT).show();
            getLocation();
        }
    }


//    private void findAddress() {
//        progressDialog.dismiss();
//        Geocoder geocoder;
//        List<Address> addresses;
//        geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            String address = addresses.get(0).getAddressLine(0);
//            et_starting_point.setText(address);
//            et_starting_point.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    stype="Source";
//                    List<Place.Field> fieldList= Arrays.asList(Place.Field.ADDRESS
//                            ,Place.Field.LAT_LNG,Place.Field.NAME);
//
//                    Intent intent=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList)
//                            .build(getApplicationContext());
//                    startActivityForResult(intent,100);
//
//                }
//            });
//
//        } catch (Exception e) {
//            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//    }


    private void distance(double latitude, double longitude, double lat_dest, double long_dest) {
//            double long_diff = longitude - long_dest;
//            double distance = Math.sin(deg2rad(latitude))
//                    * Math.sin(deg2rad(lat_dest))
//                    + Math.cos(deg2rad(latitude))
//                    * Math.cos(deg2rad(lat_dest))
//                    * Math.cos(deg2rad(long_diff));
//            distance = Math.acos(distance);
//            distance = rad2deg(distance);
//            distance = distance * 60 * 1.1515;
//            distance = distance * 1.609344;
//            Log.d("Distance", "" + distance);
        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude, lat_dest, long_dest, results);
        float distance = results[0];
        int dist_in_km = (int) (distance / 1000);
        if (dist_in_km >= 1) {
            traveldetails_ll.setVisibility(View.VISIBLE);
            durationhour_tv.setText("");
            durationminuite_tv.setText("");
        }

        // Toast.makeText(this,""+dist_in_km,Toast.LENGTH_LONG).show();

        float duration = dist_in_km * 3;

        if (duration >= 60) {
            double hour = duration / 60;

            String finalhour = String.valueOf(hour).substring(0, 1);
            int finalhour2 = Integer.parseInt(finalhour);
            Log.e("finalhour", String.valueOf(finalhour2));

            int hourtominuite = finalhour2 * 60;
            double finalminuite = duration - hourtominuite;
            int finalminuite2 = (int) finalminuite;
            Log.e("finalhour", String.valueOf(finalminuite));

            durationhour_tv.setText(finalhour2 + " hour ");
            durationminuite_tv.setText(String.valueOf(finalminuite2) + " minuite");


        } else {
            durationminuite_tv.setText(String.valueOf(duration) + " minuite");
        }


        if (dist_in_km <= 10) {
            txt_amount_new.setText("849");
            double maxprice = 849 * 0.6;
            double finaloldprice = 849 + maxprice;
            txt_amount_old.setText(String.valueOf(finaloldprice));
        } else if (dist_in_km <= 20) {
            int finaldistance = dist_in_km - 10;
            int finalfare = (finaldistance * 35) + 849;

            double maxprice = finalfare * 0.6;
            double finaloldprice = finalfare + maxprice;

            txt_amount_new.setText(String.valueOf(finalfare));
            txt_amount_old.setText(String.valueOf(finaloldprice));
        } else if (dist_in_km > 20) {
            int finaldistance = dist_in_km - 20;
            int finalfare = (finaldistance * 30) + 1199;

            double maxprice = finalfare * 0.6;
            double finaloldprice = finalfare + maxprice;

            txt_amount_new.setText(String.valueOf(finalfare));
            txt_amount_old.setText(String.valueOf(finaloldprice));

        }
        txt_distance.setText(String.valueOf(dist_in_km) + " KM");


    }

    private double rad2deg(double distance) {
        return (distance * 180.0 / Math.PI);

    }

    private double deg2rad(double latitude) {
        return (latitude * Math.PI / 180.0);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        //googleMap.setMapType(1);

            /*googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));*/
        LatLng latLng;
        latLng = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
        marker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(MapsActivity.this, R.mipmap.ic_user_location1)).title("User Location"));
//            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
//                    .title("Your Current Location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        Log.e("HEllo","going to call");
        addDriverMarker();


    }

    public void Findroutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
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

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//        Findroutes(start,end);

    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(MapsActivity.this, "Finding Route...", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
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

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        endMarker.icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_location_ambulance));
        googleMap.addMarker(endMarker);
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(latlng, latlng_destination);
    }


}
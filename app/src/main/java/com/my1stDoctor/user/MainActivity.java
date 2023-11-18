package com.my1stDoctor.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageView registration;
//    String url1 = "https://firebasestorage.googleapis.com/v0/b/e-upchaar.appspot.com/o/WhatsApp%20Image%202022-03-24%20at%2022.35.59.jpeg?alt=media&token=15cffd27-c6eb-4513-868b-aca60de83826";
//    String url2="https://firebasestorage.googleapis.com/v0/b/e-upchaar.appspot.com/o/WhatsApp%20Image%202022-03-25%20at%2012.03.48.jpeg?alt=media&token=9a50bf91-89e0-4c39-a9ed-9e20a5067b98";

    MaterialCardView ambulance_card,joinUs_card,labtest_card,store_card,hospital_card,call_card;

    LinearLayout home_LL,Booking_LL,Payout_LL;

    MaterialButton book_appointment_btn;
    FirebaseAuth firebaseAuth;

    private String[] locationPermissions;
    private static final int Location_Request_code = 200;
    private LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    public Location currentlocation;
    private ArrayList<String> bannerList;
    private TextView txt_location;

    String address,locality;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();
        txt_location=findViewById(R.id.txt_current_location);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.statusbar));

        }

        home_LL=findViewById(R.id.home_LL);
        Booking_LL=findViewById(R.id.Booking_LL);
        Payout_LL=findViewById(R.id.Payout_LL);

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        book_appointment_btn=findViewById(R.id.book_appointment_btn);
        book_appointment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode("9129250102"))));

            }
        });

        Booking_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, UserBookingHistoryActivity.class);
                startActivity(intent);
            }
        });

        Payout_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        registration=findViewById(R.id.registration);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();

                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
        ambulance_card=findViewById(R.id.ambulance_card);
        joinUs_card=findViewById(R.id.joinUs_card);
        labtest_card=findViewById(R.id.labtest_card);
        store_card=findViewById(R.id.store_card);
        hospital_card=findViewById(R.id.hospital_card);
        call_card=findViewById(R.id.call_card);

        ambulance_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });

        joinUs_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,JoinUsActivity.class);
                startActivity(intent);
            }
        });
        labtest_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CommingSoonActivity.class);
                startActivity(intent);
            }
        });
        store_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CommingSoonActivity.class);
                startActivity(intent);
            }
        });
        hospital_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CommingSoonActivity.class);
                startActivity(intent);
            }
        });
        call_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CommingSoonActivity.class);
                startActivity(intent);
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkLocationPermission()) {
            getLocation();
        } else {
            requestLocationPermission();
        }

        addBannerUrls();

    }
    private Boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, Location_Request_code);
    }
    @SuppressLint("MissingPermission")
    private void getLocation() {

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    currentlocation = location;

                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1


                        );
                        address = addresses.get(0).getSubLocality();
                        Log.e("address_City",address);
                        locality = addresses.get(0).getSubAdminArea();

                        txt_location.setText(address+","+locality);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }


    private void addBannerUrls() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Banners");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bannerList=new ArrayList<>();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String Url=ds.getValue(String.class);
                    Log.e("Url of banner",Url);
                    bannerList.add(Url);
                }
                startSliderBanner();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void startSliderBanner() {
        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

        // initializing the slider view.
        SliderView sliderView =findViewById(R.id.slider);
        // adding the urls inside array list
        for(int i=0;i<bannerList.size();i++){
            sliderDataArrayList.add(new SliderData(bannerList.get(i)));
        }

//        sliderDataArrayList.add(new SliderData(url2));
//        sliderDataArrayList.add(new SliderData(url1));
//        // passing this array list inside our adapter class.
        SliderAdapter adapter = new SliderAdapter(MainActivity.this, sliderDataArrayList);

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter);

        // below method is use to set
        // scroll time in seconds.
        sliderView.setScrollTimeInSec(5);

        // to set it scrollable automatically
        // we use below method.
        sliderView.setAutoCycle(true);

        // to start autocycle below method is used.
        sliderView.startAutoCycle();


    }
}
package com.my1stDoctor.user;

import static android.view.View.GONE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserRegistrationActivity extends AppCompatActivity {

    private static final int Location_Request_code = 100;
    private static final int Camera_Request_code = 200;
    private static final int Storage_Request_code = 300;
    private static final int Image_pick_gallery_code = 400;
    private static final int Image_pick_camera_code = 500;


    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    Uri image_uri;
    ImageView iv_logo;
    Bitmap ProfilePhoto=null;
    EditText tiet_user_name,et_user_mobile_no,tiet_user_email,til_user_password,til_user_confirm_password;
    Button btn_submit_Registration;

    MaterialCardView btn_ProfilePhoto_upload;
    String user_name,user_mobile,user_password,user_c_password,user_email;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    String phone_user;

    String address;
    FusedLocationProviderClient fusedLocationProviderClient;
    public Location currentlocation;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(UserRegistrationActivity.this, R.color.statusbar));
        }
        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);


// The value will be default as empty string because for
        phone_user = sh.getString("phone", "");

        iv_logo=findViewById(R.id.iv_logo);
        btn_ProfilePhoto_upload=findViewById(R.id.btn_ProfilePhoto_upload);

        btn_ProfilePhoto_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickdialog() ;
            }
        });
        tiet_user_name=findViewById(R.id.tiet_user_name);
        et_user_mobile_no=findViewById(R.id.et_user_mobile_no);
        if(!phone_user.equals(null)){
            et_user_mobile_no.setFocusable(false);
            et_user_mobile_no.setClickable(false);
            et_user_mobile_no.setText(phone_user);
        }
        tiet_user_email=findViewById(R.id.tiet_user_email);
        til_user_password=findViewById(R.id.til_user_password);
        til_user_password.setVisibility(GONE);
        til_user_confirm_password=findViewById(R.id.til_user_confirm_password);
        til_user_confirm_password.setVisibility(GONE);


        btn_submit_Registration=findViewById(R.id.btn_submit_Registration);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        firebaseAuth=FirebaseAuth.getInstance();

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        btn_submit_Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
        if (checkLocationPermission()) {
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
            getLocation();
        } else {
            requestLocationPermission();
        }




    }
    @SuppressLint("MissingPermission")
    private void getLocation () {
        progressDialog.dismiss();
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    currentlocation = location;

                    try {
                        Geocoder geocoder = new Geocoder(UserRegistrationActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1


                        );
                        address = addresses.get(0).getAddressLine(0);
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                        Log.e("SourceLat", "" + latitude);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, Location_Request_code);
    }


    private void inputData() {
        user_name=tiet_user_name.getText().toString().trim();
        user_email=tiet_user_email.getText().toString().trim();
        user_password=til_user_password.getText().toString().trim();
        user_c_password=til_user_confirm_password.getText().toString().trim();
        user_mobile=et_user_mobile_no.getText().toString().trim();
        validateData();
    }



    private void validateData() {
        if(TextUtils.isEmpty(user_name)){
            Toast.makeText(UserRegistrationActivity.this,"User Name Empty",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(user_email) || !Patterns.EMAIL_ADDRESS.matcher(user_email).matches()){
            Toast.makeText(UserRegistrationActivity.this,"Invalid Email",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(user_mobile)|| user_mobile.length()<10){
            Toast.makeText(UserRegistrationActivity.this,"Invalid Mobile Number",Toast.LENGTH_SHORT).show();
        }else if(user_mobile.equals(phone_user)){
            saveFirebaseData();
        }else{
            registerAccount();
        }

    }

    private void registerAccount() {
        progressDialog.setMessage("Creating Account......");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(user_email,user_password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UserRegistrationActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void saveFirebaseData() {
        progressDialog.setMessage("Sacing Account Info....");
        progressDialog.show();
        String timestamp=""+System.currentTimeMillis();
        if(image_uri==null){
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+user_email);
            hashMap.put("name",""+user_name);
            hashMap.put("phone",""+user_mobile);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longitude",""+longitude);
            hashMap.put("timeStamp",""+timestamp);
            hashMap.put("online","true");
            hashMap.put("accountType","User");
            hashMap.put("profileImage","");
            //Saving to database

            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            startActivity(new Intent(UserRegistrationActivity.this,MainActivity.class));
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            startActivity(new Intent(UserRegistrationActivity.this,MainActivity.class));
                            finish();

                        }
                    });
        }
        else{
            String filePathAndName="profile_images/"+""+firebaseAuth.getUid();
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri=uriTask.getResult();
                            if(uriTask.isSuccessful()){
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+user_email);
                                hashMap.put("name",""+user_name);
                                hashMap.put("phone",""+user_mobile);
                                hashMap.put("address",""+address);
                                hashMap.put("latitude",""+latitude);
                                hashMap.put("longitude",""+longitude);
                                hashMap.put("timeStamp",""+timestamp);
                                hashMap.put("accountType","User");
                                hashMap.put("profileImage",""+downloadImageUri);
                                //Saving to database

                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(UserRegistrationActivity.this,MainActivity.class));
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(UserRegistrationActivity.this,MainActivity.class));
                                                finish();

                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserRegistrationActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void showImagePickdialog() {
        String[] options={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            if(check_camera_permission()){
                                pick_from_camera();
                            }
                            else{
                                requestCameraPermission();
                            }

                        }
                        else{
                            if(check_storage_permission()){
                                pick_from_gallery();

                            }
                            else{
                                requestStoragePermission();
                            }

                        }
                    }
                }).show();
    }

    private void pick_from_gallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,Image_pick_gallery_code);
    }

    private void pick_from_camera(){
        /*ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_ Image Description");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent=new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,Image_pick_camera_code);
    }

    private boolean check_storage_permission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,Storage_Request_code);

    }

    private boolean check_camera_permission(){
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,Camera_Request_code);

    }
    private Boolean checkLocationPermission () {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
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
            case Camera_Request_code: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //Permission Allowed
                    } else {
                        //permission denied
                        pick_from_camera();
                        Toast.makeText(this, "Camera Permissions Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case Storage_Request_code: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        //Permission Allowed
                        Log.d("compiler","is at current position");
                        pick_from_gallery();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Storage Permission Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == Image_pick_gallery_code) {
                image_uri = data.getData();
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
                    ProfilePhoto = bitmap;
                    iv_logo.setImageBitmap(ProfilePhoto);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //iv_logo.setImageURI(image_uri);


            } else if (requestCode == Image_pick_camera_code) {
                Log.e("uri", String.valueOf(data.getData()));
                //image_uri=data.getData();
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ProfilePhoto = imageBitmap;
                iv_logo.setImageBitmap(ProfilePhoto);

            }
            super.onActivityResult(requestCode, resultCode, data);
        }


    }
}
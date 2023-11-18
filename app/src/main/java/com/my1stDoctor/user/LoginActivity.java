package com.my1stDoctor.user;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    MaterialCardView User_Card, Driver_Card;
    String LOGIN_TYPE = "none";
    Chip chipEmail, chipMobile;
    LinearLayout EmailPass_Layout, MobileOtp_Layout;
    TextInputEditText MobileNo_ET, Email_ET, Pass_ET;
    PinView MobileOtp_Pinview;
    MaterialButton btn_SendOtp, btn_MobileOtp_Login, btn_EmailPass_Login;
    TextView SignUpUser_TV, SignUpDriver_TV;
    String email, pswrd;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    String user_type = "User";
    String phone_verify;
    private String verificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(LoginActivity.this, R.color.statusbar));
        }

        User_Card = findViewById(R.id.User_Card);
        Driver_Card = findViewById(R.id.Driver_Card);
        chipEmail = findViewById(R.id.chipEmail);
        chipMobile = findViewById(R.id.chipMobile);
        EmailPass_Layout = findViewById(R.id.EmailPass_Layout);
        MobileOtp_Layout = findViewById(R.id.MobileOtp_Layout);
        phone_verify="verify";

        MobileNo_ET = findViewById(R.id.MobileNo_ET);
        MobileOtp_Pinview = findViewById(R.id.MobileOtp_Pinview);

        btn_SendOtp = findViewById(R.id.btn_SendOtp);

        btn_MobileOtp_Login = findViewById(R.id.btn_MobileOtp_Login);
        btn_EmailPass_Login = findViewById(R.id.btn_EmailPass_Login);

        Email_ET = findViewById(R.id.Email_ET);
        Pass_ET = findViewById(R.id.Pass_ET);

        SignUpUser_TV = findViewById(R.id.SignUpUser_TV);


        SignUpDriver_TV = findViewById(R.id.SignUpDriver_TV);




        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        chipMobile.setChipBackgroundColor(ColorStateList.valueOf(0xff0091EA));
        chipMobile.setTextColor(0xFFFFFFFF);
        chipEmail.setChipBackgroundColor(ColorStateList.valueOf(0xffE8E8E8));
        chipEmail.setTextColor(0xFF0091EA);

        firebaseAuth = FirebaseAuth.getInstance();


        chipEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipEmail.setChipBackgroundColor(ColorStateList.valueOf(0xff0091EA));
                chipEmail.setTextColor(0xFFFFFFFF);
                chipMobile.setChipBackgroundColor(ColorStateList.valueOf(0xffE8E8E8));
                chipMobile.setTextColor(0xFF0091EA);
                EmailPass_Layout.setVisibility(View.VISIBLE);
                MobileOtp_Layout.setVisibility(View.GONE);
                MobileNo_ET.setText(null);
                MobileOtp_Pinview.setText(null);
            }
        });

        chipMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipMobile.setChipBackgroundColor(ColorStateList.valueOf(0xff0091EA));
                chipMobile.setTextColor(0xFFFFFFFF);
                chipEmail.setChipBackgroundColor(ColorStateList.valueOf(0xffE8E8E8));
                chipEmail.setTextColor(0xFF0091EA);
                MobileOtp_Layout.setVisibility(View.VISIBLE);
                EmailPass_Layout.setVisibility(View.GONE);
                Email_ET.setText(null);
                MobileNo_ET.setText(null);
            }
        });

        btn_SendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MobileNo_ET.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Enter The Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    // Send The Otp
                    Log.e("Otp", "" + MobileOtp_Pinview.getText().toString());
                    String phone = "+91" + MobileNo_ET.getText().toString();
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.custom_progress_bar);
                    progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                    progressDialog.setCancelable(false);
                    sendVerificationCode(phone);

                }

            }
        });


        btn_EmailPass_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                 if (Email_ET.getText().toString().isEmpty()){
//                    Toast.makeText(LoginActivity.this, "Please Enter The Email", Toast.LENGTH_SHORT).show();
//                }else if (Pass_ET.getText().toString().isEmpty()){
//                    Toast.makeText(LoginActivity.this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
//                }else {
                loginUser();
                //Verify The Email And Password And Login
            }

        });

        btn_MobileOtp_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setContentView(R.layout.custom_progress_bar);
                progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                progressDialog.setCancelable(false);
                if (MobileNo_ET.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Enter The Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (MobileOtp_Pinview.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Enter The Otp", Toast.LENGTH_SHORT).show();
                } else {
                    //V verifyCode(edtOTP.getText().toString());erify The Otp And SignUp
                    verifyCode(MobileOtp_Pinview.getText().toString());
                }
            }
        });

        SignUpUser_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //User Registration
                Intent intent = new Intent(LoginActivity.this, UserRegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        SignUpDriver_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Driver Registration
//                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

    }

    private void loginUser() {
        email = Email_ET.getText().toString().trim();
        pswrd = Pass_ET.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pswrd)) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;

        }
        progressDialog.setMessage("Logging In.....");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, pswrd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkuserType();
                        ;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeMeOnline() {
        progressDialog.setMessage("Checking User");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        DatabaseReference ref;
        if (user_type == "User") {
            ref = FirebaseDatabase.getInstance().getReference("Users");
        } else {
            ref = FirebaseDatabase.getInstance().getReference("Driver");
        }
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                           checkExistingUser();

                        } else {
                            Toast.makeText(getApplicationContext(),"Some Error Occurred",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void checkExistingUser() {
        Log.e("VerifyPhone----------",MobileNo_ET.getText().toString());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        if(snapshot.hasChild(firebaseAuth.getUid())){
                            Log.e("Snapshot",""+snapshot.hasChild(firebaseAuth.getUid()));
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Log.e("SnapshotElse",""+snapshot.hasChild(firebaseAuth.getUid()));

                            Intent i = new Intent(LoginActivity.this, UserRegistrationActivity.class);
                            startActivity(i);
                            finish();
                            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putString("phone", MobileNo_ET.getText().toString());
                            myEdit.commit();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();

                    }
                });
    }

    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressDialog.dismiss();
                        verificationId = s;
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }

    private void checkuserType() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("uid")) {
                    progressDialog.dismiss();
                    user_type = "User";
//                    makeMeOnline();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    user_type = "Driver";
//                    makeMeOnline();
//                    startActivity(new Intent(LoginActivity.this, com.doc.my1stdoctor.MainDriverActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
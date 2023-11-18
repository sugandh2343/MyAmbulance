package com.my1stDoctor.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserBookingHistoryActivity extends AppCompatActivity {
    ListView BookingHistory_listview;
    FirebaseAuth firebaseAuth;
    String user_uid,driver_name,driver_mob;
    ArrayList<PresentBookingModel> presentBookinglist=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking_history);

        BookingHistory_listview=findViewById(R.id.BookingHistory_listview);
        firebaseAuth=FirebaseAuth.getInstance();
        getFirebaseData();




    }
    private void getFirebaseData() {
        presentBookinglist=new ArrayList<>();
        ValueEventListener reference = FirebaseDatabase.getInstance().getReference("Bookings")
                .orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        presentBookinglist.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            PresentBookingModel presentBookingModel = ds.getValue(PresentBookingModel.class);
                            presentBookinglist.add(presentBookingModel);
                            Log.e("PatientTimestamp", ""+presentBookingModel.getTimeStamp());
                            Log.e("PatientAddress", ""+presentBookingModel.getEndPointAddress());

                        }
                        UserBookingHistoryAdapter userBookingHistoryAdapter=new UserBookingHistoryAdapter();
                        BookingHistory_listview.setAdapter(userBookingHistoryAdapter);




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    class UserBookingHistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if(!((presentBookinglist.size()) ==0)){
                return presentBookinglist.size();
            }else{
                return 0;
            }

        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.user_booking_history_child,null);

            TextView name_TV = row.findViewById(R.id.name_TV);
            TextView mobile_TV = row.findViewById(R.id.mobile_TV);
            TextView addressFrom_TV = row.findViewById(R.id.addressFrom_TV);
            TextView addressTo_TV = row.findViewById(R.id.addressTo_TV);
            TextView duration_TV = row.findViewById(R.id.duration_TV);
            TextView Fare_TV = row.findViewById(R.id.Fare_TV);
//            name_TV.setText(presentBookinglist.get(position).getPassenger_name());
//            mobile_TV.setText(presentBookinglist.get(position).getPassenger_mob());
            if(!((presentBookinglist.size()) ==0)) {
                addressFrom_TV.setText(presentBookinglist.get(position).getStartPointAddress());
                addressTo_TV.setText(presentBookinglist.get(position).getEndPointAddress());
                duration_TV.setText(presentBookinglist.get(position).getDuration());
                Fare_TV.setText(presentBookinglist.get(position).getFare());
            }

            //name_TV.setText(presentBookinglist.get(position).getPassenger_name());
            //mobile_TV.setText(presentBookinglist.get(position).getPassenger_mob());
            //addressFrom_TV.setText(presentBookinglist.get(position).getStartPointAddress());
           // addressTo_TV.setText(presentBookinglist.get(position).getEndPointAddress());
           // duration_TV.setText(presentBookinglist.get(position).getDuration());
           // distance_TV.setText("( "+presentBookinglist.get(position).getDistance()+" )");
            //Fare_TV.setText(presentBookinglist.get(position).getFare());


            return row;
        }
    }

    public void back_BTN_Pressed(View view){
        super.onBackPressed();
    }
}
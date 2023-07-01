package com.sha.smartden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MinerDetails extends AppCompatActivity {
    private TextView _class,_temperature,_fire,_gas,_humidity,_accident;

    private EnvApiInterface envApi;

    int count =0;

    public String temp;
    public String fire;
    public String gas;
    public String hum;
    public String acc;
    public String emess;


    SharedPreferences sharedpreferences;
    String miner_id;
    public static final String MyPREFERENCES = "MyPrefs" ;

    NotificationManagerCompat notificationManagerCompat;
    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miner_details);

        _class = (TextView) findViewById(R.id.txtViewClass);
        _temperature = (TextView) findViewById(R.id.txtViewTemp);
        _fire = (TextView) findViewById(R.id.txtViewFire);
        _gas = (TextView) findViewById(R.id.txtViewGas);
        _humidity = (TextView) findViewById(R.id.txtViewHumidity);
        _accident = (TextView) findViewById(R.id.txtViewAccident);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            miner_id  = sharedpreferences.getString("miner_id_for_admin", null);
            myRef = database.getReference(miner_id);
        } else {
            // No user is signed in
            miner_id = sharedpreferences.getString("miner_id", null);
            myRef = database.getReference(miner_id);
        }


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                temp = (String) dataSnapshot.child("Temperature").getValue();
                fire = (String) dataSnapshot.child("Fire").getValue();
                gas = (String) dataSnapshot.child("Gas").getValue();
                hum = (String) dataSnapshot.child("Humidity").getValue();
                acc = (String) dataSnapshot.child("Accident").getValue();
                emess = (String) dataSnapshot.child("EMessage").getValue();

                if(acc.equals("1")){
                    _accident.setText("Accident Detected");
                    count +=1;
                    notification(acc,emess,count);
                    SmsManager sms=SmsManager.getDefault();
                    sms.sendTextMessage("03241456711", "03486576541", "***SmartDen***\nMiner Accident Detected!!\nMiner Id : " + miner_id, null,null);
                }
                if(emess.equals("1")){
                    count +=1;
                    notification(acc,emess,count);
                    SmsManager sms=SmsManager.getDefault();
                    sms.sendTextMessage("03241456711", "03486576541", "***SmartDen***\nMiner Accident Detected!!\nMiner Id : " + miner_id, null,null);
                }

                if(temp.equals("1")){
                    _temperature.setText("Temperature Detected");
                }
                else{
                    _temperature.setText("No Temperature Detected");
                }

                if(fire.equals("1")){
                    _fire.setText("Fire Detected");
                }
                else{
                    _fire.setText("No Fire Detected");
                }

                if(hum.equals("1")){
                    _humidity.setText("Humidity Detected");
                }
                else{
                    _humidity.setText("No Humidity Detected");
                }

                if(gas.equals("1")){
                    _gas.setText("Gas Detected");
                }
                else{
                    _gas.setText("No Gas Detected");
                }

                if(acc.equals("1")){
                    _accident.setText("Accident Detected");
                }
                else{
                    _accident.setText("No Accident Detected");
                }



                Log.d("db", "Value is: " + temp + " " + fire + " " + gas + " " + hum);


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://shaghil65.pythonanywhere.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();



                envApi = retrofit.create(EnvApiInterface.class);


                String url = "https://shaghil65.pythonanywhere.com/" + "getdata/" + temp + "/"+ fire + "/"+ hum + "/"+ gas + "/0/0/0/0/0";

                Call<results> call = envApi.getEnvData(url);

                ProgressDialog progress = new ProgressDialog(MinerDetails.this);
                progress.setTitle("Loading");
                progress.setMessage("Please wait...");
                progress.setIndeterminate(true);
                progress.show();

                call.enqueue(new Callback<results>() {
                    @Override
                    public void onResponse(Call<results> call, Response<results> response) {
                        if (!response.isSuccessful()) {
                            Log.e("api", "onResponse: " + "Response nahi arraha" );
                            return;
                        }


                        results result = response.body();
                        if(response.isSuccessful()){
                            Log.e("api", "onResponse: " + result.gettClass() );
                            _class.setText(result.gettClass() + "");
                            progress.dismiss();
                        }
                        else{
                            Log.e("gas", "onResponse: " + "notSuccessfull" );
                        }
                    }
                    @Override
                    public void onFailure(Call<results> call, Throwable t) {
                        Log.e("api", "onFailure: " + t.getLocalizedMessage() );
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("db", "Failed to read value.", error.toException());
            }
        });
    }
    private void notification(String acc,String emess, int count) {
        NotificationChannel channel = new NotificationChannel("myCh","myChannel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        if (acc.equals("1")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"myCh")
                    .setSmallIcon(android.R.drawable.stat_notify_sync)
                    .setContentTitle("Push Notification")
                    .setContentText("Accident Detected!");
            notification = builder.build();
            notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(count,notification);
        }
        if (emess.equals("1")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"myCh")
                    .setSmallIcon(android.R.drawable.stat_notify_sync)
                    .setContentTitle("Push Notification")
                    .setContentText("Need Emergency Help");
            notification = builder.build();
            notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(count,notification);
        }
    }
}
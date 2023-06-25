package com.sha.smartden;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MinerHealth extends AppCompatActivity {

    private TextView _class,_temperature,_heart,_steps;

    private HealthApiInterface healthApi;

    public String temp;
    public String heart;
    public String steps;


    SharedPreferences sharedpreferences;
    String miner_id;
    public static final String MyPREFERENCES = "MyPrefs" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miner_health);


        _class = (TextView) findViewById(R.id.txtViewClass);
        _temperature = (TextView) findViewById(R.id.txtViewTemp);
        _heart = (TextView) findViewById(R.id.txtViewHeart);
        _steps = (TextView) findViewById(R.id.txtViewSteps);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            miner_id = sharedpreferences.getString("miner_id_for_admin", null);
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
                temp = (String) dataSnapshot.child("BTemp").getValue();
                heart = (String) dataSnapshot.child("Heart").getValue();
                steps = (String) dataSnapshot.child("Steps").getValue();
                _steps.setText(steps);
                _temperature.setText(temp);
                _heart.setText(heart);

                Log.d("db", "Value is: " + temp + " " + heart);

                //
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://shaghil89.pythonanywhere.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();



                healthApi = retrofit.create(HealthApiInterface.class);


                String url = "https://shaghil89.pythonanywhere.com/" + "getdata/" + temp + "/"+ heart;

                Call<result2> call = healthApi.getHealthData(url);

                ProgressDialog progress = new ProgressDialog(MinerHealth.this);
                progress.setTitle("Loading");
                progress.setMessage("Please wait...");
                progress.setIndeterminate(true);
                progress.show();

                call.enqueue(new Callback<result2>() {
                    @Override
                    public void onResponse(Call<result2> call, Response<result2> response) {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        result2 result = response.body();
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
                    public void onFailure(Call<result2> call, Throwable t) {
                        Log.e("api", "onFailure: " + t.getLocalizedMessage() );
                    }
                });

                //
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("db", "Failed to read value.", error.toException());
            }
        });



    }
}
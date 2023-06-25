package com.sha.smartden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

public class AdminEmployeeDetail extends AppCompatActivity {

    TextView miner_name;
    private CardView mc4,mc2,mc3,mc1;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    String miner_username_for_admin, miner_id_for_admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_employee_detail);

        miner_name = findViewById(R.id.miner_name);

        mc1 = (CardView) findViewById(R.id.mc1);
        mc2 = (CardView) findViewById(R.id.mc2);
        mc3 = (CardView) findViewById(R.id.mc3);
        mc4 = (CardView) findViewById(R.id.mc4);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        miner_username_for_admin = sharedpreferences.getString("miner_username_for_admin", null);

        miner_name.setText(miner_username_for_admin.toUpperCase(Locale.ROOT));

        mc1.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),MinerPersonalInformation.class));
        });
        mc4.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),MinerDetails.class));

        });
        mc2.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),MinerHealth.class));
        });
        mc3.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
        });

    }
}
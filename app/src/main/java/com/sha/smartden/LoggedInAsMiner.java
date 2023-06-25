package com.sha.smartden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class LoggedInAsMiner extends AppCompatActivity {
    TextView miner_name;
    SharedPreferences sharedpreferences;
    String miner_username;
    public static final String MyPREFERENCES = "MyPrefs" ;

    private CardView mc4,mc5,mc2,mc3,mc6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_as_miner);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        miner_username = sharedpreferences.getString("miner_username", null);

        miner_name = findViewById(R.id.miner_name);
        miner_name.setText("Hi, " + miner_username.toUpperCase(Locale.ROOT));

        mc5 = (CardView) findViewById(R.id.mc5);
        mc2 = (CardView) findViewById(R.id.mc2);
        mc3 = (CardView) findViewById(R.id.mc3);
        mc4 = (CardView) findViewById(R.id.mc4);
        mc6 = (CardView) findViewById(R.id.mc6);

        mc5.setOnClickListener(v -> {
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

        mc6.setOnClickListener(v -> {
            SharedPreferences sharedpreferences = getSharedPreferences(LoginUserActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(getApplicationContext(),LoginUserActivity.class));
            finish();
        });

    }
}
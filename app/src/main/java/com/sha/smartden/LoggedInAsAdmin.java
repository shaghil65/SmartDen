package com.sha.smartden;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoggedInAsAdmin extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private CardView ac2, ac3, ac6,ac1;
    FirebaseAuth fauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_as_admin);

        ActivityCompat.requestPermissions(LoggedInAsAdmin.this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);

        fauth = FirebaseAuth.getInstance();

        ac2 = (CardView) findViewById(R.id.ac2);
        ac3 = (CardView) findViewById(R.id.ac3);
        ac6 = (CardView) findViewById(R.id.ac6);


        ac6.setOnClickListener(v -> {
            fauth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginUserActivity.class));
            Toast.makeText(LoggedInAsAdmin.this, "Admin Sign out", Toast.LENGTH_SHORT).show();
            finish();
        });
        ac2.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AllEmployees.class)));
        ac3.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegistrationActivity.class)));
    }
}
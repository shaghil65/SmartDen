package com.sha.smartden;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton LoginUser,RegisterUser;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginUser =(AppCompatButton) findViewById(R.id.btnLoginUser);
        RegisterUser =(AppCompatButton) findViewById(R.id.btnRegisterUser);

        LoginUser.setOnClickListener(v -> openLoginUserActivity());
        RegisterUser.setOnClickListener(v -> openRegisterUserActivity());

    }
    private void openLoginUserActivity() {
        Intent intent =new Intent(this,LoginUserActivity.class);
        startActivity(intent);
    }
    private void openRegisterUserActivity() {
        Intent intent =new Intent(this,RegistrationActivity.class);
        startActivity(intent);
    }
}
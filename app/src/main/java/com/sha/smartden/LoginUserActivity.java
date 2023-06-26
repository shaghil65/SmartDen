package com.sha.smartden;

import static com.sha.smartden.PasswordEncryption.getMd5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginUserActivity extends AppCompatActivity {

    private EditText email,password;
    private AppCompatButton loginbutton;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    boolean valid = true;
    boolean isLoggedIn;
    public static final String MyPREFERENCES = "MyPrefs" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        ActivityCompat.requestPermissions(LoginUserActivity.this,new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);


        email= findViewById(R.id.edtTxtEmail);
        password= findViewById(R.id.edtTxtPassword);
        loginbutton = (AppCompatButton) findViewById(R.id.btnLogin);

        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        isLoggedIn = sharedpreferences.getBoolean("isLoggedIn", false);



        loginbutton.setOnClickListener(v -> {
            checkField(email);
            checkField(password);
            if (valid) {
                fauth.signInWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent home = new Intent(LoginUserActivity.this, LoggedInAsAdmin.class);
                        startActivity(home);
                        Toast.makeText(LoginUserActivity.this, "Admin Logged In", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    fstore.collection("Miners")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    int count = 0;
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        String a = doc.getString("email");
                                        String b = doc.getString("password");
                                        String miner_username = doc.getString("username");
                                        int c = Math.toIntExact(doc.getLong("Minerid"));
                                        String a1 = email.getText().toString().trim();
                                        String b1 = getMd5(password.getText().toString().trim());
                                        if (a.equals(a1) & b.equals(b1)) {
                                            count = 1;
                                            Editor editor = sharedpreferences.edit();
                                            editor.putBoolean("isLoggedIn", true);
                                            editor.putString("miner_username", miner_username);
                                            editor.putString("miner_id", String.valueOf(c));
                                            editor.apply();
                                            Intent home = new Intent(LoginUserActivity.this, LoggedInAsMiner.class);
                                            startActivity(home);
                                            Toast.makeText(LoginUserActivity.this, "Miner Logged In", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                    }
                                    if (count == 0) {
                                        Toast.makeText(LoginUserActivity.this, "Cannot login,incorrect Email and Password", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                });

            }
        });
    }

    public boolean checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
            textField.setError("Error");
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }







    @Override
    protected  void onStart() {

        super.onStart();
        if (fauth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),LoggedInAsAdmin.class));
            finish();
        }
        else if (isLoggedIn) {
            startActivity(new Intent(getApplicationContext(),LoggedInAsMiner.class));
            finish();
        }
    }
}

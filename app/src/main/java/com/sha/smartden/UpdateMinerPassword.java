package com.sha.smartden;

import static com.sha.smartden.PasswordEncryption.getMd5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UpdateMinerPassword extends AppCompatActivity {

    private EditText currentpassword, newpassword;
    private Button updatebutton, cancelbutton;

    private ImageView imageView;
    String imgUrl;

    String _currpassword;



    String documentId;
    boolean valid;

    SharedPreferences sharedpreferences;
    String miner_username;
    public static final String MyPREFERENCES = "MyPrefs" ;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_miner_password);

        fstore = FirebaseFirestore.getInstance();
        currentpassword = findViewById(R.id.edtTxtCurrentPassword);
        newpassword = findViewById(R.id.edtTxtNewPassoword);

        imageView = (ImageView) findViewById(R.id.male_avatar);

        updatebutton = (Button) findViewById(R.id.btnUpdate);
        cancelbutton = (Button) findViewById(R.id.btnCancel);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        FirebaseUser userauth = FirebaseAuth.getInstance().getCurrentUser();
        if (userauth != null) {
            miner_username = sharedpreferences.getString("miner_username_for_admin", null);
        } else {
            // No user is signed in
            miner_username = sharedpreferences.getString("miner_username", null);

        }

        fstore.collection("Miners")
                .whereEqualTo("username", miner_username)
                .get().addOnCompleteListener(task -> {

                    if (task.isSuccessful() && !task.getResult().isEmpty()){
                        DocumentSnapshot dc = task.getResult().getDocuments().get(0);
                        documentId = dc.getId();
                    }
                });


        fstore.collection("Miners")
                .whereEqualTo("username", miner_username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            _currpassword = document.getString("password");
                            imgUrl = document.getString("img");
                            Glide.with(UpdateMinerPassword.this).load(imgUrl).into(imageView);
                        }
                    } else {
                        Log.w("failed", "Error getting documents.", task.getException());
                    }
                });

        cancelbutton.setOnClickListener(v -> {
            if (userauth != null) {
                startActivity(new Intent(getApplicationContext(), MinerPersonalInformation.class));
            } else {
                // No user is signed in
                startActivity(new Intent(getApplicationContext(), LoggedInAsMiner.class));

            }

            finish();
        });

        updatebutton.setOnClickListener(v -> {
            String txtCurrentPassoword = currentpassword.getText().toString().trim();
            String txtNewPassword = newpassword.getText().toString().trim();

            CheckCurrentPassword(txtCurrentPassoword,_currpassword);
            validatePassword();

            if(CheckCurrentPassword(txtCurrentPassoword,_currpassword) && validatePassword()){
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("password", getMd5(txtNewPassword));

                    fstore.collection("Miners")
                            .document(documentId)
                            .update(userInfo)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("TAG", "Miner created");
                                Toast.makeText(UpdateMinerPassword.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MinerPersonalInformation.class));
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(UpdateMinerPassword.this, "Failed", Toast.LENGTH_SHORT).show());

                }

        });



    }

    private Boolean CheckCurrentPassword(String txtCurrentPassword,String _currpassword){
        if(getMd5(txtCurrentPassword).equals(_currpassword)){
            currentpassword.setError(null);
            return true;
        }
        else if(txtCurrentPassword.isEmpty()){
            currentpassword.setError("Field cannot be empty");
            return false;
        }
        else{
            currentpassword.setError("Password Incorrect");
            return false;
        }
    }

    private Boolean validatePassword() {
        String val = newpassword.getText().toString().trim();
        String passwordVal = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
//                "(?=.*[a-z])" +         //at least 1 lower case letter
//                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if (val.isEmpty()) {
            newpassword.setError("Field cannot be empty");
            valid = false;
        } else if (!val.matches(passwordVal)) {
            newpassword.setError("Password is too weak");
            valid = false;
        } else {
            newpassword.setError(null);
            valid = true;
        }
        return valid;
    }

}
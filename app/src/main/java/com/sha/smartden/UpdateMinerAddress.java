package com.sha.smartden;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UpdateMinerAddress extends AppCompatActivity {

    private EditText newaddress;
    private Button updatebutton, cancelbutton;

    private ImageView imageView;
    String imgUrl;

    String documentId;
    boolean valid;

    SharedPreferences sharedpreferences;
    String miner_username;
    public static final String MyPREFERENCES = "MyPrefs" ;
    FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_miner_address);

        fstore = FirebaseFirestore.getInstance();
        newaddress = findViewById(R.id.edtTxtNewAddress);

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
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                imgUrl = document.getString("img");
                                Glide.with(UpdateMinerAddress.this).load(imgUrl).into(imageView);
                            }
                        } else {
                            Log.w("failed", "Error getting documents.", task.getException());
                        }
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
            String _newaddress = newaddress.getText().toString().trim();
            validateAddress();

            if(validateAddress()){
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("address", _newaddress);

                fstore.collection("Miners")
                        .document(documentId)
                        .update(userInfo)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(UpdateMinerAddress.this, "Address Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MinerPersonalInformation.class));
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(UpdateMinerAddress.this, "Failed", Toast.LENGTH_SHORT).show());
            }

        });

    }
    private Boolean validateAddress() {
        String val = newaddress.getText().toString().trim();
        if (val.isEmpty()) {
            newaddress.setError("Field cannot be empty");
            valid = false;
        } else {
            newaddress.setError(null);
            valid = true;
        }
        return valid;
    }
}
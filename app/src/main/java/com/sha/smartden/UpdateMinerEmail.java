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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UpdateMinerEmail extends AppCompatActivity {

    private EditText email;
    private Button updatebutton, cancelbutton;

    private ImageView imageView;

    String documentId;
    String imgUrl;
    boolean valid;

    SharedPreferences sharedpreferences;
    String miner_username;
    public static final String MyPREFERENCES = "MyPrefs" ;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_miner_email);
        fstore = FirebaseFirestore.getInstance();
        email = findViewById(R.id.edtTxtEmail);

        Intent home = getIntent();

        imageView = (ImageView) findViewById(R.id.male_avatar);

        updatebutton = (Button) findViewById(R.id.btnUpdate);
        cancelbutton = (Button) findViewById(R.id.btnCancel);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        miner_username = sharedpreferences.getString("miner_username", null);

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
                                Glide.with(UpdateMinerEmail.this).load(imgUrl).into(imageView);
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
            validateEmail();


            String _email = email.getText().toString().trim();

            CollectionReference usersRef = fstore.collection("Miners");
            Query query = usersRef.whereEqualTo("email", _email);

            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        String user= documentSnapshot.getString("email");
                        if(user.equals(_email)){
                            email.setError("user already exists");
                        }
                    }
                }
                if(task.getResult().size() == 0 ){
                    Log.d("Message", "User not Exists");
                    if (validateEmail()) {
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("email", _email);

                        fstore.collection("Miners")
                                .document(documentId)
                                .update(userInfo)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("TAG", "Updated");
                                    Toast.makeText(UpdateMinerEmail.this, "Updated", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MinerPersonalInformation.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(UpdateMinerEmail.this, "Failed", Toast.LENGTH_SHORT).show());
                    }

                }
            });

        });

    }
    private Boolean validateEmail() {
        String val = email.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            valid = false;
        } else if (!val.matches(emailPattern)) {
            email.setError("Invalid email address");
            valid = false;
        } else {
            email.setError(null);
            valid = true;
        }
        return valid;
    }
}
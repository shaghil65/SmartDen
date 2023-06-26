package com.sha.smartden;

import static com.sha.smartden.PasswordEncryption.getMd5;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MinerPersonalInformation extends AppCompatActivity {


    private EditText username, email, password, phone, miner_id,city,address;
    private Button cancelbutton;

//
    private Button btnChoose, btnUpload;
    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
//

    SharedPreferences sharedpreferences;
    String miner_username;
    String imgUrl;
    String documentId;
    String miner_id_for_img;

    public static final String MyPREFERENCES = "MyPrefs" ;

    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miner_personal_information);

        //Initialize Views
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        imageView = (ImageView) findViewById(R.id.male_avatar);



        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
                uploadImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

//
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fstore = FirebaseFirestore.getInstance();

        miner_id = findViewById(R.id.edtTxtMinerId);
        username = findViewById(R.id.edtTxtUsername);
        email = findViewById(R.id.edtTxtEmail);
        password = findViewById(R.id.edtTxtPassword);
        city = findViewById(R.id.edtTxtCity);
        address = findViewById(R.id.edtTxtAddress);
        phone = findViewById(R.id.edtTxtPhone);
        cancelbutton = (Button) findViewById(R.id.btnCancel);


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);





        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            miner_id_for_img = sharedpreferences.getString("miner_id_for_admin", null);
            miner_username = sharedpreferences.getString("miner_username_for_admin", null);
        } else {
            // No user is signed in
            miner_id_for_img = sharedpreferences.getString("miner_id", null);
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
                                miner_id.setText(document.getLong("Minerid").toString());
                                username.setText(document.getString("username"));
                                email.setText(document.getString("email"));
                                phone.setText(document.getString("phone"));
                                city.setText(document.getString("city"));
                                address.setText(document.getString("address"));
                                imgUrl = document.getString("img");
                                Glide.with(MinerPersonalInformation.this).load(imgUrl).into(imageView);
                            }
                        } else {
                            Log.w("failed", "Error getting documents.", task.getException());
                        }
                    }
                });



        cancelbutton.setOnClickListener(v -> {
            if (user != null) {
                startActivity(new Intent(getApplicationContext(), AdminEmployeeDetail.class));
            } else {
                // No user is signed in
                startActivity(new Intent(getApplicationContext(), LoggedInAsMiner.class));
            }
            finish();
        });
    }
//
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void uploadImage() {
        if(filePath != null)
        {
            StorageReference ref = storageReference.child(miner_id_for_img);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MinerPersonalInformation.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("img", uri.toString());
                                    fstore.collection("Miners")
                                            .document(documentId)
                                            .update(userInfo)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(MinerPersonalInformation.this, "Added to firestore", Toast.LENGTH_SHORT).show();
                                                })
                                            .addOnFailureListener(e -> Toast.makeText(MinerPersonalInformation.this, "Failed", Toast.LENGTH_SHORT).show());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MinerPersonalInformation.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(MinerPersonalInformation.this, "Please choose an image first", Toast.LENGTH_SHORT).show();
        }
    }
//

    public void get_UpdateMinerId(View v){
            Toast.makeText(MinerPersonalInformation.this, "Can't Update MinerId", Toast.LENGTH_SHORT).show();
        }
        public void get_UpdateMinerUsername(View v){
            startActivity(new Intent(getApplicationContext(), UpdateMinerUsername.class));
            Toast.makeText(MinerPersonalInformation.this, "MinerUsername", Toast.LENGTH_SHORT).show();
            finish();
        }
        public void get_UpdateMinerEmail(View v){
            startActivity(new Intent(getApplicationContext(), UpdateMinerEmail.class));
            Toast.makeText(MinerPersonalInformation.this, "MinerEmail", Toast.LENGTH_SHORT).show();
            finish();
        }
        public void get_UpdateMinerPassword(View v){
            startActivity(new Intent(getApplicationContext(), UpdateMinerPassword.class));
            Toast.makeText(MinerPersonalInformation.this, "MinerPassword", Toast.LENGTH_SHORT).show();
            finish();
        }
        public void get_UpdateMinerPhone(View v){
            startActivity(new Intent(getApplicationContext(), UpdateMinerPhone.class));
            Toast.makeText(MinerPersonalInformation.this, "MinerPhone", Toast.LENGTH_SHORT).show();
            finish();
        }
        public void get_UpdateMinerCity(View v){
            startActivity(new Intent(getApplicationContext(), UpdateMinerCity.class));
            Toast.makeText(MinerPersonalInformation.this, "MinerCity", Toast.LENGTH_SHORT).show();
            finish();
        }
        public void get_UpdateMinerAddress(View v){
            startActivity(new Intent(getApplicationContext(), UpdateMinerAddress.class));
            Toast.makeText(MinerPersonalInformation.this, "MinerAddress", Toast.LENGTH_SHORT).show();
            finish();
        }

}
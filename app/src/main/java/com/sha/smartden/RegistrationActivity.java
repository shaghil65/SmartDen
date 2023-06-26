package com.sha.smartden;

import static com.sha.smartden.PasswordEncryption.getMd5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText username, email, password, phone,city,address;
    private Button regbutton, cancelbutton;
    boolean valid;
    int i = 1;
    int b ;

    FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fstore = FirebaseFirestore.getInstance();



        createMinerUniqueId();

        username = findViewById(R.id.edtTxtUsername);
        email = findViewById(R.id.edtTxtEmail);
        password = findViewById(R.id.edtTxtPassword);
        phone = findViewById(R.id.edtTxtPhone);
        city = findViewById(R.id.edtTxtCity);
        address = findViewById(R.id.edtTxtAddress);
        regbutton = (Button) findViewById(R.id.btnRegister);
        cancelbutton = (Button) findViewById(R.id.btnCancel);



        cancelbutton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoggedInAsAdmin.class)));


        regbutton.setOnClickListener(v -> {
            validateUsername();
            validateEmail();
            validatePassword();
            validatePhoneNo();
            validateCity();
            validateAddress();


            String _username = username.getText().toString().trim();
            String _email = email.getText().toString().trim();
            String _password = password.getText().toString().trim();
            String _phone = phone.getText().toString().trim();
            String _city = city.getText().toString().trim();
            String _address = address.getText().toString().trim();
            String walletaddress = "";
            CollectionReference usersRef = fstore.collection("Miners");
            Query query = usersRef.whereEqualTo("username", _username);
            Query query2 = usersRef.whereEqualTo("email",_email);

            query.get().addOnCompleteListener(task -> query2.get().addOnCompleteListener(tas -> {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String user = documentSnapshot.getString("username");
                        if (user.equals(_username)) {
                            username.setError("user already exists");
                        }
                    }
                }
                if(tas.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : tas.getResult())
                    {
                        String useremail = documentSnapshot.getString("email");
                        if(useremail.equals(_email)){
                            email.setError("email already taken");
                        }
                    }
                }
                if(task.getResult().size() == 0 && tas.getResult().size() == 0 ){
                    if (validateUsername() && validateEmail() && validatePassword() && validatePhoneNo() && validateCity()&&validateAddress()) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(String.valueOf(b));

                        myRef.child("Fire").setValue("0");
                        myRef.child("Gas").setValue("0");
                        myRef.child("Heart").setValue("0");
                        myRef.child("Humidity").setValue("0");
                        myRef.child("Latitude").setValue("0");
                        myRef.child("Longitude").setValue("0");
                        myRef.child("BTemp").setValue("0");
                        myRef.child("Temperature").setValue("0");
                        myRef.child("Accident").setValue("0");
                        myRef.child("Steps").setValue("0");
                        myRef.child("EMessage").setValue("0");



                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("Minerid", b);
                        userInfo.put("username", _username);
                        userInfo.put("email", _email);
                        userInfo.put("password", getMd5(_password));
                        userInfo.put("phone", _phone);
                        userInfo.put("city", _city);
                        userInfo.put("address", _address);
                        userInfo.put("img", "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png");
                        userInfo.put("walletaddress", walletaddress);

                    fstore.collection("Miners")
                            .document(String.valueOf(b))
                            .set(userInfo)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("TAG", "Miner created");
                                Toast.makeText(RegistrationActivity.this, "Miner Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoggedInAsAdmin.class));
                            })
                            .addOnFailureListener(e -> Toast.makeText(RegistrationActivity.this, "Failed", Toast.LENGTH_SHORT).show());
                    }
                }
            }));

        });
    }

    private int createMinerUniqueId() {

        CollectionReference usersRef = fstore.collection("Miners");
        Query query = usersRef.whereEqualTo("Minerid", i);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    int id = Math.toIntExact(documentSnapshot.getLong("Minerid"));

                    if(id == i){
                        Log.d("TAG", "id Exists" + i);
                        i++;
                        createMinerUniqueId();
                    }
                }
            }

            if(task.getResult().size() == 0 ){
                Log.d("TAG", "id not Exists" + i);
                b = i;
                Log.d("TAG", "value of b" + b);
            }
        });
        return i;
    }

    private Boolean validateUsername() {
        String val = username.getText().toString().trim();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            valid = false;
        } else if (val.length() >= 15) {
            username.setError("Username too long");
            valid = false;
        } else if (!val.matches(noWhiteSpace)) {
            username.setError("White Spaces are not allowed");
            valid = false;
        } else {
            username.setError(null);
            valid = true;
        }
        return valid;
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

    private Boolean validatePassword() {
        String val = password.getText().toString().trim();
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
            password.setError("Field cannot be empty");
            valid = false;
        } else if (!val.matches(passwordVal)) {
            password.setError("Password is too weak");
            valid = false;
        } else {
            password.setError(null);
            valid = true;
        }
        return valid;
    }

    private Boolean validatePhoneNo() {
        String val = phone.getText().toString().trim();
        String phoneVal = "^(\\d{11})";
        if(val.length() != 11){
            phone.setError("Enter only 11 numbers");
            valid = false;
        }
        else if (!val.matches(phoneVal)) {
            phone.setError("Enter Only digits");
            valid = false;
        }
        else if (val.isEmpty()) {
            phone.setError("Field cannot be empty");
            valid = false;
        } else {
            phone.setError(null);
            valid = true;
        }
        return valid;
    }
    private Boolean validateCity() {
        String val = city.getText().toString().trim();
        if (val.isEmpty()) {
            city.setError("Field cannot be empty");
            valid = false;
        } else {
            city.setError(null);
            valid = true;
        }
        return valid;
    }
    private Boolean validateAddress() {
        String val = address.getText().toString().trim();
        if (val.isEmpty()) {
            address.setError("Field cannot be empty");
            valid = false;
        } else {
            address.setError(null);
            valid = true;
        }
        return valid;
    }
}
package com.sha.smartden;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class AllEmployees extends AppCompatActivity implements EmployeeClickListener {

    RecyclerView recyclerView;
    ArrayList<EmployeeData> employeeDataArrayList;
    EmployeeAdapter employeeAdapter;
    FirebaseFirestore fstore;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_employees);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fstore = FirebaseFirestore.getInstance();
        employeeDataArrayList = new ArrayList<EmployeeData>();
        employeeAdapter = new EmployeeAdapter(this,employeeDataArrayList,this);
        recyclerView.setAdapter(employeeAdapter);
        EventChangeListener();

    }

    private void EventChangeListener() {
        fstore.collection("Miners").orderBy("Minerid", Query.Direction.ASCENDING).addSnapshotListener(
                (value, error) -> {
                    if(error != null){
                        Log.e("FStore Error",error.getMessage());
                        return;
                    }
                    for (DocumentChange dc: value.getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED){
                            employeeDataArrayList.add(dc.getDocument().toObject(EmployeeData.class));

                        }
                        employeeAdapter.notifyDataSetChanged();
                    }
                }
        );
    }

    @Override
    public void onItemClicked(EmployeeData employeeData) {
        Toast.makeText(this, String.valueOf(employeeData.getMinerid()), Toast.LENGTH_SHORT).show();
        Intent home = new Intent(AllEmployees.this, AdminEmployeeDetail.class);
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("miner_username_for_admin", employeeData.getUsername());
        editor.putString("miner_id_for_admin", String.valueOf(employeeData.getMinerid()));
        editor.apply();
        startActivity(home);
    }
}
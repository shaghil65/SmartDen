package com.sha.smartden;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {


    String documentId;
    Context context;
    ArrayList<EmployeeData> employeeArrayList;
    EmployeeClickListener empClickListener;
    FirebaseFirestore fstore;
    FirebaseStorage storage;
    StorageReference storageReference;


    public EmployeeAdapter(Context context, ArrayList<EmployeeData> employeeArrayList,EmployeeClickListener employeeClickListener) {
        this.context = context;
        this.employeeArrayList = employeeArrayList;
        this.empClickListener = employeeClickListener;
    }

    @NonNull
    @Override
    public EmployeeAdapter.EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_employee,parent,false);

        return new EmployeeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.EmployeeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        EmployeeData emp = employeeArrayList.get(position);

        holder.e_id.setText(String.valueOf(emp.id));
        holder.e_username.setText(emp.username);
        holder.e_email.setText(emp.email);

        String imgUri = null;
        imgUri = emp.getImg();
        Glide.with(context).load(imgUri).into(holder.e_img);

        holder.cardView.setOnLongClickListener(v -> {

            fstore = FirebaseFirestore.getInstance();
            fstore.collection("Miners")
            .whereEqualTo("username", emp.username)
            .get().addOnCompleteListener(task -> {

                if (task.isSuccessful() && !task.getResult().isEmpty()){
                    DocumentSnapshot dc = task.getResult().getDocuments().get(0);
                    documentId = dc.getId();
                }
            });

    AlertDialog.Builder builder = new AlertDialog.Builder(context)
            .setTitle("Delete Miner")
            .setMessage("Are you sure you want to delete?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fstore = FirebaseFirestore.getInstance();
                    storage = FirebaseStorage.getInstance();
                    storageReference = storage.getReference();
                    fstore.collection("Miners")
                            .document(documentId)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    employeeArrayList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                    FirebaseDatabase.getInstance().getReference(String.valueOf(emp.id)).removeValue();
                                    StorageReference ref = storageReference.child(String.valueOf(emp.getId()));
                                    ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("G", "onSuccess: Storage file deleted.");
                                        }
                                    });
                                    Log.e("AG", "onSuccess: Deleted");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
//
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
    builder.show();


//                empClickListener.onItemLongClicked(employeeArrayList.get(position));
            return true;
        });
        holder.cardView.setOnClickListener(v -> {
            empClickListener.onItemClicked(employeeArrayList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return employeeArrayList.size();
    }
    public static  class  EmployeeViewHolder extends RecyclerView.ViewHolder{
        ImageView e_img;
        TextView e_id,e_username,e_email;
        public CardView cardView;
        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            e_id = itemView.findViewById(R.id.emp_id);
            e_username = itemView.findViewById(R.id.emp_username);
            e_email = itemView.findViewById(R.id.emp_email);
            e_img = itemView.findViewById(R.id.male_avatar);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }
}

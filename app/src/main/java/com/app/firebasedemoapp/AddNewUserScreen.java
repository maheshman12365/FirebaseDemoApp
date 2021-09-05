package com.app.firebasedemoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddNewUserScreen extends AppCompatActivity {
    TextInputEditText et_title;
    Switch switch_ischecked;
    MaterialButton proceed;
    View progressBar;
    FirebaseDatabase mDataBase;
    static DatabaseReference mRef;
    String MASTER_TABLE = "master_table_users";
    String DB_URL = "https://fir-authdemoapp-ae2ed-default-rtdb.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user_screen);
        mDataBase = FirebaseDatabase.getInstance(DB_URL);
        mRef = mDataBase.getReference(MASTER_TABLE);
        et_title = findViewById(R.id.et_title);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        switch_ischecked = findViewById(R.id.switch_ischecked);
        proceed = findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_title.getText().toString().trim().length() == 0) {
                    Toast.makeText(AddNewUserScreen.this, "Please enter Title", Toast.LENGTH_SHORT).show();
                    return;
                }
                addNewUser(et_title.getText().toString().trim(), switch_ischecked.isChecked());

            }
        });
    }

    private void addNewUser(String title, boolean checked) {
        progressBar.setVisibility(View.VISIBLE);
        Query lastQuery = mRef.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                String message = dataSnapshot.child("title").getValue().toString();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String lastItemId = child.getKey();
                    int newItemId = Integer.parseInt(lastItemId) + 1;
                    User user = new User(newItemId, title, checked);
                    mRef.child(String.valueOf(newItemId)).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddNewUserScreen.this, "Added to DB", Toast.LENGTH_SHORT)
                                        .show();
                                startActivity(new Intent(AddNewUserScreen.this, RegisterationScreen.class));
                                finish();
                            } else {
                                Toast.makeText(AddNewUserScreen.this, "Failed to add (" + task.getException() + ")", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);

                        }
                    });
//                    Log.d("addNewUserUsernewItemId", String.valueOf(newItemId));
//                    Log.d("addNewUserUser val", child.child("title").getValue().toString());
                }
//                Log.e("addNewUser",message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
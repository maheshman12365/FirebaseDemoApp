package com.app.firebasedemoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {
    FirebaseDatabase mDataBase;
    static DatabaseReference mRef;
    String MASTER_TABLE = "master_table_users";
    String DB_URL = "https://fir-authdemoapp-ae2ed-default-rtdb.firebaseio.com/";
    View view_creating_db;
    TextView tv_database;
    ArrayList<User> allUsersList = new ArrayList<>();
    RecyclerView recycler;
    SearchView search_view;
    View view_data;
    View button;
    Boolean searchOpen = false;
    static Activity activity;
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        activity = this;
        view_data = findViewById(R.id.view_data);
        view_data.setVisibility(View.GONE);
        search_view = findViewById(R.id.search_view);
        recycler = findViewById(R.id.recycler);
        view_creating_db = findViewById(R.id.view_creating_db);
        mDataBase = FirebaseDatabase.getInstance(DB_URL);
        mRef = mDataBase.getReference(MASTER_TABLE);
        populateDataForFirstTime();
        findViewById(R.id.view_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, ManageProfileScreen.class));
            }
        });
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, AddNewUserScreen.class));
            }
        });
        tv_database = findViewById(R.id.tv_database);
        tv_database.setText("Please wait");
        view_creating_db.setVisibility(View.VISIBLE);
    }

    public static void updateValueToDB(User user) {
        mRef.child(String.valueOf(user.getUid())).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(activity, "Updated in DB", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Failed to update in DB", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
//        handleSearch(searchQuery);
    }

    private void populateDataForFirstTime() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //create new user
                    tv_database.setText("Creating database");
                    Log.e("populateData", "creating"); //Don't ignore errors!
                    mRef.setValue(MASTER_TABLE);
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e("populateData", "count - " + dataSnapshot.getChildrenCount());
                            if (dataSnapshot.getChildrenCount() == 0) {
                                Log.e("populateData", "insert 500 rows here - ");
                                for (int i = 0; i < 5; i++) {
                                    User user = new User(i, "UserName" + i, false);
                                    mRef.child(String.valueOf(i)).setValue(user);
                                }
                                tv_database.setText("Database created");
                                view_creating_db.setVisibility(View.GONE);
                                Toast.makeText(HomeScreen.this, "Database created", Toast.LENGTH_SHORT).show();
                                populateRecyclerView();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view_creating_db.setVisibility(View.GONE);
                        }
                    });
                } else {
                    populateRecyclerView();
                    view_creating_db.setVisibility(View.GONE);
                    Log.e("populateData", "exists"); //Don't ignore errors!

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("populateData", databaseError.getMessage()); //Don't ignore errors!
                view_creating_db.setVisibility(View.GONE);
            }
        });

    }

    private void populateRecyclerView() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allUsersList = new ArrayList<>();
                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                    allUsersList.add(userSnapShot.getValue(User.class));
                }
                setupRecycler(allUsersList);
                search_view.setVisibility(View.VISIBLE);
                search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        handleSearch(query.toLowerCase().trim());
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        handleSearch(newText.toLowerCase().trim());
                        return false;
                    }
                });
                Log.e("populateRecyclerView", "Size - " + allUsersList.size()); //Don't ignore errors!

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("populateData", databaseError.getMessage()); //Don't ignore errors!
                view_creating_db.setVisibility(View.GONE);
                search_view.setVisibility(View.GONE);

            }
        });

    }

    private void setupRecycler(ArrayList<User> allUsersList) {
        view_data.setVisibility(View.VISIBLE);
        UserAdapter userAdapter = new UserAdapter(allUsersList, HomeScreen.this);
        recycler.setAdapter(userAdapter);
    }

    String searchQuery = "";
    private void handleSearch(String query) {
        searchQuery = query;
        if (query.length() == 0) {
            setupRecycler(allUsersList);
        } else {
            searchOpen = true;
            ArrayList<User> searchResultsList = new ArrayList<>();
            for (User user : allUsersList) {
                if (user.getTitle().toLowerCase().trim().contains(query)) {
                    searchResultsList.add(user);
                }
            }
            if (searchResultsList.size() > 0) {
                setupRecycler(searchResultsList);
            } else {
                setupRecycler(new ArrayList<>());
//                Toast.makeText(this, "No matches found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!searchOpen) {
            if (pressedTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                finishAffinity();
            } else {
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
            pressedTime = System.currentTimeMillis();

        } else if (!search_view.isIconified()) {
            search_view.setIconified(true);
            searchOpen = false;
        }
    }
}
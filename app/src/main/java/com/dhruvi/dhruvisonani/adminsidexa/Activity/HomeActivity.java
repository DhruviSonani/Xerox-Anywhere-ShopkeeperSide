package com.dhruvi.dhruvisonani.adminsidexa.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.dhruvi.dhruvisonani.adminsidexa.Activity.Activity.ChatActivity;
import com.dhruvi.dhruvisonani.adminsidexa.Activity.Activity.LoginSignUpActivity;
import com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment.AddBookFragment;
import com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment.BookQueueFragment;
import com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment.EditProfileFragment;
import com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment.PendingFragment;
import com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment.RecordFragment;
import com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment.RequestsFragment;
import com.dhruvi.dhruvisonani.adminsidexa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static String MyPREFERENCES = "dhruvi";
    FirebaseAuth auth;
    FirebaseFirestore db;
    private AppBarConfiguration mAppBarConfiguration;
    private static final int STORAGE_PERMISSION_CODE = 1;
    DocumentReference databaseReference;
    private static String[] PERMISSION_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    TextView nav_user, nav_mobileNumber;
    String str_name;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        ActionBar a = getSupportActionBar();
        a.setTitle("Requests");
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo datac = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null & datac != null) && (wifi.isConnected() | datac.isConnected())) {
            drawer = findViewById(R.id.drawer_layout);
            SharedPreferences sharedpreferences = HomeActivity.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            str_name = sharedpreferences.getString("uname", "");

            db = FirebaseFirestore.getInstance();
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View hView = navigationView.getHeaderView(0);
            nav_user = hView.findViewById(R.id.nav_header_shopName);
            nav_mobileNumber = hView.findViewById(R.id.nav_header_emailId);

            list();
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RequestsFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_requests);
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("No internet Connection");
            builder.setCancelable(false);
            builder.setMessage("This app requires Internet Connection so.. \n 1. Close this app\n 2. Turn On Internet \n 3. Use efficiently");
            builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_message:
//                Toast.makeText(this, "chat", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(HomeActivity.this, ChatActivity.class));
//                return true;
            case R.id.action_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditProfileFragment()).commit();
                getSupportActionBar().setTitle("Setting");
                return true;

            case R.id.action_logout:
                auth.getInstance().signOut();
                finishAffinity();
                startActivity(new Intent(HomeActivity.this, LoginSignUpActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        Fragment fragment;

        switch (item.getItemId()) {
            case R.id.nav_addBook:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddBookFragment()).commit();
                getSupportActionBar().setTitle("Add Book");
                break;
            case R.id.nav_pending:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PendingFragment()).commit();
                getSupportActionBar().setTitle("Pending List");
                break;
            case R.id.nav_record:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecordFragment()).commit();
                getSupportActionBar().setTitle("Record");
                break;
            case R.id.nav_bookList:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BookQueueFragment()).commit();
                getSupportActionBar().setTitle("Book Buy Record");
                break;
            case R.id.nav_requests:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RequestsFragment()).commit();
                getSupportActionBar().setTitle("Requests");
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void list() {
        final String[] num = new String[1];
        FirebaseFirestore.getInstance().collection("Shopkeeper").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }


                    for (String value : list) {
                        databaseReference = db.collection("Shopkeeper").document(value);
                        databaseReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    if (str_name.equals(snapshot.getString("Mobile Number"))) {
                                        nav_mobileNumber.setText(str_name);
                                        nav_user.setText(snapshot.getString("Full Name"));

                                    }
                                }

                            }
                        });

                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();

                }
            }

        });

    }


}


// Passing each menu ID as a set of Ids because each
// menu should be considered as top level destinations.
        /*mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_requests,R.id.nav_pending, R.id.nav_record, R.id.nav_addBook).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavControl



        ler(navigationView, navController);*/
//
//        if(savedInstanceState == null){
//        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new RequestsFragment()).commit();
//        navigationView.setCheckedItem(R.id.nav_requests);}


// permission
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 1:
//
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //resume tasks needing this permission
//                    Boolean a = isReadStoragePermissionGranted();
//                }
//                break;
//
//        }
//    }
//
//        public  boolean isReadStoragePermissionGranted() {
//            if (Build.VERSION.SDK_INT >= 23) {
//                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                        == PackageManager.PERMISSION_GRANTED) {
//
//                    return true;
//                } else {
//
//                    Toast.makeText(this, "Permission is revoked1", Toast.LENGTH_SHORT).show();
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
//                    return false;
//                }
//            }
//            else { //permission is automatically granted on sdk<23 upon installation
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//
//                return true;
//            }
//        }
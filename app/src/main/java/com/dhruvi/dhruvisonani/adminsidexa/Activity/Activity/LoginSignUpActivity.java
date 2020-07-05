package com.dhruvi.dhruvisonani.adminsidexa.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment.LoginFragment;
import com.dhruvi.dhruvisonani.adminsidexa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LoginSignUpActivity extends AppCompatActivity {
    FirebaseAuth  firebaseAuth;

    public static List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R. layout.activity_login_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore.getInstance().collection("Shopkeeper").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
//                    Toast.makeText(LoginSignUpActivity.this, list.toString(), Toast.LENGTH_SHORT).show();
//                    for (String value : list) {
//                        databaseReference = FirebaseFirestore.getInstance().collection("Shopkeeper").document(value);
//                        databaseReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                            @Override
//                            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
//                                if (e != null) {
//                                    return;
//                                }
//                                if (snapshot != null && snapshot.exists()) {
//                                    if (ShopkeeperNumber.equals(snapshot.getString("Mobile Number"))) {
//
//                                        Toast.makeText(getActivity(), snapshot.getString("Full Name"), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        });
//                    }
                } else {
                    Toast.makeText(LoginSignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_login_signUp,new LoginFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

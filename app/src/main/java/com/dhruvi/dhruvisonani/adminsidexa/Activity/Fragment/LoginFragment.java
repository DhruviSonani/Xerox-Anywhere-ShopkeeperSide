package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dhruvi.dhruvisonani.adminsidexa.Activity.HomeActivity;
import com.dhruvi.dhruvisonani.adminsidexa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.dhruvi.dhruvisonani.adminsidexa.Activity.HomeActivity.MyPREFERENCES;

public class LoginFragment extends Fragment implements View.OnClickListener {
    EditText et_userName, et_verificationCode, et_password;
    Button btn_login, btn_sendCode;
    TextView tv_doSignUp;
    String UserId;

    public FirebaseAuth auth;
    String codeSent;
    Fragment fragment;
    DocumentReference databaseReference;

    int k = 0;
    private String ver_id, str_password;

    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        declaration(view);
        initialization();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void declaration(View v) {

        auth = FirebaseAuth.getInstance();
        btn_login = v.findViewById(R.id.btn_login);
        tv_doSignUp = v.findViewById(R.id.tv_doSignUp);
        btn_sendCode = v.findViewById(R.id.btn_sendCode);
        et_password = v.findViewById(R.id.et_password);
        et_userName = v.findViewById(R.id.et_userName);
        //progressDialog = new ProgressDialog(getActivity());
        et_verificationCode = v.findViewById(R.id.et_verificationCode);

        if (auth.getInstance().getCurrentUser() != null) {
            UserId = auth.getCurrentUser().getUid();
            Log.e(">>>>>>>>>>>>>>>>", UserId);
            Intent i = new Intent(getActivity(), HomeActivity.class);
            startActivity(i);
        }
    }

    public void initialization() {
        btn_login.setOnClickListener(this);
        tv_doSignUp.setOnClickListener(this);
        btn_sendCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_doSignUp:
                fragment = new SignUpFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_login_signUp, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                Toast.makeText(getActivity(), "Go to signUp", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_login:
                String verify = et_verificationCode.getText().toString();
//                list();

                if (TextUtils.isEmpty(et_userName.getText().toString())) {
                    et_userName.setError("Enter Registered Mobile Number");
                    et_userName.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(et_password.getText().toString())) {
                    et_password.setError("Enter Password");
                    et_password.requestFocus();
                    return;
                }
                if (et_password.getText().toString().equals(str_password)) {
                    verifyCode(verify);
                } else {
                    et_password.setError("Enter Valid Password");
                    et_password.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(verify)) {
                    et_verificationCode.setError("Enter Verifification Code");
                    et_verificationCode.requestFocus();
                    return;
                } else if (verify.length() < 6) {
                    et_verificationCode.setError("Enter Correct Code");
                    et_verificationCode.requestFocus();
                    return;
                }
                break;
            case R.id.btn_sendCode:
                if (et_userName.length() == 10) {
                    checkUser();
//                    if(k==0){
//
//                    }
                } else {
                    if (TextUtils.isEmpty(et_userName.getText().toString())) {
                        et_userName.setError("Mobile Number can not be empty.");
                        et_userName.requestFocus();
                        return;
                    } else if (et_userName.getText().toString().length() != 10) {
                        et_userName.setError("Mobile Number is not valid.");
                        et_userName.requestFocus();
                        return;
                    }
                }
                break;
        }
    }

    private void checkUser() {
        FirebaseFirestore.getInstance().collection("Shopkeeper").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
//                    Toast.makeText(getActivity(), list.toString(), Toast.LENGTH_SHORT).show();
                    for (String value : list) {
                        databaseReference = FirebaseFirestore.getInstance().collection("Shopkeeper").document(value);
                        databaseReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    if (et_userName.getText().toString().equals(snapshot.getString("Mobile Number"))) {
                                        str_password = snapshot.getString("Password");
                                        if (et_password.getText().toString().equals(str_password)) {
                                            k = 1;
                                            Toast.makeText(getActivity(), snapshot.getString("Full Name") + ", we are sending you otp on " + et_userName.getText().toString(), Toast.LENGTH_SHORT).show();
                                            sendVerificationCode();
                                        } else {
                                            et_password.setError("Incorrect Password");
                                            et_password.requestFocus();
                                            return;
                                        }
                                    }

                                }

                            }
                        });

                    }
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();

                }
            }

        });


//        if(k!=1){
//                Toast.makeText(getActivity(), "Please do register your self by meeting to authority", Toast.LENGTH_SHORT).show();
//                return;
//
//        }

    }


    private void verifyCode(String code) {
//        try{520782
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(ver_id, code);
            signInWithCredential(credential);
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Code Received", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("uname", et_userName.getText().toString());
                    editor.putString("password", et_password.getText().toString());
                    editor.apply();
                    editor.commit();

                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
//                    Toast.makeText(getActivity(), "Login", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getActivity(), "Successful Login", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerificationCode() {
        try {
            String num = et_userName.getText().toString();
            num = "+91" + num;
            PhoneAuthProvider.getInstance().verifyPhoneNumber(num, 120, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);//583473

//            Toast.makeText(getActivity(), "Wait Untill You Receive Code !!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            ver_id = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
//                et_verificationCode.setText(code);
                verifyCode(code);
            }
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


}


//    private void list() {
//        final String ShopkeeperNumber = "9913106087";
//
//        FirebaseFirestore.getInstance().collection("Shopkeeper").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    List<String> list = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        list.add(document.getId());
//                    }
//                    Toast.makeText(getActivity(), list.toString(), Toast.LENGTH_SHORT).show();
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
//                } else {
//                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        });
//    }


//    private void CheckNumberAvailable() {
//
//        for (String value : LoginSignUpActivity.list) {
//            databaseReference = FirebaseFirestore.getInstance().collection("Shopkeeper").document(value);
//            databaseReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
//                    if (e != null) {
//                        return;
//                    }
//                    if (snapshot != null && snapshot.exists()) {
//                        if (et_userName.getText().toString().equals(snapshot.getString("Mobile Number"))) {
//                            Toast.makeText(getActivity(), "dsdvs", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getActivity(), snapshot.getString("Full Name"), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            });
//        }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////        FirebaseFirestore.getInstance().collection("Shopkeeper").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                if (task.isSuccessful()) {
////                    List<String> list = new ArrayList<>();
////                    for (QueryDocumentSnapshot document : task.getResult()) {
////                        list.add(document.getId());
////                    }
////                    for (String value : list) {
////                        databaseReference = FirebaseFirestore.getInstance().collection("Shopkeeper").document(value);
////                        databaseReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
////                            @Override
////                            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
////                                if (e != null) {
////                                    return;
////                                }
////                                if (snapshot != null && snapshot.exists()) {
////                                    if (et_userName.getText().toString().equals(snapshot.getString("Mobile Number"))) {
////                                        Toast.makeText(getActivity(), "fddfdr", Toast.LENGTH_SHORT).show();
////                                        sendVerificationCode();
////
////                                    }
////                                }
////
////                            }
////                        });
////
////                    }
////                } else {
////                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
////
////                }
////            }
////
////        });
//
//    }

//
//    private void verifySignInCode(){
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent,et_verificationCode.getText().toString());
//        signInWithPhoneAuthCredential(credential);
//    }

//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                           // FirebaseUser user = task.getResult().getUser();
//                            startActivity(new Intent(getActivity(), ShopInformationBottomActivity.class));
//                            Toast.makeText(getActivity(), "Login SuccessFul ", Toast.LENGTH_SHORT).show();
//                        }
//                        else{
//                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//    }
//    private void sendVerificationCode(){
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//
//                et_userName.getText().toString(),
//                60,
//                TimeUnit.SECONDS,
//                getActivity(),
//                callbacks);
//    }
//
//    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//
//        }
//
//        @Override
//        public void onVerificationFailed(@NonNull FirebaseException e) {
//
//        }
//
//        @Override
//        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//            codeSent = s;
//        }
//    };
//}

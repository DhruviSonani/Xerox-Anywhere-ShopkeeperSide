package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dhruvi.dhruvisonani.adminsidexa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

public class SignUpFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    EditText et_C_password, et_password, et_userName, et_fullName, et_EmailID, et_aadhar, et_otp, et_address, et_licence, et_colorprintPrice, et_ColorxeroxPrice, et_shopName;
    EditText et_BW_printPrice, et_A1, et_A2, et_A3, et_A4, et_BWxeroxPrice, et_sadleStiching, et_sectionSewn, et_CasedInWiro, et_Pamphlet, et_Coptic, et_Japanese;
    EditText et_smallSpiral, et_MeduimSpiral, et_LargeSpiral, et_hard;
    TextView tv_terms;
    AutoCompleteTextView ATv_EndTime, ATv_EndDay, ATv_startDay,ATv_startTime;
    Button btn_signUp, btn_SendSignUpCode;
    ProgressDialog progressDialog;
    CheckBox cb_signup;
    ImageView iv_licence, iv_aadhar, iv_example;
    RadioGroup radioGroup;
    RadioButton rb_fm;
    private Uri mImageUri, mAadharUri;

    LinearLayout et_shopTime;
    private String ver_id;
    private static final int PICK_IMAGE_REQUEST = 1;
    public static String str_name, str_emailId, str_pwd, str_cpwd, str_gender, str_mobileNum;

    private static final String[] Day = new String[]{"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    private static final String[] Time = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};//,"13","14","15","16","17","18","19","20","21","22","23","24"};
    String UserId;
    private DatabaseReference databaseReference;
    FirebaseFirestore fstore; // storing user data in database
    FirebaseAuth firebaseAuth;
    private StorageTask uploadTask;
    StorageReference StorageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
//        list();

        declaration(view);
        initialization(view);
        return view;

    }

    private void list() {
        FirebaseFirestore.getInstance().collection("Shopkeeper").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    Toast.makeText(getActivity(), list.toString(), Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void declaration(View v) {


        ATv_EndTime = v.findViewById(R.id.ATv_EndTime);
        ATv_EndDay = v.findViewById(R.id.ATv_EndDay);
        ATv_startDay = v.findViewById(R.id.ATv_startDay);
        ATv_startTime = v.findViewById(R.id.ATv_startTime);
        et_shopTime = v.findViewById(R.id.et_shopTime);
        et_BW_printPrice = v.findViewById(R.id.et_BW_printPrice);
        et_A1 = v.findViewById(R.id.et_A1);
        et_A2 = v.findViewById(R.id.et_A2);
        et_A3 = v.findViewById(R.id.et_A3);
        et_A4 = v.findViewById(R.id.et_A4);
        et_hard = v.findViewById(R.id.et_hard);
        et_BWxeroxPrice = v.findViewById(R.id.et_BWxeroxPrice);
        et_sadleStiching = v.findViewById(R.id.et_sadleStiching);
        et_sectionSewn = v.findViewById(R.id.et_sectionSewn);
        et_CasedInWiro = v.findViewById(R.id.et_CasedInWiro);
        et_Pamphlet = v.findViewById(R.id.et_Pamphlet);
        et_Coptic = v.findViewById(R.id.et_Coptic);
        et_Japanese = v.findViewById(R.id.et_Japanese);
        et_smallSpiral = v.findViewById(R.id.et_smallSpiral);
        et_MeduimSpiral = v.findViewById(R.id.et_MeduimSpiral);
        et_LargeSpiral = v.findViewById(R.id.et_LargeSpiral);

        et_colorprintPrice = v.findViewById(R.id.et_colorprintPrice);
        et_ColorxeroxPrice = v.findViewById(R.id.et_ColorxeroxPrice);
        et_shopName = v.findViewById(R.id.et_shopName);
        iv_licence = v.findViewById(R.id.iv_licence);
        btn_SendSignUpCode = v.findViewById(R.id.btn_SendSignUpCode);
        et_licence = v.findViewById(R.id.et_licence);
        et_address = v.findViewById(R.id.et_address);
        et_C_password = v.findViewById(R.id.et_C_password);
        et_EmailID = v.findViewById(R.id.et_EmailID);
        et_password = v.findViewById(R.id.et_password);
        et_fullName = v.findViewById(R.id.et_fullName);
        progressDialog = new ProgressDialog(getActivity());
        et_userName = v.findViewById(R.id.et_userName);
        btn_signUp = v.findViewById(R.id.btn_signUp);
        radioGroup = v.findViewById(R.id.radiogroup);
        et_aadhar = v.findViewById(R.id.et_aadhar);
        et_otp = v.findViewById(R.id.et_otp);
        tv_terms = v.findViewById(R.id.tv_terms);
        iv_aadhar = v.findViewById(R.id.iv_aadhar);
        iv_example = v.findViewById(R.id.iv_example);
        cb_signup = v.findViewById(R.id.cb_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        StorageRef = FirebaseStorage.getInstance().getReference().child("Shopkeeper_Record");

//        if (firebaseAuth.getCurrentUser() != null) {
//            Intent i = new Intent(getActivity(), HomeActivity.class);
//            startActivity(i);
//        }
    }

    public void initialization(View view) {
        tv_terms.setText("1. You have shared your valid information with us.\n2. You are knotted to service customer with faith.");
        radioGroup.setOnCheckedChangeListener(this);
        btn_signUp.setOnClickListener(this);
        iv_licence.setOnClickListener(this);
        iv_aadhar.setOnClickListener(this);
        iv_example.setOnClickListener(this);
        btn_SendSignUpCode.setOnClickListener(this);
        ArrayAdapter<String> adapterDay = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, Day);
        ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, Time);

        ATv_startTime.setAdapter(adapterTime);
        ATv_startTime.setAdapter(adapterTime);
        ATv_EndDay.setAdapter(adapterDay);
        ATv_startDay.setAdapter(adapterDay);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_example:
                Fragment fragment = new ViewLicenceFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_login_signUp, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
            case R.id.btn_SendSignUpCode:
                if (et_userName.length() == 10) {

                    sendVerificationCode();
                } else {
                    et_userName.setText("Invalid Mobile Number");
                    et_userName.findFocus();
                    return;
                }
                break;
            case R.id.iv_licence:
                Intent getintent = new Intent(Intent.ACTION_GET_CONTENT);
                getintent.setType("image/*");
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                Intent chooserIntent = Intent.createChooser(getintent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                startActivityForResult(chooserIntent, 1);
//                openFileChooser();
                break;

            case R.id.iv_aadhar:
                Intent getAadhar = new Intent(Intent.ACTION_GET_CONTENT);
                getAadhar.setType("image/*");
                Intent AadharIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                AadharIntent.setType("image/*");
                Intent chooserAadharIntent = Intent.createChooser(AadharIntent, "Select Image");
                chooserAadharIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{AadharIntent});
                startActivityForResult(chooserAadharIntent, 2);
                break;

            case R.id.btn_signUp:
                String verify = et_otp.getText().toString();
//                iv_licence.getDrawable() == null || iv_aadhar.getDrawable() == null)
                if (TextUtils.isEmpty(et_fullName.getText().toString())) {
                    et_fullName.setError("Enter Full Name");
                    et_fullName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(et_EmailID.getText().toString())) {
                    et_EmailID.setError("Enter Email Id");
                    et_EmailID.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(et_EmailID.getText().toString()).matches()) {
                    et_EmailID.setError("Enter Valid Email Id");
                    et_EmailID.requestFocus();
                    return;
                }
                if (et_userName.getText().toString().length() != 10) {
                    et_userName.setError("Enter Valid Mobile Number");
                    et_userName.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(str_gender)) {
                    Toast.makeText(getActivity(), "Select Gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(et_password.getText().toString())) {
                    et_password.setError("Enter Password");
                    et_password.requestFocus();
                    return;
                }
                if (et_password.getText().toString().length() < 6) {
                    et_password.setError("Password must be length of 6 or more");
                    et_password.requestFocus();
                    return;
                }
                if (!et_C_password.getText().toString().equals(et_password.getText().toString())) {
                    et_C_password.setError("Enter same password");
                    et_C_password.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(et_shopName.getText().toString())) {
                    et_shopName.setError("Enter Shop Name");
                    et_shopName.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(ATv_startTime.getText().toString()) && TextUtils.isEmpty(ATv_EndTime.getText().toString()) && TextUtils.isEmpty(ATv_startDay.getText().toString()) && TextUtils.isEmpty(ATv_EndDay.getText().toString())) {
                    ATv_EndDay.setError("Enter Shop Time");
                    ATv_EndDay.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(et_aadhar.getText().toString())) {
                    et_aadhar.setError("Enter Aadhar Number");
                    et_aadhar.requestFocus();
                    return;
                }
                if (et_aadhar.getText().toString().length() != 12) {
                    et_aadhar.setError("Please Enter Valid Aadhar Number");
                    et_aadhar.requestFocus();
                    return;
                }
                if (iv_aadhar.getDrawable() == null) {
                    iv_aadhar.setFocusable(true);
                    Toast.makeText(getActivity(), "sd", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(et_address.getText().toString())) {
                    et_address.setError("Enter Address");
                    et_address.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(et_licence.getText().toString())) {
                    et_licence.setError("Enter Licence Number");
                    et_licence.requestFocus();
                    return;
                }
                if (!"".equals(et_licence.getText().toString())) {

                    int l = CheckLicenceNumber();
                    if (l == 0) {
                        return;
                    }
                }

                if (iv_licence.getDrawable() == null) {
                    iv_licence.setFocusable(true);
                    Toast.makeText(getActivity(), "licence image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(et_colorprintPrice.getText().toString())) {
                    et_colorprintPrice.setError("Enter Color print Price");
                    et_colorprintPrice.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(et_BW_printPrice.getText().toString())) {
                    et_BW_printPrice.setError("Enter BW print Price");
                    et_BW_printPrice.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(et_ColorxeroxPrice.getText().toString())) {
                    et_ColorxeroxPrice.setError("Enter Color Xerox Price");
                    et_ColorxeroxPrice.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(et_BWxeroxPrice.getText().toString())) {
                    et_BWxeroxPrice.setError("Enter BW Xerox Price");
                    et_BWxeroxPrice.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(et_smallSpiral.getText().toString())) {
                    et_smallSpiral.setError("Enter Small Spiral Price");
                    et_smallSpiral.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(et_LargeSpiral.getText().toString())) {
                    et_LargeSpiral.setError("Enter Large Spiral Price");
                    et_LargeSpiral.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(et_MeduimSpiral.getText().toString())) {
                    et_MeduimSpiral.setError("Enter Medium Spiral Price");
                    et_MeduimSpiral.requestFocus();
                    return;
                }

                if (verify.length() < 6) {
                    et_otp.setError("Enter Correct Code");
                    et_otp.requestFocus();
                    return;
                }

                if (cb_signup.isChecked()) {
                    verifyCode(verify);
//                     add_user();

                } else {
                    Toast.makeText(getActivity(), "Please Read & Agree User Policy First", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
        }
    }

    private int CheckLicenceNumber() {
        int ln = 0;
        List<String> s = Arrays.asList(et_licence.getText().toString().split("/"));
        if ((s.get(0).length() == 2) && (s.get(0).matches("^[A-Z]*$")) && (s.get(0).equals("NZ") || s.get(0).equals("SZ") || s.get(0).equals("WZ") || s.get(0).equals("EZ"))) {
            if ((s.get(1).length() == 1) && (s.get(1).matches("^[A-Z]*$")) && s.get(1).equals("D")) {
                if ((s.get(2).length() > 0) && (s.get(2).matches("^[A-Z]*$"))) {
                    if (s.get(3).length() == 6 && (TextUtils.isDigitsOnly(s.get(3)))) {
                        ln = 1;
                    } else {
                        et_licence.setError("Licence Number Should be length of 6");
                        et_licence.requestFocus();
                        ln = 0;
                        return ln;
                    }
                } else {
                    et_licence.setError("Enter correct Area");
                    et_licence.requestFocus();

                    ln = 0;
                    return ln;
                }
            } else {
                et_licence.setError("Incorrect Authority Grade");
                et_licence.requestFocus();
                ln = 0;
                return ln;
            }
        } else {
            et_licence.setError("Incorrect Zone");
            et_licence.requestFocus();

            ln = 0;
            return ln;


        }
        return ln;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_male:
                str_gender = "Male";
//                Toast.makeText(getActivity(), "Male", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rb_female:
                str_gender = "Female";
//                Toast.makeText(getActivity(), "Female", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    add_user();
//                    Toast.makeText(getActivity(), "Code Received", Toast.LENGTH_SHORT).show();
//                            SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedpreferences.edit();
//                            editor.putString("uname", et_userName.getText().toString());
//                            editor.putString("password", et_password.getText().toString());
//                            editor.putString("currentuser",FirebaseAuth.getInstance().getCurrentUser().getUid());
//                            editor.apply();
//                            editor.commit();
//


//                    Intent intent = new Intent(getActivity(), HomeActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), String.valueOf(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void add_user() {

        str_name = et_fullName.getText().toString().trim();
        str_emailId = et_EmailID.getText().toString();
        str_pwd = et_password.getText().toString().trim();
        str_cpwd = et_C_password.getText().toString().trim();
        str_mobileNum = et_userName.getText().toString().trim();

        final StorageReference ref = StorageRef.child(str_mobileNum + "_" + et_shopName.getText().toString()).child("Shop_validation.jpg");
        ref.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
        final StorageReference ref1 = StorageRef.child(str_mobileNum + "_" + et_shopName.getText().toString()).child("aadhar.jpg");
        ref1.putFile(mAadharUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });

        firebaseAuth.createUserWithEmailAndPassword(str_emailId, str_pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Wait a Second ", Toast.LENGTH_SHORT).show();
                    UserId = et_shopName.getText().toString();
                    DocumentReference documentReference = fstore.collection("Shopkeeper").document(UserId);
                    Map<String, Object> User = new HashMap<>();
                    User.put("Full Name", str_name);
                    User.put("et_hard", et_hard.getText().toString());
                    User.put("Email Id", str_emailId);
                    User.put("Mobile Number", str_mobileNum);
                    User.put("Gender", str_gender);
                    User.put("Password", str_pwd);
                    User.put("Shop Name", et_shopName.getText().toString());
                    User.put("Address", et_address.getText().toString());
                    User.put("Licence Number", et_licence.getText().toString());
                    User.put("Color Print Price", et_colorprintPrice.getText().toString());
                    User.put("Color Xerox Price", et_ColorxeroxPrice.getText().toString());
                    User.put("BW Print Price", et_BW_printPrice.getText().toString());
                    User.put("et_BWxeroxPrice", et_BWxeroxPrice.getText().toString());
                    User.put("et_A1", et_A1.getText().toString());
                    User.put("et_A2", et_A2.getText().toString());
                    User.put("et_A3", et_A3.getText().toString());
                    User.put("et_A4", et_A4.getText().toString());
                    User.put("Shop Time", ATv_startTime.getText().toString()+" AM "+" to "+ATv_EndTime.getText().toString()+" PM on"+ATv_startDay.getText().toString()+" to "+ATv_EndDay.getText().toString());
                    User.put("et_sadleStiching", et_sadleStiching.getText().toString());
                    User.put("et_sectionSewn", et_sectionSewn.getText().toString());
                    User.put("et_CasedInWiro", et_CasedInWiro.getText().toString());
                    User.put("et_Japanese", et_Japanese.getText().toString());
                    User.put("et_Pamphlet", et_Pamphlet.getText().toString());
                    User.put("et_Coptic", et_Coptic.getText().toString());
                    User.put("et_smallSpiral", et_smallSpiral.getText().toString());
                    User.put("et_MeduimSpiral", et_MeduimSpiral.getText().toString());
                    User.put("et_LargeSpiral", et_LargeSpiral.getText().toString());

                    documentReference.set(User).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Signed Up Successfully", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame_login_signUp, new LoginFragment());
                            fragmentTransaction.commit();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Error !! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(getActivity()).load(mImageUri).into(iv_licence);

        }
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mAadharUri = data.getData();
            Picasso.with(getActivity()).load(mAadharUri).into(iv_aadhar);
        }
    }

    private void verifyCode(String code) {
//        try{520782
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(ver_id, code);
            signInWithCredential(credential);
//            add_user();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void sendVerificationCode() {
        try {
            String num = et_userName.getText().toString();
            num = "+91" + num;
            PhoneAuthProvider.getInstance().verifyPhoneNumber(num, 120, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);//583473
            Toast.makeText(getActivity(), "Wait Untill You Receive 6 digit Code !!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            ver_id = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
//                et_verificationCode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };


}


//                    firebaseAuth.createUserWithEmailAndPassword(str_emailId,str_pwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                //startActivity(new Intent(getActivity(), ShopInformationBottomActivity.class));
//                                //Toast.makeText(getActivity(), "SignUp Successful", Toast.LENGTH_SHORT).show();
//
//                                //FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                                SignUpEntity info = new SignUpEntity(str_name, str_emailId, str_gender, str_pwd);
//
//                                FirebaseDatabase.getInstance().getReference("SignUpEntity")
//                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(info)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Toast.makeText(getActivity(), "Successfull", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                });
//                                //updateUI(user);
//                            } else {
//                                Toast.makeText(getActivity(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });


//public class SignUpFragment<DatabaseReference, FirebaseDatabse> extends Fragment implements View.OnClickListener {
//    EditText et_C_password,et_password,et_userName,et_fullName,et_addMin,et_EmailID;
//    TextView tv_doLogIn;
//    Button btn_signUp,btn_SendSignUpCode;
//    ProgressDialog progressDialog;
//
//    private FirebaseAuth firebaseAuth;
//
//    DatabaseHelper helper;
//
//    //FireBase
//
//    private DatabaseReference databaseReference;
//    private FirebaseDatabse firebaseDatabse;
//
//
//    public SignUpFragment() {
//        // Required empty public constructor
//    }
//
//
//    //values
//    String str_name,str_emailId, str_pwd, str_cpwd,int_mobileNum;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view =  inflater.inflate(R.layout.fragment_sign_up, container, false);
//        declaration(view);
//        initialization(view);
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//    public void declaration(View v){
//        btn_SendSignUpCode = v.findViewById(R.id.btn_SendSignUpCode);
//        et_addMin = v.findViewById(R.id.et_addMin);
//        et_C_password = v.findViewById(R.id.et_C_password);
//        et_EmailID = v.findViewById(R.id.et_EmailID);
//        et_password = v.findViewById(R.id.et_password);
//        et_fullName = v.findViewById(R.id.et_fullName);
//        progressDialog = new ProgressDialog(getActivity());
//        et_userName = v.findViewById(R.id.et_userName);
//        tv_doLogIn = v.findViewById(R.id.tv_doLogIn);
//        btn_signUp = v.findViewById(R.id.btn_signUp);
//        helper = new DatabaseHelper(getContext());
//    }
//    public void initialization(View v){
//
//        btn_signUp.setOnClickListener(this);
//        tv_doLogIn.setOnClickListener(this);
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        databaseReference = FirebaseDatabse.getInstance().getReference("User");
//    }
//    @Override
//    public void onClick(View v) {
////        if(v.getId() == R.id.btn_signUp){
////            if(TextUtils.isEmpty(et_addMin.getText().toString())  || TextUtils.isEmpty(et_C_password.getText().toString())||
////                    TextUtils.isEmpty(et_password.getText().toString())|| TextUtils.isEmpty(et_fullName.getText().toString())||
////                    TextUtils.isEmpty(et_userName.getText().toString())){
////                Toast.makeText(getActivity(), "Fill Fields Properly", Toast.LENGTH_SHORT).show();
////            }
////            else if(!et_C_password.getText().toString().equals(et_password.getText().toString())){
////                Toast.makeText(getActivity(), "Password don't matched", Toast.LENGTH_SHORT).show();
////            }
////            else{
////                //SQLite
////                Contact_SignUp c = new Contact_SignUp();
////                c.setFull_name(et_fullName.getText().toString());
////                c.setPass_word(et_password.getText().toString());
////                c.setPhone_number(Long.valueOf(et_userName.getText().toString()));
////
////                helper.insertContact(c);
////                Intent intent = new Intent(getActivity(), DisplaySignedUpUserActivity.class);
////                startActivity(intent);
////
////                //authentication Firebase
////
////                /*auth.createUserWithEmailAndPassword(et_userName.getText().toString(),et_password.getText().toString())
////                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
////                            @Override
////                            public void onComplete(@NonNull Task<AuthResult> task) {
////                                if(task.isSuccessful()){
////                                    startActivity(new Intent(getActivity(), ShopInformationBottomActivity.class));
////                                    Toast.makeText(getActivity(), "SignUp Successful", Toast.LENGTH_SHORT).show();
////                                }
////                                else{
////                                    Toast.makeText(getActivity(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
////                                }
////                            }
////                        });*/
////
////
////                //sqlite
////
////
////            }
////        }
//
//        if (v.getId() == R.id.btn_SendSignUpCode){
//            if(TextUtils.isEmpty(et_addMin.getText().toString())  || TextUtils.isEmpty(et_C_password.getText().toString())||
//                    TextUtils.isEmpty(et_password.getText().toString())|| TextUtils.isEmpty(et_fullName.getText().toString())||
//                    TextUtils.isEmpty(et_userName.getText().toString())){
//                  Toast.makeText(getActivity(), "Fill Fields Properly", Toast.LENGTH_SHORT).show();
//            }
//            if(!et_C_password.getText().toString().equals(et_password.getText().toString())){
//                Toast.makeText(getActivity(), "Password don't matched", Toast.LENGTH_SHORT).show();
//            }
//            else{
//
//            }
//        }
//
//        else if (v.getId()==R.id.btn_signUp) {
//            str_name = et_fullName.getText().toString().trim();
//            str_emailId = et_EmailID.getText().toString();
//            str_pwd = et_password.getText().toString().trim();
//            str_cpwd = et_C_password.getText().toString().trim();
//            int_mobileNum = et_userName.getText().toString().trim();
//
//            if (TextUtils.isEmpty(et_addMin.getText().toString()) || TextUtils.isEmpty(et_C_password.getText().toString()) || TextUtils.isEmpty(et_password.getText().toString()) || TextUtils.isEmpty(et_fullName.getText().toString()) || TextUtils.isEmpty(et_userName.getText().toString())) {
//                Toast.makeText(getActivity(), "Fill Fields Properly", Toast.LENGTH_SHORT).show();
//            }
//            if (!et_C_password.getText().toString().equals(et_password.getText().toString())) {
//                Toast.makeText(getActivity(), "Password don't matched", Toast.LENGTH_SHORT).show();
//            }
//            else if(v.getId() == R.id.tv_doLogIn){
//                getFragmentManager().beginTransaction().replace(R.id.frame_login_signUp,new LoginFragment()).addToBackStack(null).commit();
//            }
//            else {
//                progressDialog.setMessage("Please Wait for a time");
//                progressDialog.show();
//
//                firebaseAuth.createUserWithEmailAndPassword(str_emailId, str_pwd)
//                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            //registered successful
//                            Toast.makeText(getActivity(), "You Are Registered", Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();
//                        }
//                        else{
//                            Toast.makeText(getActivity(), "sorry", Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();
//                        }
//                    }
//                });
//            }
//        }
//
//    }
//}


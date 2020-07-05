package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dhruvi.dhruvisonani.adminsidexa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dhruvi.dhruvisonani.adminsidexa.Activity.HomeActivity.MyPREFERENCES;

public class EditProfileFragment extends Fragment {

    TextView tv_ep_mobileNumber, tv_et_Name, tv_et_gender, tv_et_Address, tv_ep_mailId, tv_ep_licence, tv_et_shopName,tv_ep_shopTime;
    EditText et_et_a3, et_et_a4, et_et_a2, et_et_a1, et_et_bwps, et_et_cps, et_et_bwxs, et_et_cxs, et_et_ciwb, et_et_cb, et_et_jb, et_et_pb, et_et_ssb, et_et_sswb;
    EditText et_et_hb, et_et_ssp, et_et_msp, et_et_lsp, et_et_pwd, et_et_rpwd;
    ImageView iv_et_update;
//    AutoCompleteTextView ATv_EndTime, ATv_EndDay, ATv_startDay,ATv_startTime;
    DocumentReference databaseReference;
    FirebaseFirestore db;

    String username;
    String UserId;
    public static String ShopName;
    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        declaration(view);
        initialization();
        return view;
    }

    private void initialization() {
        iv_et_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialog();
//                Toast.makeText(getActivity(), "Update", Toast.LENGTH_SHORT).show();
            }
        });
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        username = sharedpreferences.getString("uname", "");

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
                            public void onEvent( DocumentSnapshot snapshot,  FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    if (username.equals(snapshot.getString("Mobile Number"))) {
                                        setValue(snapshot);
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
    }

    private void UpdateValue() {
        UserId = tv_et_shopName.getText().toString();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Shopkeeper").document(UserId);
        Map<String, Object> User = new HashMap<>();
        User.put("Full Name", tv_et_Name.getText().toString());
        User.put("et_hard", et_et_hb.getText().toString());
        User.put("Email Id", tv_ep_mailId.getText().toString());
        User.put("Mobile Number", tv_ep_mobileNumber.getText().toString());
        User.put("Shop Time", tv_ep_shopTime.getText().toString());
        User.put("Gender", tv_et_gender.getText().toString());
        if (et_et_pwd.getText().toString().equals(et_et_rpwd.getText().toString())) {
            User.put("Password", et_et_pwd.getText().toString());
        } else {
//            User.put("Password", "Dhruv");//et_et_pwd.getText().toString());
            Toast.makeText(getActivity(), "You  ", Toast.LENGTH_SHORT).show();
            return;
        }

        User.put("Shop Name", tv_et_shopName.getText().toString());
        User.put("Address", tv_et_Address.getText().toString());
        User.put("Licence Number", tv_ep_licence.getText().toString());
        User.put("Color Print Price", et_et_cps.getText().toString());
        User.put("Color Xerox Price", et_et_cxs.getText().toString());
        User.put("BW Print Price", et_et_bwps.getText().toString());
        User.put("et_BWxeroxPrice", et_et_bwxs.getText().toString());
        User.put("et_A1", et_et_a1.getText().toString());
        User.put("et_A2", et_et_a2.getText().toString());
        User.put("et_A3", et_et_a3.getText().toString());
        User.put("et_A4", et_et_a4.getText().toString());
        User.put("et_sadleStiching", et_et_ssb.getText().toString());
        User.put("et_sectionSewn", et_et_sswb.getText().toString());
        User.put("et_CasedInWiro", et_et_ciwb.getText().toString());
        User.put("et_Japanese", et_et_jb.getText().toString());
        User.put("et_Pamphlet", et_et_pb.getText().toString());
        User.put("et_Coptic", et_et_cb.getText().toString());
        User.put("et_smallSpiral", et_et_ssp.getText().toString());
        User.put("et_MeduimSpiral", et_et_msp.getText().toString());
        User.put("et_LargeSpiral", et_et_lsp.getText().toString());
        User.put("et_hard", et_et_hb.getText().toString());

        documentReference.set(User).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void OpenDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null);
        dialog.setContentView(view);

        TextView tv_customdialog = view.findViewById(R.id.tv_customdialog);
        Button edit = view.findViewById(R.id.btn_cancel_popup);
        Button delete = view.findViewById(R.id.btn_delete_data);
        delete.setText("Update");
        tv_customdialog.setText("Are you sure to update your details?");
        dialog.setCanceledOnTouchOutside(false);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setValue(DocumentSnapshot snapshot) {
        et_et_pwd.setText(snapshot.getString("Password"));
        et_et_rpwd.setText(snapshot.getString("Password"));
        if (snapshot.getString("et_sadleStiching").equals("")) {
            et_et_ssb.setHint("No");
        } else {
            et_et_ssb.setText(snapshot.getString("et_sadleStiching"));
        }
        if (snapshot.getString("et_Coptic").equals("")) {
            et_et_cb.setHint("No");
        } else {
            et_et_cb.setText(snapshot.getString("et_Coptic"));
        }
        if (snapshot.getString("et_Japanese").equals("")) {
            et_et_jb.setHint("No");
        } else {
            et_et_jb.setText(snapshot.getString("et_Japanese"));
        }
        if (snapshot.getString("et_CasedInWiro").equals("")) {
            et_et_ciwb.setHint("No");
        } else {
            et_et_ciwb.setText(snapshot.getString("et_CasedInWiro"));
        }
        if (snapshot.getString("et_Pamphlet").equals("")) {
            et_et_pb.setHint("No");
        } else {
            et_et_pb.setText(snapshot.getString("et_Pamphlet"));
        }
        et_et_cps.setText(snapshot.getString("Color Print Price"));
        et_et_bwps.setText(snapshot.getString("BW Print Price"));
        et_et_cxs.setText(snapshot.getString("Color Xerox Price"));

        if (snapshot.getString("et_sectionSewn").equals("")) {
            et_et_sswb.setHint("No");
        } else {
            et_et_sswb.setText(snapshot.getString("et_sectionSewn"));
        }
        if (snapshot.getString("et_A1").equals("")) {
            et_et_a1.setHint("No");
        } else {
            et_et_a1.setText(snapshot.getString("et_A1"));
        }
        et_et_bwxs.setText(snapshot.getString("et_BWxeroxPrice"));
        et_et_ssp.setText(snapshot.getString("et_smallSpiral"));
        et_et_lsp.setText(snapshot.getString("et_LargeSpiral"));
        et_et_msp.setText(snapshot.getString("et_MeduimSpiral"));
        if (snapshot.getString("et_A4").equals("")) {
            et_et_a4.setHint("No");
        } else {
            et_et_a4.setText(snapshot.getString("et_A4"));
        }
        if (snapshot.getString("et_A3").equals("")) {
            et_et_a3.setHint("No");
        } else {
            et_et_a3.setText(snapshot.getString("et_A3"));
        }
        if (snapshot.getString("et_A2").equals("")) {
            et_et_a2.setHint("No");
        } else {
            et_et_a2.setText(snapshot.getString("et_A2"));
        }
        tv_et_Name.setText(snapshot.getString("Full Name"));
        tv_ep_mobileNumber.setText(snapshot.getString("Mobile Number"));
        tv_ep_shopTime.setText(snapshot.getString("Shop Time"));


        tv_ep_mailId.setText(snapshot.getString("Email Id"));
        tv_et_Address.setText(snapshot.getString("Address"));
        tv_et_gender.setText(snapshot.getString("Gender"));
        tv_ep_licence.setText(snapshot.getString("Licence Number"));
        tv_et_shopName.setText(snapshot.getString("Shop Name"));
        ShopName = tv_et_shopName.getText().toString();

        try {
            if (snapshot.getString("et_hard").equals("")) {
                et_et_hb.setHint("No");
            } else {
                et_et_hb.setText(snapshot.getString("et_hard"));
            }
        } catch (Exception e) {
        }
    }


    private void declaration(View view) {

        db = FirebaseFirestore.getInstance();
        iv_et_update = view.findViewById(R.id.iv_et_update);

        tv_ep_mobileNumber = view.findViewById(R.id.tv_ep_mobileNumber);
        tv_ep_shopTime = view.findViewById(R.id.tv_ep_shopTime);
        tv_et_Name = view.findViewById(R.id.tv_et_Name);
        tv_et_gender = view.findViewById(R.id.tv_et_gender);
        tv_et_Address = view.findViewById(R.id.tv_et_Address);
        tv_ep_mailId = view.findViewById(R.id.tv_ep_mailId);
        tv_ep_licence = view.findViewById(R.id.tv_ep_licence);
        tv_et_shopName = view.findViewById(R.id.tv_et_shopName);

        et_et_a3 = view.findViewById(R.id.et_et_a3);
        et_et_a4 = view.findViewById(R.id.et_et_a4);
        et_et_a2 = view.findViewById(R.id.et_et_a2);
        et_et_a1 = view.findViewById(R.id.et_et_a1);
        et_et_bwps = view.findViewById(R.id.et_et_bwps);
        et_et_cps = view.findViewById(R.id.et_et_cps);
        et_et_bwxs = view.findViewById(R.id.et_et_bwxs);
        et_et_cxs = view.findViewById(R.id.et_et_cxs);
        et_et_ciwb = view.findViewById(R.id.et_et_ciwb);
        et_et_cb = view.findViewById(R.id.et_et_cb);
        et_et_jb = view.findViewById(R.id.et_et_jb);
        et_et_pb = view.findViewById(R.id.et_et_pb);
        et_et_ssb = view.findViewById(R.id.et_et_ssb);
        et_et_sswb = view.findViewById(R.id.et_et_sswb);
        et_et_hb = view.findViewById(R.id.et_et_hb);
        et_et_ssp = view.findViewById(R.id.et_et_ssp);
        et_et_msp = view.findViewById(R.id.et_et_msp);
        et_et_lsp = view.findViewById(R.id.et_et_lsp);
        et_et_rpwd = view.findViewById(R.id.et_et_rpwd);
        et_et_pwd = view.findViewById(R.id.et_et_pwd);

    }

}

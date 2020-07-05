package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;

//Shop Time
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.RequestsEntity;
import com.dhruvi.dhruvisonani.adminsidexa.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.dhruvi.dhruvisonani.adminsidexa.Activity.HomeActivity.MyPREFERENCES;

public class RequestsFragment extends Fragment {

    final int Send_sms_request_code = 1;

    RecyclerView rv_requestTocome, rv_notFound;

    public static String shopname, Str_UserName;

    Fragment fragment = null;
    Query query;
    FirebaseRecyclerOptions<RequestsEntity> options;
    FirebaseRecyclerAdapter<RequestsEntity, MyViewHodlderForRequestFragment> adapterFirebase;
    RecyclerView.LayoutManager layoutManager;
    SmsManager smsManager;
    DatabaseReference databaseReference, bookreference, from, to;
    RequestsEntity requestsEntity;
    String username, str_imageUri, formattedDate, str_message, timeToShow;


    public RequestsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_requests2, container, false);

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        username = sharedpreferences.getString("uname", "");

        rv_requestTocome = v.findViewById(R.id.rv_dhruviRequest);
        requestsEntity = new RequestsEntity();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = df.format(c);

        if (checkSelfPermission(Manifest.permission.SEND_SMS)) {
            loadRequest();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, Send_sms_request_code);
        }


        return v;
    }

    private boolean checkSelfPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(getActivity(), permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    private void loadRequest() {

        getShopKeeperNumber();

        query = FirebaseDatabase.getInstance().getReference().child("RequestPrintAttachment").child(username);//.orderByChild("");
        options = new FirebaseRecyclerOptions.Builder<RequestsEntity>().setQuery(query, RequestsEntity.class).build();


        databaseReference = FirebaseDatabase.getInstance().getReference().child("RequestPrintAttachment").child(username);
        bookreference = FirebaseDatabase.getInstance().getReference().child("AddNewBookEntity").child(username);


        adapterFirebase = new FirebaseRecyclerAdapter<RequestsEntity, MyViewHodlderForRequestFragment>(options) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHodlderForRequestFragment holder1, final int i, final RequestsEntity entity) {
                String str_PageNum = "", str_color = "", str_mobile = "", str_url = "";
                try {
                    holder1.ll_xerox.setVisibility(View.GONE);
                    holder1.ll_book.setVisibility(View.GONE);
//                    holder1.btn_emergency.setVisibility(View.VISIBLE);
                    holder1.ll_print.setVisibility(View.VISIBLE);
                    str_PageNum = entity.getStr_numpage();
                    str_color = entity.getStr_color_count();
                    str_mobile = entity.getStr_MobileNum();
                    str_url = entity.getUrl();
                    holder1.cv_request.setCardBackgroundColor(Color.parseColor("#81D4FA"));
                    holder1.tv_requests_Side.setText(entity.getStr_rb_side());
                    holder1.tv_requests_mobileNumber.setText(entity.getStr_MobileNum());
                    holder1.tv_requests_pageSize.setText(entity.getStr_cb_pagesize());
                    holder1.tv_requests_Note.setText(entity.getStr_note());
                    holder1.tv_requests_pageNumber.setText(entity.getStr_numpage());
                    holder1.tv_reuqests_spiral.setText(entity.getStr_rb_spiral());
                    holder1.tv_requests_bound.setText(entity.getStr_rb_bound());
                    holder1.tv_requests_totalAmount.setText(entity.getStr_total());
                    holder1.tv_requests_Note.setText(entity.getStr_note());
                    if (!entity.getStr_bw_count().equals("0") && !entity.getStr_color_count().equals("0")) {
                        holder1.tv_requests_numberOfCopy.setText(entity.getStr_bw_count() + " Bw and " + entity.getStr_color_count() + " Color");
                        holder1.tv_requests_printType.setText("Both");
                        ;
                    } else if (entity.getStr_color_count().equals("0")) {
                        holder1.tv_requests_numberOfCopy.setText(entity.getStr_bw_count());
                        holder1.tv_requests_printType.setText("Black & White");
                    } else {
                        holder1.tv_requests_numberOfCopy.setText(entity.getStr_color_count());
                        holder1.tv_requests_printType.setText("Color");

                    }
                } catch (Exception e) {
                    try {
//                        holder1.btn_emergency.setVisibility(View.VISIBLE);
                        holder1.ll_print.setVisibility(View.GONE);
                        holder1.ll_xerox.setVisibility(View.VISIBLE);
                        holder1.ll_book.setVisibility(View.GONE);
                        str_PageNum = entity.getStr_page();
                        str_color = entity.getStr_color();
                        str_mobile = entity.getStr_MobileNum();
                        holder1.cv_request.setCardBackgroundColor(Color.parseColor("#E0F7FA"));
                        holder1.tv_requests_numberOfpageXerox.setText(entity.getStr_sum());
                        holder1.tv_requests_mobileNumberXerox.setText(entity.getStr_MobileNum());
                        holder1.tv_requests_SideXerox.setText(entity.getStr_side());
                        holder1.tv_requests_NoteXerox.setText(entity.getStr_note());
                        holder1.tv_requests_pagesXerox.setText(entity.getStr_page());
                        holder1.tv_reuqests_spiralXerox.setText(entity.getStr_spiral());
                        holder1.tv_requests_totalAmountXerox.setText(entity.getStr_total());
                        holder1.tv_requests_Note.setText(entity.getStr_note());
                        if (!entity.getStr_bw().equals("0") && !entity.getStr_color().equals("0")) {

                            holder1.tv_requests_numberOfCopyXerox.setText(entity.getStr_bw() + " Bw and " + entity.getStr_color() + " Color");
                            holder1.tv_requests_printTypeXerox.setText("Both");
                        } else if (entity.getStr_color().equals("0")) {
                            holder1.tv_requests_numberOfCopyXerox.setText(entity.getStr_bw() + " ");

                            holder1.tv_requests_printTypeXerox.setText("Black & White");
                        } else {
                            holder1.tv_requests_numberOfCopyXerox.setText(entity.getStr_color() + " ");
                            holder1.tv_requests_printTypeXerox.setText("Color");
                        }
                    } catch (Exception ee) {
                        holder1.btn_emergency.setVisibility(View.GONE);
                        holder1.ll_xerox.setVisibility(View.GONE);
                        holder1.ll_print.setVisibility(View.GONE);
                        holder1.ll_book.setVisibility(View.VISIBLE);
                        holder1.btn_requests_approve.setVisibility(View.GONE);
                        holder1.btn_requests_reject.setVisibility(View.VISIBLE);
                        holder1.btn_requests_book.setVisibility(View.VISIBLE);
                        holder1.cv_request.setCardBackgroundColor(Color.parseColor("#33AAFF"));
                        holder1.tv_requests_BookNam.setText(entity.getBookName());
                        holder1.tv_requests_ShopContactBook.setText(entity.getStr_MobileNum());
                        holder1.tv_reuqests_BookPrice.setText(entity.getBookPrice());

                    }
                }

                final String finalStr_PageNum = str_PageNum;
                final String finalStr_color = str_color;
                final String finalStr_mobile = str_mobile;
                loadName(finalStr_mobile);
                holder1.btn_requests_approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder1.ll_xerox.getVisibility() == View.VISIBLE) {
                            int time = 0;

                            str_message = "Hello " + Str_UserName + ", Your Request to " + shopname + " For Xerox " + "Page(s) : " + holder1.tv_requests_pagesXerox.getText().toString() + "\nSpiral : " + holder1.tv_reuqests_spiralXerox.getText().toString() + "\nPrint Type : " + holder1.tv_requests_printTypeXerox.getText().toString() + "\nSide : " + holder1.tv_requests_SideXerox.getText().toString() + "\nNumber Of Copy : " + holder1.tv_requests_numberOfCopyXerox.getText().toString() + "\nAmount : " + holder1.tv_requests_totalAmount.getText().toString() + "\nTotal Page : " + holder1.tv_requests_numberOfpageXerox.getText().toString() + "\nhas been receieved and we assure to do your task on time. please visit and give your book to us.\nYour Id is : "+ finalStr_mobile + "_" + finalStr_PageNum + "_" + finalStr_color+"\n\nThank You,\n" + shopname;

                            //                            str_message = "Hello " + Str_UserName + ", Your Request to " + shopname + " For Xerox " + holder1.tv_requests_numberOfpageXerox.getText().toString() + " Page(s), " + holder1.tv_reuqests_spiralXerox.getText().toString() + " Spiral, " + holder1.tv_requests_printTypeXerox.getText().toString() + " Print," + holder1.tv_requests_SideXerox.getText().toString() + " of costed " + holder1.tv_requests_totalAmount.getText().toString() + " INR has been receieved.Come n visit shop.";//, we assure to do your task on time.";// you will get your checkout message soon.\n\nThank You.";}
                            Accept(finalStr_PageNum, finalStr_color, finalStr_mobile, str_message, time);
                        } else {
//                            str_message = "Hello " + Str_UserName + ", Your Request to " + shopname + " For Print " + holder1.tv_requests_pageNumber.getText().toString() + "Pages, " + holder1.tv_reuqests_spiral.getText().toString() + " Spiral," + holder1.tv_requests_printType.getText().toString() + " Print, " + holder1.tv_requests_Side.getText().toString() + " Side," + holder1.tv_requests_bound.getText().toString() + "Bound : " + holder1.tv_requests_pageSize.getText().toString() + "Page Size costed" + holder1.tv_requests_totalAmount.getText().toString() + " INR";// has been receieved and we assure to do your task on time. you will get your checkout message soon.\n\nThank You.";
                            int time = 1;
                            str_message = "Hello " + Str_UserName + ",Your Request to " + shopname + " For Print\n" + "Page Count: " + holder1.tv_requests_pageNumber.getText().toString() + "\nSpiral : " + holder1.tv_reuqests_spiral.getText().toString() + "\nPrint Type : " + holder1.tv_requests_printType.getText().toString() + "\nSide : " + holder1.tv_requests_Side.getText().toString() + "\nNumber Of Copy : " + holder1.tv_requests_numberOfCopy.getText().toString() + "\nBound : " + holder1.tv_requests_bound.getText().toString() + "\nPage Size : " + holder1.tv_requests_pageSize.getText().toString() + "\nAmount : " + holder1.tv_requests_totalAmount.getText().toString() + " INR" + "\nhas been receieved and we assure to do your task on time";

                            Accept(finalStr_PageNum, finalStr_color, finalStr_mobile, str_message, time);
                        }
                    }
                });

                holder1.btn_emergency.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setCancelable(true);
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_dialog, null);
                        dialog.setContentView(view);
                        TextView tv_customdialog = view.findViewById(R.id.tv_customdialog);
                        Button edit = view.findViewById(R.id.btn_cancel_popup);
                        Button delete = view.findViewById(R.id.btn_delete_data);
                        delete.setText("Yes, Sure");
                        tv_customdialog.setText("Are you sure for sending your inconveniency for task completion?");
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
                                if (holder1.ll_print.getVisibility() == View.VISIBLE) {
                                    str_message = "Hello " + Str_UserName + ", You have sent a print request to " + shopname + " but due to raised technical error in machine we will not be able to make your task done on time. if not emergency you will you please be waited till you get acceptence message? else you may go for refund.\n\nThank You,\n" + shopname;
                                } else {
                                    str_message = "Hello " + Str_UserName + ", You have sent a Xerox request to " + shopname + " but due to raised technical error in machine we will not be able to make your task done on time. if not emergency you will you please be waited till you get acceptence message? else you  may go for refund.\n\nThank You,\n" + shopname;
                                }
                                smsManager = SmsManager.getDefault();

                                smsManager.sendTextMessage(finalStr_mobile, username, str_message, null, null);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();


                    }
                });

                holder1.btn_requests_book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_imageUri = entity.getStr_imageId();
                        str_message = "Hello " + Str_UserName + ", Your Interest for buying book from " + shopname + " is welcomed. Please collect " + holder1.tv_requests_BookNam.getText().toString() + " of price " + holder1.tv_reuqests_BookPrice.getText().toString() + " on your convenience.\n\nThank You.";
                        deletebook(holder1.tv_requests_BookNam, finalStr_mobile, str_message);
                    }
                });
                final String finalStr_url = str_url;
                holder1.btn_requests_reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (holder1.ll_xerox.getVisibility() == View.VISIBLE) {
                            str_message = "Hello " + Str_UserName + ",We are Sorry as we can not complete your Xerox request by today. You can send your request tommorrow if not emergency.\nThank You,\n" + shopname;
                            Decline(finalStr_PageNum, finalStr_color, finalStr_mobile, finalStr_url);
                        } else if (holder1.ll_print.getVisibility() == View.VISIBLE) {
                            str_message = "Hello " + Str_UserName + ",We are Sorry as we can not complete your Print request by today. You can send your request tommorrow if not emergency.\nThank You,\n" + shopname;
                            Decline(finalStr_PageNum, finalStr_color, finalStr_mobile, finalStr_url);
                        } else {
                            str_message = "Hello " + Str_UserName + ",We already got reuqest for buying " + holder1.tv_requests_BookNam.getText().toString() + ". You can buy another book from our list or requested to go for refund.\nThank You,\n" + shopname;
                            if (checkSelfPermission(Manifest.permission.SEND_SMS)) {
                                smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(finalStr_mobile, username, str_message, null, null);
                                databaseReference.child(databaseReference.child(username+"_"+holder1.tv_requests_BookNam.getText().toString()+"_").getKey()).removeValue();
                            }
                            else {
                                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });
            }

            @NonNull
            @Override
            public MyViewHodlderForRequestFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_for_requets_fragment, null);
                return new MyViewHodlderForRequestFragment(v);
            }
        };
        adapterFirebase.startListening();
        rv_requestTocome.setAdapter(adapterFirebase);
    }


    private void loadName(String number) {
        DocumentReference doc = FirebaseFirestore.getInstance().collection("Users").document(number);

        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Str_UserName = snapshot.getString("Full Name");
                }
            }
        });


    }

    private void getShopKeeperNumber() {
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
                        DocumentReference doc = FirebaseFirestore.getInstance().collection("Shopkeeper").document(value);
                        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    if (username.equals(snapshot.getString("Mobile Number"))) {
                                        shopname = snapshot.getString("Shop Name");
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

    private void deletebook(final TextView tv_requests_bookNam, final String finalStr_mobile, final String str_msg) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_dialog, null);
        dialog.setContentView(view);
        TextView tv_customdialog = view.findViewById(R.id.tv_customdialog);
        Button edit = view.findViewById(R.id.btn_cancel_popup);
        Button delete = view.findViewById(R.id.btn_delete_data);
        delete.setText("Accept");
        tv_customdialog.setText("Accepting request, your book will be deleted from your account.");
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
                moveToPendingBook(tv_requests_bookNam.getText().toString(), dialog, finalStr_mobile, str_msg);
            }
        });
        dialog.show();

    }


    private void Decline(final String str_PageNum, final String str_color, final String str_mobile, final String url) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_dialog, null);
        dialog.setContentView(view);
        TextView tv_customdialog = view.findViewById(R.id.tv_customdialog);
        Button edit = view.findViewById(R.id.btn_cancel_popup);
        Button delete = view.findViewById(R.id.btn_delete_data);
        delete.setText("Decline");
        tv_customdialog.setText("Are you sure to decline request? once you reject request, it's unrecoverable.");
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
                //Do something
                try {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS)) {
                        smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(str_mobile, username, str_message, null, null);

                        String set_NEW_Records_Key = databaseReference.child(str_mobile + "_" + str_PageNum + "_" + str_color).getKey();
                        databaseReference.child(set_NEW_Records_Key).removeValue();
                        StorageReference StorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        StorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "ffd", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
//                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                Toast.makeText(getActivity(), "Rejected", Toast.LENGTH_SHORT).show();

            }
        });
        dialog.show();
    }


    private void Accept(final String str_PageNum, final String str_color, final String str_mobile, String message, final int time) {


        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        final TextView tv_checkoutTime;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_dialog, null);
        dialog.setContentView(view);

        TextView tv_customdialog = view.findViewById(R.id.tv_customdialog);
        tv_customdialog.setText("You are accepting request.Customer will see you with document soon.");

        Button edit = view.findViewById(R.id.btn_cancel_popup);
        Button delete = view.findViewById(R.id.btn_delete_data);
        delete.setText("Accept");

        dialog.setCanceledOnTouchOutside(false);

        final String[] d = new String[1];

        if (time == 1) {

            tv_customdialog.setText("You are accepting request.checkout time will be sent to your customer.");
            LinearLayout rv_timeSend = view.findViewById(R.id.rv_timeSend);
            rv_timeSend.setVisibility(View.VISIBLE);

            final TextView tv_15 = view.findViewById(R.id.tv_15);
            final TextView tv_20 = view.findViewById(R.id.tv_20);
            final TextView tv_30 = view.findViewById(R.id.tv_30);
            final TextView tv_45 = view.findViewById(R.id.tv_45);
            final TextView tv_60 = view.findViewById(R.id.tv_60);
            final TextView tv_90 = view.findViewById(R.id.tv_90);
            final TextView tv_75 = view.findViewById(R.id.tv_75);
            final TextView tv_120 = view.findViewById(R.id.tv_120);
            tv_checkoutTime = view.findViewById(R.id.tv_checkoutTime);

//            tv_15.setOnClickListener(this);
//            tv_30.setOnClickListener(this);

            final String finalMessage1 = message;//+"\n\n ID: "+str_mobile+"_"+str_PageNum+"_"+str_color+"\n";
            tv_15.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_15.setBackgroundColor(Color.parseColor("#000000"));
                    tv_15.setTextColor(Color.parseColor("#FFFFFF"));
                    getUserTime(tv_15.getText().toString().split(" ")[0]);
                    tv_checkoutTime.setText("Print should be ready on " + timeToShow);
//                    d[0] = String.format("%s. %s. you will get your checkout message soon.\n\nThank You.", finalMessage1, tv_checkoutTime.getText().toString());
                    d[0] = String.format("%s. %s.\n\nThank you.", finalMessage1, tv_checkoutTime.getText().toString());
                }
            });

            tv_20.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_20.setBackgroundColor(Color.parseColor("#000000"));
                    tv_20.setTextColor(Color.parseColor("#FFFFFF"));
                    getUserTime(tv_20.getText().toString().split(" ")[0]);
                    tv_checkoutTime.setText("Print shold be ready on " + timeToShow);
                    d[0] = String.format("%s. %s.\n\nThank you.", finalMessage1, tv_checkoutTime.getText().toString());
//                    d[0] = String.format("%s. %s. you will get your checkout message soon.\n\nThank You.", finalMessage1, tv_checkoutTime.getText().toString());
                }
            });

            tv_30.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_30.setBackgroundColor(Color.parseColor("#000000"));
                    tv_30.setTextColor(Color.parseColor("#FFFFFF"));
                    getUserTime(tv_30.getText().toString().split(" ")[0]);
                    tv_checkoutTime.setText("Print shold be ready on " + timeToShow);
                    d[0] = String.format("%s. %s.\n\nThank you.", finalMessage1, tv_checkoutTime.getText().toString());
//                    d[0] = String.format("%s. %s. you will get your checkout message soon.\n\nThank You.", finalMessage1, tv_checkoutTime.getText().toString());
                }
            });

            tv_45.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_45.setBackgroundColor(Color.parseColor("#000000"));
                    tv_45.setTextColor(Color.parseColor("#FFFFFF"));
                    getUserTime(tv_45.getText().toString().split(" ")[0]);
                    tv_checkoutTime.setText("Print shold be ready on " + timeToShow);
                    d[0] = String.format("%s. %s.\n\nThank you.", finalMessage1, tv_checkoutTime.getText().toString());
//                    d[0] = String.format("%s. %s. you will get your checkout message soon.\n\nThank You.", finalMessage1, tv_checkoutTime.getText().toString());
                }
            });

            tv_60.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_60.setBackgroundColor(Color.parseColor("#000000"));
                    tv_60.setTextColor(Color.parseColor("#FFFFFF"));
                    getUserTime(tv_60.getText().toString().split(" ")[0]);
                    tv_checkoutTime.setText("Print shold be ready on " + timeToShow);
                    d[0] = String.format("%s. %s.\n\nThank you.", finalMessage1, tv_checkoutTime.getText().toString());
//                    d[0] = String.format("%s. %s. you will get your checkout message soon.\n\nThank You.", finalMessage1, tv_checkoutTime.getText().toString());
                }
            });

            tv_75.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_75.setBackgroundColor(Color.parseColor("#000000"));
                    tv_75.setTextColor(Color.parseColor("#FFFFFF"));
                    getUserTime(tv_75.getText().toString().split(" ")[0]);
                    tv_checkoutTime.setText("Print shold be ready on " + timeToShow);
                    d[0] = String.format("%s. %s.\n\nThank you.", finalMessage1, tv_checkoutTime.getText().toString());
//                    d[0] = String.format("%s. %s. you will get your checkout message soon.\n\nThank You.", finalMessage1, tv_checkoutTime.getText().toString());
                }
            });

            tv_90.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_90.setBackgroundColor(Color.parseColor("#000000"));
                    tv_90.setTextColor(Color.parseColor("#FFFFFF"));
                    getUserTime(tv_90.getText().toString().split(" ")[0]);
                    tv_checkoutTime.setText("Print shold be ready on " + timeToShow);
                    d[0] = String.format("%s. %s.\n\nThank you.", finalMessage1, tv_checkoutTime.getText().toString());
//                    d[0] = String.format("%s. %s. you will get your checkout message soon.\n\nThank You.", finalMessage1, tv_checkoutTime.getText().toString());
                }
            });


            tv_120.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_120.setBackgroundColor(Color.parseColor("#000000"));
                    tv_120.setTextColor(Color.parseColor("#FFFFFF"));
                    getUserTime(tv_120.getText().toString().split(" ")[0]);
                    tv_checkoutTime.setText("Print shold be ready on " + timeToShow);
                    d[0] = String.format("%s. %s.\n\nThank you.", finalMessage1, tv_checkoutTime.getText().toString());
//                    d[0] = String.format("%s. %s. you will get your checkout message soon.\n\nThank You.", finalMessage1, tv_checkoutTime.getText().toString());
//                    Toast.makeText(getActivity(), d[0], Toast.LENGTH_SHORT).show();

                }
            });

            message = d[0];

        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final String finalMessage = message;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.SEND_SMS)) {
                    //                    Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                    //                    PendingIntent pi = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, 0);
                    //                    PendingIntent deliveredPI = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent, 0);
                    smsManager = SmsManager.getDefault();
                    if (time == 1) {
                        try {
//                        Toast.makeText(getActivity(), d[0], Toast.LENGTH_SHORT).show();
                            smsManager.sendTextMessage(str_mobile, username, d[0], null, null);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Select Time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        smsManager.sendTextMessage(str_mobile, username, finalMessage, null, null);
                    }
                    moveToPending(str_PageNum, str_color, str_mobile, dialog);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void getUserTime(String min) {
//        Toast.makeText(getActivity(), min, Toast.LENGTH_SHORT).show();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm , dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

        Date date = null;
        try {
            date = sdf.parse(currentDateandTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.MINUTE, Integer.parseInt(min));
        if (Calendar.MINUTE > 60) {
            calendar.add(Calendar.HOUR, 1);
        }
        timeToShow = "" + sdf.format(calendar.getTime());
//        Toast.makeText(getActivity(), "" + sdf.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
    }

    private void moveToPendingBook(final String toString, final Dialog dialog, final String finalStr_mobile, final String str_message) {
        to = FirebaseDatabase.getInstance().getReference().child("Accepted_Request_Book").child(username).child(databaseReference.child(username + "_" + toString + "_").getKey());
        from = FirebaseDatabase.getInstance().getReference().child("RequestPrintAttachment").child(username).child(databaseReference.child(username + "_" + toString + "_").getKey());
        from.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                to.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(getActivity(), "Failure in accepting request", Toast.LENGTH_SHORT).show();
                        } else {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(finalStr_mobile, username, str_message, null, null);
                            Toast.makeText(getActivity(), "Book Buy Acceptd ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            databaseReference.child(databaseReference.child(username + "_" + toString + "_").getKey()).removeValue();
                            bookreference.child(databaseReference.child(username + "_" + toString + "_").getKey()).removeValue();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "dfmkdsj", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void moveToPending(final String str_PageNum, final String str_color, final String str_mobile, final Dialog dialog) {

        final String set_NEW_Records_Key = databaseReference.child(str_mobile + "_" + str_PageNum + "_" + str_color).getKey();
//        to = FirebaseDatabase.getInstance().getReference().child("Accepted_Request").child(username).child(formattedDate).child(set_NEW_Records_Key);

        to = FirebaseDatabase.getInstance().getReference().child("Pending_request").child(username).child(set_NEW_Records_Key);
        from = FirebaseDatabase.getInstance().getReference().child("RequestPrintAttachment").child(username).child(set_NEW_Records_Key);
        from.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                to.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(getActivity(), "Failure in accepting request", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Go to Pending to see accepted request", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            databaseReference.child(set_NEW_Records_Key).removeValue();

                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "dfmkdsj", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private class MyViewHodlderForRequestFragment extends RecyclerView.ViewHolder {
        TextView tv_requests_mobileNumber, tv_requests_pageNumber, tv_reuqests_spiral, tv_requests_printType, tv_requests_Side, tv_requests_numberOfCopy, tv_requests_bound, tv_requests_pageSize, tv_requests_totalAmount, tv_requests_paidonline, tv_requests_codeoffline, tv_requests_Note;
        Button btn_requests_approve, btn_requests_reject, btn_requests_book, btn_emergency;

        TextView tv_requests_mobileNumberXerox, tv_requests_pagesXerox, tv_reuqests_spiralXerox, tv_requests_printTypeXerox, tv_requests_SideXerox, tv_requests_numberOfpageXerox, tv_requests_numberOfCopyXerox, tv_requests_totalAmountXerox, tv_requests_paidonlineXerox, tv_requests_codeofflineXerox, tv_requests_NoteXerox;

        TextView tv_requests_ShopContactBook, tv_requests_BookNam, tv_reuqests_BookPrice;

        LinearLayout ll_xerox, ll_print, ll_book;
        CardView cv_request;

        public MyViewHodlderForRequestFragment(@NonNull View itemView) {
            super(itemView);

            ll_print = itemView.findViewById(R.id.ll_print);
            ll_xerox = itemView.findViewById(R.id.ll_xerox);
            ll_book = itemView.findViewById(R.id.ll_book);

            cv_request = itemView.findViewById(R.id.cv_request);


            tv_requests_ShopContactBook = itemView.findViewById(R.id.tv_requests_ShopContactBook);
            tv_requests_BookNam = itemView.findViewById(R.id.tv_requests_BookNam);
            tv_reuqests_BookPrice = itemView.findViewById(R.id.tv_reuqests_BookPrice);
            btn_emergency = itemView.findViewById(R.id.btn_emergency);


            tv_requests_mobileNumberXerox = itemView.findViewById(R.id.tv_requests_mobileNumberXerox);
            tv_requests_pagesXerox = itemView.findViewById(R.id.tv_requests_pagesXerox);
            tv_reuqests_spiralXerox = itemView.findViewById(R.id.tv_reuqests_spiralXerox);
            tv_requests_printTypeXerox = itemView.findViewById(R.id.tv_requests_printTypeXerox);
            tv_requests_numberOfpageXerox = itemView.findViewById(R.id.tv_requests_numberOfpageXerox);
            tv_requests_SideXerox = itemView.findViewById(R.id.tv_requests_SideXerox);
            tv_requests_numberOfCopyXerox = itemView.findViewById(R.id.tv_requests_numberOfCopyXerox);
            tv_requests_totalAmountXerox = itemView.findViewById(R.id.tv_requests_totalAmountXerox);
//            tv_requests_paidonlineXerox = itemView.findViewById(R.id.tv_requests_paidonlineXerox);
//            tv_requests_codeofflineXerox = itemView.findViewById(R.id.tv_requests_codeofflineXerox);
            tv_requests_NoteXerox = itemView.findViewById(R.id.tv_requests_NoteXerox);

            tv_requests_mobileNumber = itemView.findViewById(R.id.tv_requests_mobileNumber);
            tv_requests_pageNumber = itemView.findViewById(R.id.tv_requests_pageNumber);
            tv_reuqests_spiral = itemView.findViewById(R.id.tv_reuqests_spiral);
            tv_requests_printType = itemView.findViewById(R.id.tv_requests_printType);
            tv_requests_Side = itemView.findViewById(R.id.tv_requests_Side);
            tv_requests_numberOfCopy = itemView.findViewById(R.id.tv_requests_numberOfCopy);
            tv_requests_bound = itemView.findViewById(R.id.tv_requests_bound);
            tv_requests_pageSize = itemView.findViewById(R.id.tv_requests_pageSize);
            tv_requests_totalAmount = itemView.findViewById(R.id.tv_requests_totalAmount);
//            tv_requests_paidonline = itemView.findViewById(R.id.tv_requests_paidonline);
//            tv_requests_codeoffline = itemView.findViewById(R.id.tv_requests_codeoffline);
            tv_requests_Note = itemView.findViewById(R.id.tv_requests_Note);
            btn_requests_approve = itemView.findViewById(R.id.btn_requests_approve);
            btn_requests_reject = itemView.findViewById(R.id.btn_requests_reject);
            btn_requests_book = itemView.findViewById(R.id.btn_requests_book);
        }
    }
}


//    private ArrayList<RequestsEntity> getMyList(){
//        ArrayList<RequestsEntity> entities = new ArrayList<>();
//        String[] type = {"Color","B&W"};
//        String[] spiral = {"Yes ","No"};
//        int[] num_of_page = {10,20};
//        String[] side = {"One","Two"};
//        int[] num_of_cpoy={2,3};
//        String[] mobile_num = {"1234567890","1452369870"};
//
//        RequestsEntity m;
//        for(int i=0;i<type.length;i++){
//            m = new RequestsEntity();
//            m.setStr_spiral(spiral[i]);
//            m.setStr_colorBw(type[i]);
//            m.setInt_numOfPages(num_of_page[i]);
//            m.setInt_numOfCopy(num_of_cpoy[i]);
//            m.setInt_mobileNum(mobile_num[i]);
//            m.setStr_side(side[i]);
//            entities.add(m);
//        }
//        return entities;
//    }


//Adapter
//package com.dhruvi.dhruvisonani.adminsidexa.Activity.Adapter;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.RequestsEntity;
//import com.dhruvi.dhruvisonani.adminsidexa.R;
//
//import java.util.ArrayList;
//
//public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MyHolder> {
//
//    Context c;
//    ArrayList<RequestsEntity> mDataset;
//
//    public RequestsAdapter(Context c, ArrayList<RequestsEntity> mDataset) {
//        this.c = c;
//        this.mDataset = mDataset;
//    }
//
//    @NonNull
//    @Override
//    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_for_requets_fragment,null);
//        return new MyHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
//        holder.tv_requests_printType.setText(mDataset.get(i).getStr_colorBw());
//        holder.tv_requests_paidonline.setText(String.valueOf(mDataset.get(i).getInt_paidonline()));
//        holder.tv_requests_pageNumber.setText(String.valueOf(mDataset.get(i).getInt_numOfPages()));
//        holder.tv_requests_mobileNumber.setText(mDataset.get(i).getInt_mobileNum());
//        holder.tv_requests_numberOfCopy.setText(String.valueOf(mDataset.get(i).getInt_numOfCopy()));
//        holder.tv_requests_Side.setText(mDataset.get(i).getStr_side());
//        holder.tv_reuqests_spiral.setText(mDataset.get(i).getStr_spiral());
//        holder.tv_requests_totalAmount.setText(mDataset.get(i).getStr_totalAmount());
//        holder.tv_requests_pageSize.setText(mDataset.get(i).getStr_pageSize());
//        holder.tv_requests_bound.setText(mDataset.get(i).getStr_rb_bound());
//        holder.tv_requests_codeoffline.setText(String.valueOf(mDataset.get(i).getInt_codeoffline()));
//        holder.tv_requests_Note.setText(mDataset.get(i).getStr_note());
//        holder.btn_requests_approve.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Accept();
//                Toast.makeText(c, "Accepted", Toast.LENGTH_SHORT).show();
//            }
//        });
//        holder.btn_requests_reject.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Dialog dialog = new Dialog(c);
//                dialog.setCancelable(true);
//
//                View view = LayoutInflater.from(c).inflate(R.layout.custom_dialog, null);
//                dialog.setContentView(view);
//                TextView tv_customdialog = view.findViewById(R.id.tv_customdialog);
//                Button edit = view.findViewById(R.id.btn_cancel_popup);
//                Button delete = view.findViewById(R.id.btn_delete_data);
//                delete.setText("Decline");
//                tv_customdialog.setText("Are you sure to decline request? once you reject request, it's unrecoverable.");
//                dialog.setCanceledOnTouchOutside(false);
//
//                edit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//                delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        //Do something
////                        String str_NEW_Records_Key = .child(username + "_" + bookname + "_").getKey();
////                        Toast.makeText(getActivity(), str_NEW_Records_Key, Toast.LENGTH_SHORT).show();
////
////                        databaseReference.child(str_NEW_Records_Key).removeValue();
//                        dialog.dismiss();
//                        Toast.makeText(c, "Rejected", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//                dialog.show();
//
//            }
//        });
//    }
//
//    private void Accept() {
//        final Dialog dialog = new Dialog(c);
//        dialog.setCancelable(true);
//
//        View view = LayoutInflater.from(c).inflate(R.layout.custom_dialog, null);
//        dialog.setContentView(view);
//
//        TextView tv_customdialog = view.findViewById(R.id.tv_customdialog);
//        Button edit = view.findViewById(R.id.btn_cancel_popup);
//        Button delete = view.findViewById(R.id.btn_delete_data);
//        delete.setText("Accept");
//        tv_customdialog.setText("You are accepting request.checkout time will be sent to your customer.");
//        dialog.setCanceledOnTouchOutside(false);
//
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                        String str_NEW_Records_Key = .child(username + "_" + bookname + "_").getKey();
////                        databaseReference.child(str_NEW_Records_Key).removeValue();
//                dialog.dismiss();
//                Toast.makeText(c, "You Accepted Request", Toast.LENGTH_SHORT).show();
//            }
//        });
//        dialog.show();
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mDataset.size();
//    }
//
//    public class MyHolder extends RecyclerView.ViewHolder {
//        TextView tv_requests_mobileNumber,tv_requests_pageNumber,tv_requests_printType,tv_reuqests_spiral,tv_requests_numberOfCopy,tv_requests_Side,tv_requests_bound;
//        TextView tv_requests_pageSize,tv_requests_Note,tv_requests_paidonline,tv_requests_codeoffline,tv_requests_totalAmount;
//        ImageView btn_requests_approve,btn_requests_reject;
//        public MyHolder(@NonNull View itemView) {
//            super(itemView);
//            tv_requests_codeoffline = itemView.findViewById(R.id.tv_requests_codeoffline);
//            tv_requests_paidonline = itemView.findViewById(R.id.tv_requests_paidonline);
//            tv_requests_Note = itemView.findViewById(R.id.tv_requests_Note);
//            tv_requests_pageSize = itemView.findViewById(R.id.tv_requests_pageSize);
//            tv_requests_mobileNumber = itemView.findViewById(R.id.tv_requests_mobileNumber);
//            tv_requests_pageNumber = itemView.findViewById(R.id.tv_requests_pageNumber);
//            tv_requests_printType = itemView.findViewById(R.id.tv_requests_printType);
//            tv_reuqests_spiral = itemView.findViewById(R.id.tv_reuqests_spiral);
//            tv_requests_numberOfCopy = itemView.findViewById(R.id.tv_requests_numberOfCopy);
//            btn_requests_approve = itemView.findViewById(R.id.btn_requests_approve);
//            tv_requests_totalAmount = itemView.findViewById(R.id.tv_requests_totalAmount);
//            btn_requests_reject = itemView.findViewById(R.id.btn_requests_reject);
//            tv_requests_Side = itemView.findViewById(R.id.tv_requests_Side);
//            tv_requests_bound = itemView.findViewById(R.id.tv_requests_bound);
//        }
//    }
//}
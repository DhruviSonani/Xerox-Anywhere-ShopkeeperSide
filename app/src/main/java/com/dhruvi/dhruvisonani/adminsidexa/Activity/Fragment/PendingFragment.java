package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;


import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment.RequestsFragment.shopname;
import static com.dhruvi.dhruvisonani.adminsidexa.Activity.HomeActivity.MyPREFERENCES;

public class PendingFragment extends Fragment {
    RecyclerView rv_requestTocome, rv_notFound;
//    StorageReference mStorageReference, sRef;

    //    ArrayList<AddBookEntity> entities;

    TextView tv_bookcolor;
    ImageView img_bookcolor;

    Fragment fragment = null;
    Query query;
    FirebaseRecyclerOptions<RequestsEntity> options, OptionsXerox;
    FirebaseRecyclerAdapter<RequestsEntity, MyViewHodlderForRequestFragment> adapterFirebase;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference databaseReference, from, to;
    RequestsEntity requestsEntity;
    String username, formattedDate, UserFullName;
    int time = 0;

    SmsManager smsManager;

    public PendingFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_requests2, container, false);

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        username = sharedpreferences.getString("uname", "");

        v.findViewById(R.id.img_bookcolor).setVisibility(View.GONE);
        v.findViewById(R.id.tv_bookcolor).setVisibility(View.GONE);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Pending_request").child(username);
        rv_requestTocome = v.findViewById(R.id.rv_dhruviRequest);
        requestsEntity = new RequestsEntity();


        loadRequest();

        return v;
    }


    private void LoadName(String number) {
        DocumentReference doc = FirebaseFirestore.getInstance().collection("Users").document(number);

        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    UserFullName = snapshot.getString("Full Name");
                }
            }
        });


    }

    private void loadRequest() {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        formattedDate = df.format(c);
        query = FirebaseDatabase.getInstance().getReference().child("Pending_request").child(username);
        options = new FirebaseRecyclerOptions.Builder<RequestsEntity>().setQuery(query, RequestsEntity.class).build();


        adapterFirebase = new FirebaseRecyclerAdapter<RequestsEntity, MyViewHodlderForRequestFragment>(options) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHodlderForRequestFragment holder1, final int i, final RequestsEntity entity) {
                String str_PageNum = "", str_color = "", str_mobile = "";
                try {

                    holder1.ll_xerox.setVisibility(View.GONE);
                    holder1.ll_print.setVisibility(View.VISIBLE);
                    holder1.btn_requests_approve.setVisibility(View.GONE);
                    holder1.btn_requests_reject.setVisibility(View.GONE);
                    holder1.iv_download.setVisibility(View.VISIBLE);
                    holder1.img_xeroxDone.setVisibility(View.VISIBLE);
                    holder1.img_Xeroxsend.setVisibility(View.GONE);
                    str_PageNum = entity.getStr_numpage();
                    str_color = entity.getStr_color_count();
                    str_mobile = entity.getStr_MobileNum();
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
                    holder1.cv_request.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setType(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(entity.getUrl()));
                            startActivity(i);

                        }
                    });


                } catch (Exception e) {
                    try {
                        holder1.ll_print.setVisibility(View.GONE);
                        holder1.ll_xerox.setVisibility(View.VISIBLE);

                        holder1.btn_requests_approve.setVisibility(View.GONE);
                        holder1.btn_requests_reject.setVisibility(View.GONE);
                        holder1.iv_download.setVisibility(View.GONE);
                        holder1.img_xeroxDone.setVisibility(View.VISIBLE);
                        holder1.img_Xeroxsend.setVisibility(View.GONE);
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
                        holder1.ll_print.setVisibility(View.GONE);
                        holder1.ll_xerox.setVisibility(View.GONE);
                        holder1.img_xeroxDone.setVisibility(View.GONE);

                    }
                }

                final String finalStr_PageNum = str_PageNum;
                final String finalStr_color = str_color;
                final String finalStr_mobile = str_mobile;

                holder1.img_Xeroxsend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Send" + (i + 1), Toast.LENGTH_SHORT).show();
                    }
                });

                holder1.iv_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            downloadPdf(entity.getUrl(), finalStr_mobile, finalStr_PageNum, finalStr_color);
                            holder1.iv_download.setVisibility(View.INVISIBLE);
                        } else {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9);
                        }


                    }
                });
                holder1.img_xeroxDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadName(finalStr_mobile);
//                        urlDone = entity.getUrl();
                        if (holder1.ll_print.getVisibility() == View.VISIBLE) {
                            time = 1;
                        }
                        TaskCompleted(finalStr_mobile, finalStr_PageNum, finalStr_color);
//                        deleteFile(v);
                    }
                });

                holder1.img_EmergencyPending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadName(finalStr_mobile);
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
                                String str_message;
                                smsManager = SmsManager.getDefault();
                                if (holder1.ll_print.getVisibility() == View.VISIBLE) {
                                    str_message = "Hello " + UserFullName + ", You have sent a print request to " + shopname + " and we've accepted it, you got time too to collect but due to raised technical error in machine we will not be able to make your task done on time. if not emergency you are requested to wait till completion message? else you may go for refund.\n\nThank You,\n" + shopname;
//                                    Toast.makeText(getActivity(), str_message, Toast.LENGTH_SHORT).show();
                                    smsManager.sendTextMessage(finalStr_mobile, username, str_message, null, null);
                                } else {
                                    str_message = "Hello " + UserFullName + ", You have sent a xerox request to " + shopname + " and we've accepted it but due to raised technical error in machine we will not be able to make your task done on time. if not emergency you will you please be waited till you get completion message? else you  may go for refund.\n\nThank You,\n" + shopname;
                                    smsManager.sendTextMessage(finalStr_mobile, username, str_message, null, null);
                                }
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
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
//        Toast.makeText(getActivity(),String.valueOf(adapterFirebase.()), Toast.LENGTH_SHORT).show();
        rv_requestTocome.setAdapter(adapterFirebase);

    }

//    public void deleteFile(View view) {
//        String FILE_NAME = finalStr_mobile + "_" + finalStr_PageNum + "_" + finalStr_color;
//        File dir = new File("Xerox Anywhere/");
//        File file = new File(dir, FILE_NAME);
//        boolean deleted = file.delete();
//        if (deleted) {
//            Toast.makeText(getActivity(), "dsf", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getActivity(), "false", Toast.LENGTH_SHORT).show();
//        }
//        Toast.makeText(getActivity(),String.valueOf( boolean), Toast.LENGTH_SHORT).show();
//    }


    private void TaskCompleted(final String str_mobile, String str_PageNum, String str_color) {
        final String set_NEW_Records_Key = databaseReference.child(str_mobile + "_" + str_PageNum + "_" + str_color).getKey();
//        Toast.makeText(getActivity(), set_NEW_Records_Key, Toast.LENGTH_SHORT).show();
        to = FirebaseDatabase.getInstance().getReference().child("Accepted_Request").child(username).child(formattedDate).child(set_NEW_Records_Key);
        from = FirebaseDatabase.getInstance().getReference().child("Pending_request").child(username).child(set_NEW_Records_Key);
        from.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                to.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(getActivity(), "Failure in accepting request", Toast.LENGTH_SHORT).show();
                        } else {
                            CompletedTaskDialog(str_mobile, set_NEW_Records_Key);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CompletedTaskDialog(final String str_mobile, final String set_NEW_Records_Key) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_dialog, null);
        dialog.setContentView(view);
        TextView tv_customdialog = view.findViewById(R.id.tv_customdialog);
        Button edit = view.findViewById(R.id.btn_cancel_popup);
        Button delete = view.findViewById(R.id.btn_delete_data);
        delete.setText("Yes");
        tv_customdialog.setText("Have you surely Completed task?");
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
                smsManager = SmsManager.getDefault();
                if (time == 1) {
                    smsManager.sendTextMessage(str_mobile, username, "Hi " + UserFullName + ", Your requested Print is completed. Please collect from shop imminently.\n\nStay associated with us.\nThank You,\n" + shopname, null, null);
                } else {
                    smsManager.sendTextMessage(str_mobile, username, "Hi " + UserFullName + ", Your requested Xerox is completed. Please collect from shop imminently.\n\nStay associated with us.\nThank You,\n" + shopname, null, null);
                }
                Toast.makeText(getActivity(), "Your Task is completed", Toast.LENGTH_SHORT).show();
                databaseReference.child(set_NEW_Records_Key).removeValue();

//                StorageReference StorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(urlDone);
//                StorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getActivity(), "Error while clearing from storage", Toast.LENGTH_SHORT).show();
//                    }
//                });

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 9:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Please Provide Permission For Reading Storage", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void downloadPdf(String url, String mNum, String page, String color) {
        Toast.makeText(getActivity(), "Downloading : " + mNum + "_" + page + "_" + color + ".pdf", Toast.LENGTH_SHORT).show();
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getActivity(), "Xerox Anywhere", mNum + "_" + page + "_" + color + ".pdf");
        downloadManager.enqueue(request);
        Toast.makeText(getActivity(), "go FileManager-> Xerox Anywhere", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private class MyViewHodlderForRequestFragment extends RecyclerView.ViewHolder {
        TextView tv_requests_mobileNumber, tv_requests_pageNumber, tv_reuqests_spiral, tv_requests_printType, tv_requests_Side, tv_requests_numberOfCopy, tv_requests_bound, tv_requests_pageSize, tv_requests_totalAmount, tv_requests_paidonline, tv_requests_codeoffline, tv_requests_Note;
        Button btn_requests_approve, btn_requests_reject, btn_emergency, img_EmergencyPending;
        TextView tv_requests_mobileNumberXerox, tv_requests_pagesXerox, tv_reuqests_spiralXerox, tv_requests_printTypeXerox, tv_requests_SideXerox, tv_requests_numberOfpageXerox, tv_requests_numberOfCopyXerox, tv_requests_totalAmountXerox, tv_requests_paidonlineXerox, tv_requests_codeofflineXerox, tv_requests_NoteXerox;

        LinearLayout ll_xerox, ll_print;// ll_paidOnline_print, ll_CC_print, ll_CC_xerox, ll_po_xerox;
        CardView cv_request;

        ImageView iv_download, img_Xeroxsend, img_xeroxDone;

        public MyViewHodlderForRequestFragment(@NonNull View itemView) {
            super(itemView);

            ll_print = itemView.findViewById(R.id.ll_print);
            ll_xerox = itemView.findViewById(R.id.ll_xerox);
//            ll_paidOnline_print = itemView.findViewById(R.id.ll_paidOnline_print);
//            ll_CC_print = itemView.findViewById(R.id.ll_CC_print);
//            ll_po_xerox = itemView.findViewById(R.id.ll_po_xerox);
//            ll_CC_xerox = itemView.findViewById(R.id.ll_CC_xerox);


            cv_request = itemView.findViewById(R.id.cv_request);

            img_xeroxDone = itemView.findViewById(R.id.img_xeroxDone);
            iv_download = itemView.findViewById(R.id.iv_download);
            img_Xeroxsend = itemView.findViewById(R.id.img_Xeroxsend);
            img_EmergencyPending = itemView.findViewById(R.id.img_EmergencyPending);
            img_EmergencyPending.setVisibility(View.VISIBLE);

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
            btn_emergency = itemView.findViewById(R.id.btn_emergency);
            btn_emergency.setVisibility(View.GONE);

        }
    }
}


//    private void deleteFile(String s) {


//        String path = Uri.parse(url).getPath(); // "/mnt/sdcard/FileName.mp3"
//        File file = new File(new URI(path));
//
//        Toast.makeText(getActivity(), file.toString(), Toast.LENGTH_SHORT).show();


//        new File(Uri.parse(url).toString()).delete();

//        File dir = new File(Environment.getExternalStorageDirectory()+"Xerox Anywhere");
//
//        if (dir.isDirectory()) {
//            Toast.makeText(getActivity(), "ds", Toast.LENGTH_SHORT).show();
//            String[] children = dir.list();
//            Toast.makeText(getActivity(), children.toString(), Toast.LENGTH_SHORT).show();
//            for (int i = 0; i < children.length; i++) {
//                try {
//                    if(children[i].equals(mNum + "_" + page + "_" + color + ".pdf")){
//                        new File(dir, children[i]).delete();}
//                }
//                catch (Exception e){
//                    Toast.makeText(getActivity(), "First downloa", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }
//        else{
//            Toast.makeText(getActivity(), "dfafasd", Toast.LENGTH_SHORT).show();
//        }

//        Toast.makeText(getActivity(), "file del", Toast.LENGTH_SHORT).show();
//    }


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


//package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;
//
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.nfc.tech.TagTechnology;
//import android.os.Build;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.cardview.widget.CardView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//
//import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.RequestsEntity;
//import com.dhruvi.dhruvisonani.adminsidexa.R;
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//
//import static com.dhruvi.dhruvisonani.adminsidexa.Activity.HomeActivity.MyPREFERENCES;
//public class PendingFragment extends Fragment {
//
//    RecyclerView rv_pendingList;
//
//    String username;
//    Query query;
//    FirebaseRecyclerOptions<RequestsEntity> options;
//    FirebaseRecyclerAdapter<RequestsEntity, MyViewHodlderForPendingFragment> adapterFirebase;
//    RecyclerView.LayoutManager layoutManager;
//
//    DatabaseReference databaseReference;
//    RequestsEntity requestsEntity;
//
//    public PendingFragment() {}
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_pending2, container, false);
//        declaration(view);
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//
//    private void declaration(View view){
//        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        username = sharedpreferences.getString("uname", "");
//        rv_pendingList = view.findViewById(R.id.rv_pendingList);
//        requestsEntity = new RequestsEntity();
//
//        pendingRequest();
//    }
//
//
//    private void pendingRequest() {
//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//        String formattedDate = df.format(c);
//
//        query = FirebaseDatabase.getInstance().getReference().child("Accepted_Request").child(username).child(formattedDate);
//        options = new FirebaseRecyclerOptions.Builder<RequestsEntity>().setQuery(query, RequestsEntity.class).build();
//        adapterFirebase = new FirebaseRecyclerAdapter<RequestsEntity, MyViewHodlderForPendingFragment>(options) {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            protected void onBindViewHolder(@NonNull final MyViewHodlderForPendingFragment holder1, final int i, final RequestsEntity entity) {
//                String str_PageNum,str_color,str_mobile;
////                try { //print
//                    holder1.ll_xerox_pending.setVisibility(View.VISIBLE);
//                    holder1.ll_print_pending.setVisibility(View.GONE);
//                    holder1.tv_pending_pageNumber.setText(entity.getStr_numpage());
//                    holder1.cv_pending.setCardBackgroundColor(Color.parseColor("#81D4FA"));
//                    holder1.tv_mNum_pending.setText(entity.getStr_MobileNum());
//
////                }catch (Exception e){
////                    holder1.ll_print_pending.setVisibility(View.GONE);
////                    holder1.ll_xerox_pending.setVisibility(View.VISIBLE);
////
////                    holder1.tv_pending_pagesXerox.setText(entity.getStr_page());
////                    holder1.cv_pending.setCardBackgroundColor(Color.parseColor("#E0F7FA"));
////                    holder1.tv_pending_mobileNumberxerox.setText(entity.getStr_MobileNum());
//
////                }
//            }
//            @NonNull
//            @Override
//            public MyViewHodlderForPendingFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_for_pending_request, null);
//                return new MyViewHodlderForPendingFragment(v);
//            }
//        };
//        adapterFirebase.startListening();
//        rv_pendingList.setAdapter(adapterFirebase);
//
//    }
//
//    private class MyViewHodlderForPendingFragment extends RecyclerView.ViewHolder {
//        TextView tv_mNum_pending,tv_time_pending,tv_pending_pageNumber;
//        TextView tv_pending_timeXerox,tv_pending_pagesXerox,tv_pending_mobileNumberxerox;
//                ImageView img_xeroxDone,iv_download,img_Xeroxsend;
//                CardView cv_pending;
//                LinearLayout ll_print_pending,ll_xerox_pending;
//        public MyViewHodlderForPendingFragment(@NonNull View itemView) {
//            super(itemView);
//
//            ll_xerox_pending = itemView.findViewById(R.id.ll_xerox_pending);
//            ll_print_pending = itemView.findViewById(R.id.ll_print_pending);
//
//            cv_pending = itemView.findViewById(R.id.cv_pending);
//
//            tv_pending_pageNumber = itemView.findViewById(R.id.tv_pending_pageNumber);
//            tv_mNum_pending = itemView.findViewById(R.id.tv_pending_mobileNumber);
//            tv_time_pending = itemView.findViewById(R.id.tv_pending_time);
//
//
//            tv_pending_mobileNumberxerox = itemView.findViewById(R.id.tv_pending_mobileNumberxerox);
//            tv_pending_pagesXerox = itemView.findViewById(R.id.tv_pending_pagesXerox);
//            tv_pending_timeXerox = itemView.findViewById(R.id.tv_pending_timeXerox);
//
//            img_xeroxDone = itemView.findViewById(R.id.img_xeroxDone);
////
//        }
//    }
//}
////
////    private ArrayList<PendingListEntity> getList(){
////        ArrayList<PendingListEntity> entities = new ArrayList<>();
////        PendingListEntity m;
////        int[] mobile_num={1234567890,1478523690};
////        int[] time={15 ,17 };
////
////        for(int i=0;i<mobile_num.length;i++){
////            m = new PendingListEntity();
////            m.setTv_mNum_pending(mobile_num[i]);
////            m.setTv_time_pending(time[i]);
////            entities.add(m);
////        }
////        return entities;
////    }
//
//
//
////PendingListAdaper
//
////package com.dhruvi.dhruvisonani.adminsidexa.Activity.Adapter;
////
////import android.content.Context;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.ImageView;
////import android.widget.TextView;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.PendingListEntity;
////import com.dhruvi.dhruvisonani.adminsidexa.R;
////
////import java.util.ArrayList;
////
////public class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.MyHolder> {
////
////
////    ArrayList<PendingListEntity> mDataset;
////    Context c;
////
////    public PendingListAdapter(Context c,ArrayList<PendingListEntity> mDataset) {
////        this.mDataset = mDataset;
////        this.c = c;
////    }
////
////    @Override
////    public MyHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
////        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_for_pending_request,null);
////        return new MyHolder(view);
////    }
////
////    @Override
////    public void onBindViewHolder(MyHolder myHolder, final int i) {
////        myHolder.tv_time_pending.setText(String.valueOf(mDataset.get(i).getTv_time_pending()));
////        myHolder.tv_mNum_pending.setText(String.valueOf(mDataset.get(i).getTv_mNum_pending()));
////        myHolder.img_xeroxDone.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Toast.makeText(c, "completed Request : "+String.valueOf(i+1), Toast.LENGTH_SHORT).show();
////            }
////        });
////    }
////
////    @Override
////    public int getItemCount() {
////        return mDataset.size();
////    }
////
////    public class MyHolder extends RecyclerView.ViewHolder {
////        //        TextView
////        TextView tv_mNum_pending,tv_time_pending;
////        ImageView img_xeroxDone,iv_download,img_Xeroxsend;
////        public MyHolder(@NonNull View itemView) {
////
////            super(itemView);
////            tv_mNum_pending = itemView.findViewById(R.id.tv_mNum_pending);
////            tv_time_pending = itemView.findViewById(R.id.tv_time_pending);
////            img_xeroxDone = itemView.findViewById(R.id.img_xeroxDone);
////
////        }
////    }
////}

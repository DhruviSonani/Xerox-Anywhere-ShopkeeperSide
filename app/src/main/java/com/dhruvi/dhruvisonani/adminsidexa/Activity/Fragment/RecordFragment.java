package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.RecordTillDateEntity;
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

import static com.dhruvi.dhruvisonani.adminsidexa.Activity.HomeActivity.MyPREFERENCES;

public class RecordFragment extends Fragment {

    CalendarView calendar_Record;
    TextView tv_RecordDate, tv_RecordTotal;
    RecyclerView rv_recordTillDate;
    RecyclerView.LayoutManager layoutManager;

    RecordTillDateEntity recordTillDateEntity;
    Query query;
    DocumentReference databaseReference;
    FirebaseRecyclerOptions<RequestsEntity> options;
    FirebaseRecyclerAdapter<RequestsEntity, MyViewHodlderForRecordFragment> adapterFirebase;

    int print, xeox;
    String username, a;

    public RecordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record2, container, false);
        declaration(view);
        initialization();
        return view;
    }

    private void declaration(View view) {
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        username = sharedpreferences.getString("uname", "");

        calendar_Record = view.findViewById(R.id.calendar_Record);
        tv_RecordDate = view.findViewById(R.id.tv_RecordDate);
        tv_RecordTotal = view.findViewById(R.id.tv_RecordTotal);
        rv_recordTillDate = view.findViewById(R.id.rv_recordTillDate);
        recordTillDateEntity = new RecordTillDateEntity();
    }

    private void initialization() {
        Date currentDate = Calendar.getInstance().getTime();//format(calendar_Record.getDate());
        final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        tv_RecordDate.setText(df.format(currentDate));

        recordTillDate(df.format(currentDate));
        calendar_Record.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String Date1 = null;
                if (month < 9) {
                    if (dayOfMonth < 10) {
                        Date1 = "0" + dayOfMonth + "-0" + (month + 1) + "-" + year;
                    } else {
                        Date1 = dayOfMonth + "-0" + (month + 1) + "-" + year;
                    }
                } else {
                    if (dayOfMonth < 10) {
                        Date1 = "0" + dayOfMonth + "-" + (month + 1) + "-" + year;
                    } else {
                        Date1 = dayOfMonth + "-" + (month + 1) + "-" + year;
                    }
                }

                tv_RecordDate.setText(Date1);
                print = 0;
                xeox = 0;
                recordTillDate(Date1);
            }
        });
    }

    private void recordTillDate(String s) {

        int childCount;
        try {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Accepted_Request").child(username).child(s);
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tv_RecordTotal.setText("Total : " + String.valueOf(dataSnapshot.getChildrenCount()));

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });

            query = FirebaseDatabase.getInstance().getReference().child("Accepted_Request").child(username).child(s);
            options = new FirebaseRecyclerOptions.Builder<RequestsEntity>().setQuery(query, RequestsEntity.class).build();
            adapterFirebase = new FirebaseRecyclerAdapter<RequestsEntity, MyViewHodlderForRecordFragment>(options) {

                @NonNull
                @Override
                public MyViewHodlderForRecordFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_for_record_till_date, null);
                    return new MyViewHodlderForRecordFragment(v);
                }

                @Override
                protected void onBindViewHolder(@NonNull final MyViewHodlderForRecordFragment holder1, int i, @NonNull RequestsEntity entity) {

                    try {
                        holder1.ll_bound.setVisibility(View.VISIBLE);
                        holder1.cv_recordTillDate.setCardBackgroundColor(Color.parseColor("#81D4FA"));
                        holder1.tv_Record_mobileNumber.setText(entity.getStr_MobileNum());
                        holder1.tv_Record_Amount.setText(entity.getStr_total());
                        holder1.tv_Record_pageNumber.setText(entity.getStr_numpage());
                        holder1.tv_Record_Bound.setText(entity.getStr_rb_bound());
                        databaseReference = FirebaseFirestore.getInstance().collection("Users").document(holder1.tv_Record_mobileNumber.getText().toString());

                        databaseReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    holder1.tv_Record_Name.setText(snapshot.getString("Full Name"));
                                }
                            }
                        });


                        if (!entity.getStr_bw_count().equals("0") && !entity.getStr_color_count().equals("0")) {
                            holder1.tv_Record_NumOfCopy.setText(entity.getStr_bw_count() + " Bw and " + entity.getStr_color_count() + " Color");
                        } else if (entity.getStr_color_count().equals("0")) {
                            holder1.tv_Record_NumOfCopy.setText(entity.getStr_bw_count() + " BW");

                        } else {
                            holder1.tv_Record_NumOfCopy.setText(entity.getStr_color_count() + " Color");
                        }
                        print++;
                    } catch (Exception e) {
                        try {
                            holder1.ll_bound.setVisibility(View.GONE);
                            holder1.cv_recordTillDate.setCardBackgroundColor(Color.parseColor("#E0F7FA"));
                            holder1.tv_Record_mobileNumber.setText(entity.getStr_MobileNum());
                            holder1.tv_Record_pageNumber.setText(entity.getStr_page());
                            holder1.tv_Record_Amount.setText(entity.getStr_total());
                            databaseReference = FirebaseFirestore.getInstance().collection("Users").document(holder1.tv_Record_mobileNumber.getText().toString());

                            databaseReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        return;
                                    }
                                    if (snapshot != null && snapshot.exists()) {
                                        holder1.tv_Record_Name.setText(snapshot.getString("Full Name"));
                                    }
                                }
                            });


                            if (!entity.getStr_bw().equals("0") && !entity.getStr_color().equals("0")) {

                                holder1.tv_Record_NumOfCopy.setText(entity.getStr_bw() + " Bw and " + entity.getStr_color() + " Color");
                            } else if (entity.getStr_color().equals("0")) {
                                holder1.tv_Record_NumOfCopy.setText(entity.getStr_bw() + " BW");

                            } else {
                                holder1.tv_Record_NumOfCopy.setText(entity.getStr_color() + " Color");
                            }
                            xeox++;
                        } catch (Exception ee) {
                            holder1.lv_record_show.setVisibility(View.GONE);
                            holder1.lv_record.setVisibility(View.GONE);
                        }
                    }
                    holder1.lv_record.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder1.lv_record_show.getVisibility() == View.VISIBLE) {
                                holder1.lv_record_show.setVisibility(View.GONE);
                            } else {
                                holder1.lv_record_show.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }

            };

            adapterFirebase.startListening();
            rv_recordTillDate.setAdapter(adapterFirebase);

        } catch (Exception e) {
        }
    }

//    private String getUserNAme(final String s) {
//        databaseReference = FirebaseFirestore.getInstance().collection("Users").document(s);
//
//        databaseReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    return;
//                }
//                if (snapshot != null && snapshot.exists()) {
//                    a = snapshot.getString("Full Name");
////                    Toast.makeText(getActivity(), a, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//        });
//        return a;
//    }

    private class MyViewHodlderForRecordFragment extends RecyclerView.ViewHolder {
        TextView tv_Record_Amount, tv_Record_Name, tv_Record_mobileNumber;
        TextView tv_Record_pageNumber, tv_Record_NumOfCopy, tv_Record_Bound;
        CardView cv_recordTillDate;
        LinearLayout lv_record_show, lv_record, ll_bound;

        public MyViewHodlderForRecordFragment(@NonNull View itemView) {
            super(itemView);

            lv_record_show = itemView.findViewById(R.id.lv_record_show);
            ll_bound = itemView.findViewById(R.id.ll_bound);
            lv_record = itemView.findViewById(R.id.lv_record);

            cv_recordTillDate = itemView.findViewById(R.id.cv_recordTillDate);
//            tv_Record_onlinepaid = itemView.findViewById(R.id.tv_Record_onlinepaid);
            tv_Record_Amount = itemView.findViewById(R.id.tv_Record_Amount);
            tv_Record_mobileNumber = itemView.findViewById(R.id.tv_Record_mobileNumber);
            tv_Record_Name = itemView.findViewById(R.id.tv_Record_Name);

            tv_Record_pageNumber = itemView.findViewById(R.id.tv_Record_pageNumber);
            tv_Record_NumOfCopy = itemView.findViewById(R.id.tv_Record_NumOfCopy);
            tv_Record_Bound = itemView.findViewById(R.id.tv_Record_Bound);


        }
    }
}


//
//    private ArrayList<RecordTillDateEntity> getMyList() {
//        ArrayList<RecordTillDateEntity> entities = new ArrayList<>();
//        String[] name= {"Dhruvi"};
//        String[] num = {"7698071241"};
//        int[] amount = {70};
//        int[] op = {15};
//
//        RecordTillDateEntity m;
//        for (int i=0;i<10;i++){
//            m = new RecordTillDateEntity();
//            m.setInt_amount(amount[0]);
//            m.setInt_mobileNum(num[0]);
//            m.setStr_name(name[0]);
//            m.setInt_onlinePaid(op[0]);
//            entities.add(m);
//        }
//        return entities;
//    }
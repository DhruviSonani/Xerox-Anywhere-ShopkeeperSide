package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.AddBookEntity;
import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.AddNewBookEntity;
import com.dhruvi.dhruvisonani.adminsidexa.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.dhruvi.dhruvisonani.adminsidexa.Activity.HomeActivity.MyPREFERENCES;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookQueueFragment extends Fragment {

    RecyclerView rv_BookQueue;
    ArrayList<AddBookEntity> entities;
    FirebaseRecyclerOptions<AddBookEntity> options;
    FirebaseRecyclerAdapter<AddBookEntity, MyViewHodlderForBookQueue> adapterFirebase;
    DatabaseReference databaseReference;
    AddNewBookEntity entity;

    String username;

    public BookQueueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_book_queue, container, false);

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        username = sharedpreferences.getString("uname", "");
        databaseReference =  FirebaseDatabase.getInstance().getReference().child("Accepted_Request_Book");
        rv_BookQueue = view.findViewById(R.id.rv_BookQueue);
        entity = new AddNewBookEntity();

        loaddata();
        return view;
    }

    private void loaddata() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Accepted_Request_Book").child(username);
        options = new FirebaseRecyclerOptions.Builder<AddBookEntity>().setQuery(query, AddBookEntity.class).build();

        adapterFirebase = new FirebaseRecyclerAdapter<AddBookEntity, MyViewHodlderForBookQueue>(options) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHodlderForBookQueue holder1, final int i, final AddBookEntity addBookEntity) {
                holder1.tv_booknameQueue.setText(addBookEntity.getBookName());
                holder1.tv_DateQueue.setText(addBookEntity.getStr_date());
                holder1.tv_mNumQueue.setText(addBookEntity.getStr_MobileNum());
                holder1.tv_mNameQueue.setText(addBookEntity.getFullname());
                holder1.tv_bookPriceQueue.setText(addBookEntity.getBookPrice());
                Picasso.with(getContext()).load(addBookEntity.getStr_imageId()).into(holder1.img_bookQueue);

            }

            @NonNull
            @Override
            public MyViewHodlderForBookQueue onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_for_book_queue, null);
                return new MyViewHodlderForBookQueue(v);
            }
        };
        adapterFirebase.startListening();
        rv_BookQueue.setAdapter(adapterFirebase);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private class MyViewHodlderForBookQueue extends RecyclerView.ViewHolder {

        ImageView img_bookQueue;
        TextView tv_booknameQueue,tv_bookPriceQueue,tv_mNumQueue,tv_mNameQueue,tv_DateQueue;
        public MyViewHodlderForBookQueue(@NonNull View itemView) {
            super(itemView);

            img_bookQueue = itemView.findViewById(R.id.img_bookQueue);
            tv_booknameQueue = itemView.findViewById(R.id.tv_booknameQueue);
            tv_bookPriceQueue = itemView.findViewById(R.id.tv_bookPriceQueue);
            tv_mNameQueue = itemView.findViewById(R.id.tv_mNameQueue);
            tv_mNumQueue = itemView.findViewById(R.id.tv_mNumQueue);
            tv_DateQueue = itemView.findViewById(R.id.tv_DateQueue);
        }
    }
}

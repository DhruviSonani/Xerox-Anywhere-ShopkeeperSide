package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;

// AddBookAdapter is not in use as i've created it in this fragment code

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.AddBookEntity;
import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.AddNewBookEntity;
import com.dhruvi.dhruvisonani.adminsidexa.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import static com.dhruvi.dhruvisonani.adminsidexa.Activity.HomeActivity.MyPREFERENCES;

public class AddBookFragment extends Fragment implements View.OnClickListener {

    RecyclerView rv_addBook;
    ArrayList<AddBookEntity> entities;
    Button btn_addBook;
    Fragment fragment = null;

    FirebaseRecyclerOptions<AddBookEntity> options;
    FirebaseRecyclerAdapter<AddBookEntity, MyViewHodlderForAddBook> adapterFirebase;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;
    AddNewBookEntity addNewBookEntity;

    String username;
    ProgressBar progressBar;

    public AddBookFragment() { }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_book, container, false);
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        username = sharedpreferences.getString("uname", "");
        databaseReference =  FirebaseDatabase.getInstance().getReference().child("AddNewBookEntity");

        progressBar = new ProgressBar(getActivity());
        rv_addBook = view.findViewById(R.id.rv_addBook);
        btn_addBook = view.findViewById(R.id.btn_addBook);
        layoutManager = new LinearLayoutManager(getActivity());
        addNewBookEntity = new AddNewBookEntity();

        rv_addBook.setLayoutManager(layoutManager);
        loadData();
        btn_addBook.setOnClickListener(this);
        return view;
    }

    private void loadData() {

//        Query query = FirebaseDatabase.getInstance().getReference().child("AddNewBookEntity").orderByChild("currentFirebaseUser").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Query query = FirebaseDatabase.getInstance().getReference().child("AddNewBookEntity").child(username);

        options = new FirebaseRecyclerOptions.Builder<AddBookEntity>().setQuery(query, AddBookEntity.class).build();
        adapterFirebase = new FirebaseRecyclerAdapter<AddBookEntity, MyViewHodlderForAddBook>(options) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHodlderForAddBook holder1, final int i, final AddBookEntity addBookEntity) {
                holder1.tv_bookPrice.setText(String.valueOf(addBookEntity.getStr_bookPrice()));
                holder1.tv_bookname.setText(addBookEntity.getStr_bookName());
                Picasso.with(getContext()).load(addBookEntity.getStr_imageId()).into(holder1.img_addBook);

                holder1.btn_book_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UpdateData(username, holder1.tv_bookname.getText().toString(), holder1.tv_bookPrice.getText().toString());
                    }
                });
                holder1.btn_book_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletedata(username, holder1.tv_bookname.getText().toString(), holder1.tv_bookPrice.getText().toString());

                    }
                });
            }

            @NonNull
            @Override
            public MyViewHodlderForAddBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_for_add_book, null);
                return new MyViewHodlderForAddBook(v);
            }
        };
        adapterFirebase.startListening();
        rv_addBook.setAdapter(adapterFirebase);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void UpdateData(final String username, final String bookname, final String bookprice) {

        final String str_NEW_Records_Key = databaseReference.child(username).child(username + "_" + bookname + "_").getKey();
//        Toast.makeText(getActivity(), str_NEW_Records_Key, Toast.LENGTH_SHORT).show();

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog_update, null);
        dialog.setContentView(view);

        Button edit = view.findViewById(R.id.btn_cancel_popup_update);
        Button update = view.findViewById(R.id.btn_update_data);
        final EditText et_update_price = view.findViewById(R.id.et_update_price);
        dialog.setCanceledOnTouchOutside(false);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = Integer.valueOf(et_update_price.getText().toString());

                HashMap hashMap = new HashMap();
                hashMap.put("str_bookPrice", i);
                addNewBookEntity.setStr_bookPrice(Integer.parseInt(et_update_price.getText().toString().trim()));
                databaseReference.child(username).child(str_NEW_Records_Key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Your data has been updated", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        dialog.show();

    };

    public void deletedata(final String username, final String bookname, final String bookprice) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);

        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null);
        dialog.setContentView(view);

        Button edit = view.findViewById(R.id.btn_cancel_popup);
        Button delete = view.findViewById(R.id.btn_delete_data);
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
                String str_NEW_Records_Key = databaseReference.child(username).child(username + "_" + bookname + "_").getKey();
//                Toast.makeText(getActivity(), str_NEW_Records_Key, Toast.LENGTH_SHORT).show();

                databaseReference.child(username).child(str_NEW_Records_Key).removeValue();
                StorageReference StorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(AddNewBookFragment.url.toString());;
                StorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {}
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "ffd", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
                Toast.makeText(getActivity(), "Your data has been deleted", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    };
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_addBook:

                fragment = new AddNewBookFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
//                Toast.makeText(getActivity(), "Opening", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private class MyViewHodlderForAddBook extends RecyclerView.ViewHolder {

        public TextView tv_bookname, tv_bookPrice;
        public ImageView img_addBook;
        Button btn_book_edit, btn_book_delete;

        public MyViewHodlderForAddBook(@NonNull View itemView) {
            super(itemView);
            tv_bookname = itemView.findViewById(R.id.tv_bookname);
            tv_bookPrice = itemView.findViewById(R.id.tv_bookPrice);
            img_addBook = itemView.findViewById(R.id.img_addBook12);
            btn_book_edit = itemView.findViewById(R.id.btn_book_edit);
            btn_book_delete = itemView.findViewById(R.id.btn_book_delete);

        }
    }

}


//        entities = new ArrayList<AddBookEntity>();
//
//
//        final ArrayList<AddBookEntity> entities = new ArrayList<>();
//        adapter = new AddBookAdapter();
//        rv_addBook.setAdapter(adapter);

//                adapter = new AddBookAdapter(getActivity(), getMyList());
//                        rv_addBook.setAdapter(adapter);


//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
////                    if(FirebaseAuth.getInstance().getCurrentUser().getUid() == ) {
//                        AddBookEntity addBookEntity = dataSnapshot1.getValue(AddBookEntity.class);
//                        entities.add(addBookEntity);
//
//                        Log.i("kjhhgsfjhsfd", "onDataChange: " + addBookEntity.getTv_bookPrice());
//
//                        adapter = new AddBookAdapter(getActivity(), entities);
//                        rv_addBook.setAdapter(adapter);
////                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getActivity(), "Oopss", Toast.LENGTH_SHORT).show();
//            }
//        });


//                    AddBookEntity addBookEntity = new AddBookEntity();
//                    addBookEntity.setTv_bookPrice(dataSnapshot1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(AddBookEntity.class).getTv_bookPrice());
//                    ArrayList<AddBookEntity> arrayList = new ArrayList<AddBookEntity>();
//                    arrayList.add(addBookEntity);
//                    adapter = new AddBookAdapter(getActivity(),arrayList);
//                    rv_addBook.setAdapter(adapter);

//    @Override
//    public void onStart() {
//        super.onStart();
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
////                    if(FirebaseAuth.getInstance().getCurrentUser().getUid() == ) {
//                        AddBookEntity addBookEntity = dataSnapshot1.getValue(AddBookEntity.class);
//                        entities.add(addBookEntity);
//
//                        Log.i("kjhhgsfjhsfd", "onDataChange: " + addBookEntity.getTv_bookPrice());
//
//                        adapter = new AddBookAdapter(getActivity(), entities);
//                        rv_addBook.setAdapter(adapter);
////                    }
//
//                }
//            }

//    @Override
//    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//    }
//});
//        }


//    private ArrayList<AddBookEntity> getMyList(){
//        ArrayList<AddBookEntity> entities = new ArrayList<>();
//        AddBookEntity m;
//        int[] image = {R.drawable.b1,R.drawable.b2,R.drawable.b3,R.drawable.b4};
//        int[] price = {150,210,412,154};
//        String[] name = {"How Successful People Think","Motivation","8 Mintes in Morning","Self Motivation"};
//
//        for(int i=0;i<image.length;i++){
//            m = new AddBookEntity();
//            m.setImg_addBook(image[i]);
//            m.setTv_bookname(name[i]);
//            m.setTv_bookPrice(price[i]);
//            entities.add(m);
//        }
//
//
//        return entities;
//
//    }


//AddBookAdapter.java

//package com.dhruvi.dhruvisonani.adminsidexa.Activity.Adapter;
//
//        import android.content.Context;
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.Button;
//        import android.widget.ImageView;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import androidx.annotation.NonNull;
//        import androidx.recyclerview.widget.RecyclerView;
//
//        import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.AddBookEntity;
//        import com.dhruvi.dhruvisonani.adminsidexa.R;
//        import com.squareup.picasso.Picasso;
//
//        import java.util.ArrayList;
//
//public class AddBookAdapter extends RecyclerView.Adapter<com.dhruvi.dhruvisonani.adminsidexa.Activity.Adapter.AddBookAdapter.MyHolder> {
//    ArrayList<AddBookEntity> mDataset = new ArrayList<AddBookEntity>();
//    Context c;
//
//    public AddBookAdapter(){
//
//    }
//    public AddBookAdapter(Context c,ArrayList<AddBookEntity> mDataset) {
//        this.mDataset = mDataset;
////        Toast.makeText(c, String.valueOf(mDataset.get(0).getTv_bookname()), Toast.LENGTH_SHORT).show();
//        this.c = c;
//    }
//
//    @Override
//    public com.dhruvi.dhruvisonani.adminsidexa.Activity.Adapter.AddBookAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//        return new com.dhruvi.dhruvisonani.adminsidexa.Activity.Adapter.AddBookAdapter.MyHolder(LayoutInflater.from(c).inflate(R.layout.data_for_add_book,null));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull com.dhruvi.dhruvisonani.adminsidexa.Activity.Adapter.AddBookAdapter.MyHolder myHolder, final int i) {
//        Log.i("mjhfjehgefjghej", "onBindViewHolder: "+mDataset.get(i).getStr_bookPrice());
//
//        myHolder.tv_bookPrice.setText(String.valueOf(mDataset.get(i).getStr_bookName()));
//        myHolder.tv_bookname.setText(String.valueOf(mDataset.get(i).getStr_bookName()));
//        myHolder.img_addBook.setImageResource(Integer.valueOf(mDataset.get(i).getStr_imageId()));
////        Picasso.get().load(mDataset.get(i).getImg_addBook()).into(myHolder.img_addBook);
//
//        myHolder.btn_book_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(c, "Edit : "+(i+1), Toast.LENGTH_SHORT).show();
//            }
//        });
//        myHolder.btn_book_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(c, "Delete : "+(i+1), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mDataset.size();
//    }
//
//    public class MyHolder extends RecyclerView.ViewHolder {
//        TextView tv_bookname,tv_bookPrice;
//        ImageView img_addBook;
//        //TextView tv_bookAvailable;
//        Button btn_book_edit,btn_book_delete;
//        public MyHolder(@NonNull View itemView) {
//            super(itemView);
//            // tv_bookAvailable = itemView.findViewById(R.id.tv_bookAvailable);
//            tv_bookname = itemView.findViewById(R.id.tv_bookname);
//            tv_bookPrice = itemView.findViewById(R.id.tv_bookPrice);
//            img_addBook = itemView.findViewById(R.id.img_addBook);
//            btn_book_delete = itemView.findViewById(R.id.btn_book_delete);
//            btn_book_edit = itemView.findViewById(R.id.btn_book_edit);
//        }
//    }
//}




//    private void loadData() {
//        entities = new ArrayList<AddBookEntity>();
//
//        databaseReference.orderByChild("currentFirebaseUser").equalTo(currentuser).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                AddBookEntity addBookEntity = dataSnapshot.getValue(AddBookEntity.class);
//                        entities.add(addBookEntity);
////
//                        Log.i("kjhhgsfjhsfd", "onDataChange: " + addBookEntity.getTv_bookPrice());
////
//                        adapter = new AddBookAdapter(getActivity(), entities);
//                        rv_addBook.setAdapter(adapter);
//////
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

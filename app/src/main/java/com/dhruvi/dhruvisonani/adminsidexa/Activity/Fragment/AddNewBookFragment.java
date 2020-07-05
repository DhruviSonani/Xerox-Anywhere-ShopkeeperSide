package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity.AddNewBookEntity;
import com.dhruvi.dhruvisonani.adminsidexa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;
import static com.dhruvi.dhruvisonani.adminsidexa.Activity.HomeActivity.MyPREFERENCES;

public class AddNewBookFragment extends Fragment implements View.OnClickListener {

    public static Uri url;
    String currentFirebaseUser;

    Button btn_addNewBook, btn_cancelNewBook;
    EditText et_bookPrice, et_bookName;
    ImageView img_addBook;
    ProgressBar progressBar;
    public Uri mImageUri;


    //Entity
    AddNewBookEntity addNewBookEntity;

    //firebase
    private StorageTask uploadTask;
    StorageReference StorageRef;
    DatabaseReference dataReferencef;

    private static final int STORAGE_PERMISSION_CODE = 1;

    private static String[] PERMISSION_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public AddNewBookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_book, container, false);
        declaration(view);
        initialization();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void RequestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    private void declaration(View v) {
        StorageRef = FirebaseStorage.getInstance().getReference().child("Book_Images");

        img_addBook = v.findViewById(R.id.img_addBook);
        et_bookName = v.findViewById(R.id.et_bookName);
        et_bookPrice = v.findViewById(R.id.et_bookPrice);
        btn_addNewBook = v.findViewById(R.id.btn_addNewBook);
        btn_cancelNewBook = v.findViewById(R.id.btn_cancelNewBook);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        addNewBookEntity = new AddNewBookEntity();
        progressBar = new ProgressBar(getActivity());
    }

    private void initialization() {
        //for run time permission to read storage
        img_addBook.setOnClickListener(this);
        btn_cancelNewBook.setOnClickListener(this);
        btn_addNewBook.setOnClickListener(this);
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public void upload() {
        if (mImageUri != null) {

            btn_cancelNewBook.setVisibility(View.GONE);
            SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String username = sharedpreferences.getString("uname", "");

            dataReferencef = FirebaseDatabase.getInstance().getReference().child("AddNewBookEntity").child(username);
//            final String key = dataReferencef.push().getKey();
            final StorageReference ref = StorageRef.child(username).child(username + "_" + et_bookName.getText().toString().trim() + "_" + et_bookPrice.getText().toString() + ".jpg");

            uploadTask = ref.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uri.isComplete()) ;

                    url = uri.getResult();

                    addNewBookEntity.setStr_bookName(et_bookName.getText().toString().trim());
                    addNewBookEntity.setStr_imageId(url.toString()); //str_imageId
                    addNewBookEntity.setCurrentFirebaseUser(currentFirebaseUser.toString().trim());
                    addNewBookEntity.setStr_bookPrice(Integer.parseInt(et_bookPrice.getText().toString().trim()));
                    dataReferencef.child(username + "_" + et_bookName.getText().toString().trim() + "_").setValue(addNewBookEntity);
                    Toast.makeText(getActivity(), "Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
                    et_bookName.setText("");
                    et_bookPrice.setText("");
                    img_addBook.setImageResource(R.drawable.ic_launcher_foreground);
                    mImageUri = null;
                    btn_cancelNewBook.setVisibility(View.VISIBLE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getActivity(), "Error in Upload", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancelNewBook:
                if (!TextUtils.isEmpty(et_bookPrice.getText().toString()) || !TextUtils.isEmpty(et_bookName.getText().toString()) || mImageUri != null) {
                    Toast.makeText(getActivity(), "Book Adding Canceled", Toast.LENGTH_SHORT).show();
                    et_bookName.setText("");
                    et_bookPrice.setText("");
                    img_addBook.setImageResource(R.drawable.ic_launcher_foreground);
                    mImageUri = null;
                }
                else{
                    Toast.makeText(getActivity(), "No data to cancel !!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_addNewBook:
                if (!TextUtils.isEmpty(et_bookPrice.getText().toString()) && !TextUtils.isEmpty(et_bookName.getText().toString()) && mImageUri != null) {
                    if (uploadTask != null && uploadTask.isInProgress()) {
                        Toast.makeText(getActivity(), "Uploading", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Upload in progress, it'll take a moment", Toast.LENGTH_LONG).show();
                        upload();
                    }
                } else {
                    if (mImageUri == null) {
                        Toast.makeText(getActivity(), "Please set Book Image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (et_bookName.getText().toString().equals("")) {
                        et_bookName.setError("Enter Book Name");
                        et_bookName.requestFocus();
                        return;
                    }
                    if (et_bookPrice.getText().toString().equals("")) {
                        et_bookPrice.setError("Enter Book Price (INR)");
                        et_bookPrice.requestFocus();
                        return;
                    }

                }
                break;
            case R.id.img_addBook:
                openFileChooser();

                break;
        }
    }

    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(getActivity()).load(mImageUri).into(img_addBook);
            img_addBook.setImageURI(mImageUri);
        }
    }
}


//        final String key = dataReferencef.push().getKey();
//        StorageRef.child(key+".jpg").putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                StorageRef.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        HashMap hashMap = new HashMap();
//                        hashMap.put("Book Name",et_bookName.getText().toString());
//                        hashMap.put("Book Price",et_bookPrice.getText().toString());
//                        hashMap.put("Image",mImageUri.toString());
//                        dataReferencef.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(getActivity(), "Successfull", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getActivity(), "Uploading Failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//

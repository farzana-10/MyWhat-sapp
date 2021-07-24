package com.example.mywhatsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class setprofile extends AppCompatActivity {

    private CardView mgetuserimage;
    private ImageView mgetuserimageinimageview;
    private static int PICK_IMAGE=123;
    private Uri imagepath;
    private EditText mgetusername;
    private android.widget.Button msaveprofile;
    private FirebaseAuth firebaseauth;
    private String name;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String ImageUriAccessToken;
    private FirebaseFirestore firebaseFirestore;
    ProgressBar mprogressbarofsetprofile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setprofile);

        firebaseauth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mgetusername=findViewById(R.id.getusername);
        mgetuserimage=findViewById(R.id.getuserimage);
        mgetuserimageinimageview=findViewById(R.id.getuserimageinimageview);
        msaveprofile=findViewById(R.id.saveprofile);
        mprogressbarofsetprofile=findViewById(R.id.progressbarofsaveprofile);

        mgetuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        msaveprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=mgetusername.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "NAME IS EMPTY", Toast.LENGTH_SHORT).show();

                }
                else if(imagepath==null){
                    Toast.makeText(getApplicationContext(), "IMAGE IS EMPTY", Toast.LENGTH_SHORT).show();
                }
                else{
                    mprogressbarofsetprofile.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    mprogressbarofsetprofile.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(setprofile.this,chatActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

        }
    private void sendDataForNewUser(){
        sendDataToRealTimeDataBase();

    }

    private void sendDataToRealTimeDataBase(){
      name=mgetusername.getText().toString().trim();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseauth.getUid());
        //userprofile class that stores informations
        userprofile muserprofile= new userprofile(name,firebaseauth.getUid());
        databaseReference.setValue(muserprofile);
        Toast.makeText(getApplicationContext(), "User Profile Added Successfully", Toast.LENGTH_SHORT).show();
        sendImageToStorage();

    }

    private void sendImageToStorage(){
        StorageReference imageref=storageReference.child("IMAGES").child("firebaseauth.getUid()").child("Profile Pic");
        //image compression
        Bitmap bitmap=null;
        try{
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);

        }
        catch(IOException e){
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data=byteArrayOutputStream.toByteArray();

        //putting image to storage
        UploadTask uploadTask=imageref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUriAccessToken=uri.toString();
                        Toast.makeText(getApplicationContext(), "Uri get success", Toast.LENGTH_SHORT).show();
                        sendDataToFireStore();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Uri get Failed", Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(getApplicationContext(), "Image is uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image is not uploaded", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void sendDataToFireStore() {
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseauth.getUid());
        Map<String , Object> userdata=new HashMap<>();
        userdata.put("name",name);
        userdata.put("uid",firebaseauth.getUid());
        userdata.put("Image",ImageUriAccessToken);
        userdata.put("status", "Online");
        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Data on Cloud Firestore send Success", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){
          imagepath=data.getData();
          mgetuserimageinimageview.setImageURI(imagepath);

        }
    }
}
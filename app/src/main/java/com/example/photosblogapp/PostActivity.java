package com.example.photosblogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import id.zelory.compressor.Compressor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private ImageView postImage;
    private EditText postDesc;
    private Button postButton;
    private ProgressBar postProgress;
    private Toolbar postTollbar;
    private Uri postImageUri;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private String currentUserId;
    private Bitmap compressedImageFile;
    private String downloadUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);

        firestore=FirebaseFirestore.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();
        auth=FirebaseAuth.getInstance();

        currentUserId=auth.getCurrentUser().getUid();

        postImage=(ImageView)findViewById(R.id.post_image);
        postDesc=(EditText)findViewById(R.id.post_desc);
        postButton=(Button)findViewById(R.id.post_btn);
        postProgress=(ProgressBar)findViewById(R.id.post_brogress);
        postTollbar=(Toolbar) findViewById(R.id.new_post_toolbar);

        setSupportActionBar(postTollbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(PostActivity.this);

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 final String description=postDesc.getText().toString();

                if(!TextUtils.isEmpty(description)&&postImageUri!=null){

                    postProgress.setVisibility(View.VISIBLE);

                    final String randomName= UUID.randomUUID().toString();
                    final StorageReference filePath=storageReference.child("post_images").child(randomName+".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){


                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        downloadUri=uri.toString();

                                        File newImageFile=new File(postImageUri.getPath());
                                        try {
                                            compressedImageFile = new Compressor(PostActivity.this)
                                                    .setMaxHeight(200)
                                                    .setMaxWidth(200)
                                                    .setQuality(10)
                                                    .compressToBitmap(newImageFile);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] thumbsData = baos.toByteArray();

                                        final StorageReference ref=storageReference.child("post_images/thumbs").child(randomName+".jpg");
                                        UploadTask uploadTask=ref.putBytes(thumbsData);


                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {

                                                        String downloadThumbUri= uri.toString();
                                                        Map<String,Object> postMap=new HashMap<>();
                                                        postMap.put("image_uri",downloadUri);
                                                        postMap.put("thumb_uri",downloadThumbUri);
                                                        postMap.put("desc",description);
                                                        postMap.put("user_id",currentUserId);
                                                        postMap.put("times_tamp",FieldValue.serverTimestamp());

                                                        firestore.collection("posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                                if (task.isSuccessful()){

                                                                    Toast.makeText(PostActivity.this, "Post was Added", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                                                                }else {
                                                                    postProgress.setVisibility(View.INVISIBLE);
                                                                }

                                                            }
                                                        });


                                                    }
                                                });


                                            }
                                        });

                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                                    }
                                });



                            }else {

                                postProgress.setVisibility(View.INVISIBLE);
                            }


                        }
                    });



                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {

            postImageUri=result.getUri();
            postImage.setImageURI(postImageUri);

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
        }

    }




}

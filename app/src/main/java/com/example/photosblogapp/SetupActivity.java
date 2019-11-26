package com.example.photosblogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {

    private Toolbar setupToolbar;
    private boolean ischange=false;
    private CircleImageView setupImage;
    private Uri mainImageUri=null;
    private EditText setupNmae;
    private Button setupButton;
    private ProgressBar setupProgress;
    private FirebaseAuth mAuth;
    private String user_id;
    private StorageReference mStorage;
    private FirebaseFirestore mStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth=FirebaseAuth.getInstance();
        user_id=mAuth.getCurrentUser().getUid();

        mStorage=FirebaseStorage.getInstance().getReference();
        mStore=FirebaseFirestore.getInstance();


        setupToolbar=(Toolbar) findViewById(R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account setup");

        setupImage=(CircleImageView) findViewById(R.id.setup_image);
        setupNmae=(EditText)findViewById(R.id.setup_name_txt);
        setupButton=(Button)findViewById(R.id.setup_save_btn);
        setupProgress=(ProgressBar)findViewById(R.id.setup_progress);

        setupProgress.setVisibility(View.VISIBLE);
        setupButton.setEnabled(false);


        mStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        String name=task.getResult().getString("name");
                        String image=task.getResult().getString("image");

                        setupNmae.setText(name);
                        mainImageUri=Uri.parse(image);

                        RequestOptions placeholder=new RequestOptions();
                        placeholder.placeholder(R.drawable.default_image);
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholder).load(image).into(setupImage);

                    }

                }else{
                    String error=task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Error Retriving data"+error, Toast.LENGTH_SHORT).show();
                }

                setupProgress.setVisibility(View.INVISIBLE);
                setupButton.setEnabled(true);

            }
        });



        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name=setupNmae.getText().toString();

                if (!TextUtils.isEmpty(name) && mainImageUri != null) {

                    setupProgress.setVisibility(View.VISIBLE);

                if (ischange) {

                    user_id=mAuth.getCurrentUser().getUid();

                        final StorageReference image_path = mStorage.child("profile_image").child(user_id + ".jpg");

                        image_path.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {

                                    image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            Uri download_uri;
                                                download_uri=mainImageUri;


                                            Map<String,String> usermap=new HashMap<>();

                                            usermap.put("name",name);
                                            usermap.put("image",download_uri.toString());

                                            mStore.collection("Users").document(user_id).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        Toast.makeText(SetupActivity.this, "User Srttings are updated..", Toast.LENGTH_SHORT).show();
                                                        Intent mainIntent=new Intent(SetupActivity.this,MainActivity.class);
                                                        startActivity(mainIntent);
                                                        finish();


                                                    }else{
                                                        String msg=task.getException().getMessage();
                                                        Toast.makeText(SetupActivity.this, "Error : "+msg, Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });

                                            setupProgress.setVisibility(View.INVISIBLE);
                                            setupButton.setEnabled(true);






                                        }
                                    });


                                } else {
                                    String error = task.getException().getMessage().toString();
                                    Toast.makeText(SetupActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();
                                    setupProgress.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }else {
                    Toast.makeText(SetupActivity.this, "errrrrrr", Toast.LENGTH_SHORT).show();
                }
                }
            }
        });


        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    }else{

                        PickerImage();

                    }

                }else {

                    PickerImage();
                }
            }

            private void PickerImage() {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SetupActivity.this);
            }
        });
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {

            mainImageUri = result.getUri();
            setupImage.setImageURI(mainImageUri);

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
        }

    }

}

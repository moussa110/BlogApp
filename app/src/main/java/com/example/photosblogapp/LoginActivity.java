package com.example.photosblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
        private EditText loginEmailTxt;
        private EditText loginPassTxt;
        private Button loginbtn;
        private Button regbtn;
        private FirebaseAuth mAuth;
        private ProgressBar loginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        loginEmailTxt=(EditText)findViewById(R.id.reg_email);
        loginPassTxt=(EditText)findViewById(R.id.reg_password);
        loginbtn=(Button)findViewById(R.id.reg_regbtn);
        regbtn=(Button)findViewById(R.id.reg_loginbtn);
        loginProgress=(ProgressBar)findViewById(R.id.reg_progress);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail=loginEmailTxt.getText().toString();
                String loginPassword=loginPassTxt.getText().toString();

                if (!TextUtils.isEmpty(loginEmail)&&!TextUtils.isEmpty(loginPassword)){
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                sendtomain();
                            }else{
                                String errormessage=task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error: "+errormessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser=mAuth.getCurrentUser();
        if (currentuser!=null){
          sendtomain();
        }
    }

    private void sendtomain() {
        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}

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

public class RegisterActivity extends AppCompatActivity {

    private EditText regEmailText;
    private EditText regPassText;
    private EditText regPassConfText;
    private Button regRegisterbtn;
    private Button regLoginbtn;
    private ProgressBar regProgress;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        regProgress=(ProgressBar)findViewById(R.id.reg_progress);
        regEmailText=(EditText)findViewById(R.id.reg_email);
        regPassText=(EditText)findViewById(R.id.reg_password);
        regPassConfText=(EditText)findViewById(R.id.reg_pass_confirm);
        regRegisterbtn=(Button)findViewById(R.id.reg_regbtn);
        regLoginbtn=(Button)findViewById(R.id.reg_loginbtn);

        regLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        regProgress.setVisibility(View.INVISIBLE);
        regRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=regEmailText.getText().toString();
                String pass=regPassText.getText().toString();
                String passconf=regPassConfText.getText().toString();

                if (!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(pass)&&!TextUtils.isEmpty(passconf)){

                    if (pass.equals(passconf)){

                        regProgress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){

                                    startActivity(new Intent(RegisterActivity.this,SetupActivity.class));

                                } else {

                                    String errormsg=task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error: "+errormsg, Toast.LENGTH_SHORT).show();
                                }


                            }
                        });


                    }else {
                        Toast.makeText(RegisterActivity.this, "The password not confirm!!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser=mAuth.getCurrentUser();
        if (currentuser!=null)
            gotomain();
    }

    private void gotomain() {
        Intent mainintent=new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainintent);
        finish();
    }
}

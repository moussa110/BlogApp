package com.example.photosblogapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar maintoolbar;
    private FirebaseAuth mAuth;
    private FloatingActionButton addppstBtn;
    private FirebaseFirestore firestore;
    private String current_user_id;

    private BottomNavigationView mainBottomNav;
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Fragments
        accountFragment=new AccountFragment();
        homeFragment=new HomeFragment();
        notificationFragment=new NotificationFragment();

        replaceFragment(homeFragment);
        mainBottomNav=(BottomNavigationView)findViewById(R.id.main_navigation_bootom);
        addppstBtn=(FloatingActionButton)findViewById(R.id.add_post_btn);
        mAuth=FirebaseAuth.getInstance();
        maintoolbar= (Toolbar) findViewById(R.id.main_toolbar);
        firestore=FirebaseFirestore.getInstance();
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setTitle("Photo Blog");

        if (mAuth.getCurrentUser() != null) {



        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){

                    case R.id.bottom_action_home:
                        replaceFragment(homeFragment);
                        return true;

                    case R.id.bottom_action_notif:
                        replaceFragment(notificationFragment);
                        return true;

                    case R.id.bottom_action_acc:
                        replaceFragment(accountFragment);
                        return true;

                        default:
                            return false;

                }

            }
        });




        addppstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post=new Intent(MainActivity.this,PostActivity.class);
                startActivity(post);
            }
        });

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser= FirebaseAuth.getInstance().getCurrentUser();
        if (currentuser==null){
           gotologin();
        }else {
            current_user_id=mAuth.getCurrentUser().getUid();
            firestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        if (!task.getResult().exists()){

                            Intent setupIntent=new Intent(MainActivity.this,SetupActivity.class);
                            startActivity(setupIntent);
                        }

                    }else {
                        String error=task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_logout_btn:
                logout();
                return true;

            case R.id.action_accountsett_btn:
                gotosetup();
                return true;

             default:return false;

        }
    }

    private void gotosetup() {
    Intent setupintent=new Intent(MainActivity.this,SetupActivity.class);
    startActivity(setupintent);
    }

    private void logout() {
        mAuth.signOut();
        gotologin();
    }

    private void gotologin() {
        Intent loginintent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginintent);
        finish();
    }

    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.home_container,fragment);
        fragmentTransaction.commit();
    }
}

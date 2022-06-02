package com.example.shelterholonandrishonlezion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class second_activity extends AppCompatActivity {
    Button enter_area_btn,profile_user_btn;
    final String REGISTER_FRAGMENT_TAG="register_fragment";
    final String SEARCH_FRAGMENT_TAG="search_fragment";
    ProgressDialog progressDialog;
    private String userName;
    TextView userNameEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        userNameEt= findViewById(R.id.user_name_welcome);
        progressDialog = new ProgressDialog(second_activity.this);
        progressDialog.setTitle(getResources().getString(R.string.Loading_information));
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/");
        DatabaseReference users = database.getReference("users");
        users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserDetails user =snapshot.getValue(UserDetails.class);
                    userName = user.getName();
                    userNameEt.setText(user.getName());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        enter_area_btn=findViewById(R.id.enter_area_btn);
        profile_user_btn=findViewById(R.id.profile_user_btn);

        FragmentManager fragmentManager = getSupportFragmentManager();

        profile_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.root_container, new UserDetailsFragment(),REGISTER_FRAGMENT_TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        enter_area_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Search_area_fragment search_area_fragment = Search_area_fragment.newInstance(userName);
                transaction.add(R.id.root_container, search_area_fragment ,SEARCH_FRAGMENT_TAG);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout_btn){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.Logout))
                    .setMessage(getResources().getString(R.string.Are_you_sure_you_want_to_logout))
                    .setPositiveButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.firebaseAuth.signOut();
                    finish();
                }
            }).setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).setCancelable(false).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

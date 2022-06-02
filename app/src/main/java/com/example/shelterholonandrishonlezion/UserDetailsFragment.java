package com.example.shelterholonandrishonlezion;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDetailsFragment extends Fragment {
    ProgressDialog progressDialog;
    TextView user_email_tv,user_password_tv,user_name_tv,user_type_tv,user_name_welcome;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_details_activity, container, false);
        user_email_tv =view.findViewById(R.id.user_email_info);
        user_password_tv = view.findViewById(R.id.user_password_info);
        user_name_tv = view.findViewById(R.id.user_name_info);
        user_type_tv=view.findViewById(R.id.user_type_info);
        user_name_welcome=view.findViewById(R.id.user_name_welcome);

        progressDialog = new ProgressDialog(getContext());
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
                    user_name_welcome.setText(user.getName());
                    user_email_tv.setText(user.getMail());
                    user_password_tv.setText(user.getPassword());
                    user_name_tv.setText(user.getName());
                    user_type_tv.setText(user.getType());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}

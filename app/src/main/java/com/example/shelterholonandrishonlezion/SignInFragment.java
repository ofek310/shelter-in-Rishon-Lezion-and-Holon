package com.example.shelterholonandrishonlezion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInFragment extends Fragment {

    TextInputEditText user_mail,user_password,user_name;
    Button sign_in_btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.kind_user, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        user_mail = view.findViewById(R.id.email_id);
        user_password = view.findViewById(R.id.password_id);
        user_name=view.findViewById(R.id.name_id);
        sign_in_btn = view.findViewById(R.id.sign_in_btn);

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = user_mail.getText().toString();
                String password = user_password.getText().toString();
                String name = user_name.getText().toString();
                String type = spinner.getSelectedItem().toString();

                FirebaseDatabase database = FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/");
                DatabaseReference users = database.getReference().child("users");

                MainActivity.firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserDetails user = new UserDetails(mail,password,name,type);
                            users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).setValue(user);
                            Snackbar.make(view,"sign in successful",Snackbar.LENGTH_SHORT).show();
                        }
                        else{
                            Snackbar.make(view,"sign in unsuccessful",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

       return view;
    }
}
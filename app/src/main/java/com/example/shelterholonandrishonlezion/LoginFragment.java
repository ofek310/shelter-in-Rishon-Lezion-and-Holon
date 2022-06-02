package com.example.shelterholonandrishonlezion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

public class LoginFragment extends Fragment {

    TextInputEditText user_mail,user_password;
    Button login_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        user_mail = view.findViewById(R.id.email_id_login);
        user_password = view.findViewById(R.id.password_id_login);
        login_btn = view.findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = user_mail.getText().toString();
                String password = user_password.getText().toString();

                MainActivity.firebaseAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Snackbar.make(view,"Login successful",Snackbar.LENGTH_SHORT).show();
                        }else {
                            Snackbar.make(view,"Login unsuccessful",Snackbar.LENGTH_SHORT).show();
                        }
                        user_mail.setText("");
                        user_password.setText("");
                    }
                });
            }
        });
        return view;
    }
}
package com.example.shelterholonandrishonlezion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CommentShelterFragment extends Fragment {

    RecyclerView recyclerViewComments;
    private EditText addComment;
    private Button post_btn;
    private String id,id_comments;
    ArrayList<Comment> commentList = new ArrayList<Comment>();
    private CommentAdapter commentAdapter;
    private ProgressDialog pd;


    public static CommentShelterFragment newInstance(String user_name,
                                                     double column,int num_shelter){
        CommentShelterFragment commentShelterFragment = new CommentShelterFragment();
        Bundle bundle =  new Bundle();
        bundle.putString("user_name",user_name);
        bundle.putDouble("column",column);
        bundle.putInt("num_shelter",num_shelter);
        commentShelterFragment.setArguments(bundle);
        return commentShelterFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_shelter, container, false);

        addComment = view.findViewById(R.id.add_comment_et);
        post_btn = view.findViewById(R.id.post_comment_c);

        recyclerViewComments = view.findViewById(R.id.recyclerview_comments);
        recyclerViewComments.setHasFixedSize(true);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));

        commentList = new ArrayList<>();

        commentAdapter = new CommentAdapter(getContext(), commentList);
        recyclerViewComments.setAdapter(commentAdapter);


        pd = new ProgressDialog(getContext());
        pd.setMessage("טוען מידע");
        pd.show();

        int number = (int)(getArguments().getDouble("column"));
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/")
                .getReference().child("shelter").child(String.valueOf(getArguments().getInt("num_shelter"))).child(String.valueOf(number))
                .child("id_comments");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id_comments = String.valueOf(snapshot.getValue(String.class));
                getComment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(addComment.getText().toString())) {
                    Toast.makeText(getContext(), getResources().getString(R.string.You_did_not_write_anything_add_a_comment), Toast.LENGTH_SHORT).show();
                } else {
                    putComment();
                    addComment.setText("");
                }
            }
        });

        return view;
    }
    private void putComment() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", addComment.getText().toString());
        map.put("publisher", getArguments().getString("user_name"));
        map.put("publisher_id",MainActivity.firebaseAuth.getCurrentUser().getUid());

        int number = (int) (getArguments().getDouble("column"));

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/")
                .getReference().child("shelter").child(String.valueOf(getArguments().getInt("num_shelter"))).child(String.valueOf(number))
                .child("id_comments");

        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals("S")) {
                    DatabaseReference ref = FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/")
                            .getReference().child("Comments");
                    id = ref.push().getKey();

                    String idComment = ref.child(id).push().getKey();
                    map.put("id", idComment);

                    ref.child(id).child(idComment).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), getResources().getString(R.string.Comment_added), Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/").getReference().child("shelter")
                                        .child(String.valueOf(getArguments().getInt("num_shelter"))).child(String.valueOf(number))
                                        .child("id_comments").setValue(id);
                            } else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    DatabaseReference ref = FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/")
                            .getReference().child("Comments").child(snapshot.getValue().toString());
                    String idComment = ref.push().getKey();
                    map.put("id", idComment);
                    ref.child(idComment).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), getResources().getString(R.string.Comment_added), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getComment(){
        FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/").getReference()
                .child("Comments").child(id_comments).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                //commentAdapter = new CommentAdapter(getContext(),commentList);
                commentAdapter.notifyDataSetChanged();

                pd.dismiss();
                //recyclerViewComments.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

package com.example.shelterholonandrishonlezion;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class Search_area_fragment extends Fragment {
    Button create_map_btn,local_area_btn;
    final String MAP_FRAGMENT_TAG="map_fragment";
    private List<String> streets= new ArrayList<>();
    private AutoCompleteTextView editTextAutoComplete;
    private int position_spinner;
    private TextView user_name;
    private String name_user;
    private TextInputEditText number_home;
    private TextInputLayout number_home_all;

    public static Search_area_fragment newInstance(String nameUser){
        Search_area_fragment search_area_fragment = new Search_area_fragment();
        Bundle bundle = new Bundle();
        bundle.putString("name_user",nameUser);
        search_area_fragment.setArguments(bundle);
        return search_area_fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_area, container, false);

        user_name = view.findViewById(R.id.user_name_welcome_search_page);
        number_home = view.findViewById(R.id.number_address_search);
        number_home_all = view.findViewById(R.id.number_address);
        user_name.setText(getArguments().getString("name_user"));
        name_user = getArguments().getString("name_user");

        Spinner spinner = view.findViewById(R.id.spinner_city_search);
        editTextAutoComplete = view.findViewById(R.id.street);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.city, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                editTextAutoComplete.setText("");
                if(position!=0) {
                    editTextAutoComplete.setVisibility(View.VISIBLE);
                    number_home.setText("");
                    number_home_all.setVisibility(View.VISIBLE);
                    position_spinner = position;
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/");
                    DatabaseReference streetsDatabase = database.getReference("streets").child(String.valueOf(position_spinner));
                    streetsDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            streets.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                streets.add(String.valueOf(dataSnapshot.child("name_street").getValue()));
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                        R.layout.custom_list_item, R.id.text_view_list_item, streets);
                                editTextAutoComplete.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    editTextAutoComplete.setVisibility(View.GONE);
                    number_home_all.setVisibility(View.GONE);
                    streets.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        create_map_btn = view.findViewById(R.id.create_map_btn);
        local_area_btn = view.findViewById(R.id.local_area_btn);

        local_area_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map map = Map.newInstance(1,"",name_user,0);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.root_container, map, MAP_FRAGMENT_TAG)
                        .addToBackStack(null)
                        .commit();
            }
        });

        create_map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position_spinner!=0){
                    if (streets.contains(editTextAutoComplete.getText().toString())){
                        String address = editTextAutoComplete.getText().toString()+" "+number_home.getText().toString()+" "+spinner.getSelectedItem().toString();
                        Map map = Map.newInstance(2,address,name_user,position_spinner);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.root_container, map, MAP_FRAGMENT_TAG)
                                .addToBackStack(null)
                                .commit();
                    }
                    else {
                        Toast.makeText(getContext(), getResources().getString(R.string.The_address_is_invalid), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), getResources().getString(R.string.You_did_not_select_an_address), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
package com.example.shelterholonandrishonlezion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MoreDetailsShelterFragment extends Fragment {

    TextView name,address,city,accessibility,area,count_peoples;
    Button comment_btn;
    private static String TAG_COMMENT_FRAGMENT="comment_fragment";

    public MoreDetailsShelterFragment() {
        // Required empty public constructor
    }

    public static MoreDetailsShelterFragment newInstance(String name, String address,String city,String accessibility,
                                                         String area, String countPeoples,
                                                         int num_shelter,double column, String user_name) {
        MoreDetailsShelterFragment fragment = new MoreDetailsShelterFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("address", address);
        args.putString("city", city);
        args.putString("accessibility", accessibility);
        args.putString("area", area);
        args.putString("count_peoples", countPeoples);
        args.putInt("num_shelter",num_shelter);
        args.putDouble("column",column);
        args.putString("user_name",user_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_more_details_shelter, container, false);
        name = view.findViewById(R.id.shelter_name_more_info);
        address = view.findViewById(R.id.shelter_address_more_info);
        city = view.findViewById(R.id.shelter_city_more_info);
        accessibility = view.findViewById(R.id.shelter_accessibility_more_info);
        area = view.findViewById(R.id.shelter_area_more_info);
        count_peoples = view.findViewById(R.id.shelter_count_peoples_more_info);
        name.setText(this.getArguments().getString("name"));
        address.setText(this.getArguments().getString("address"));
        city.setText(this.getArguments().getString("city"));
        accessibility.setText(this.getArguments().getString("accessibility"));
        area.setText(this.getArguments().getString("area"));
        count_peoples.setText(this.getArguments().getString("count_peoples"));
        comment_btn = view.findViewById(R.id.comment_btn);
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                CommentShelterFragment commentShelterFragment = CommentShelterFragment.newInstance(getArguments().getString("user_name"),getArguments().getDouble("column"),getArguments().getInt("num_shelter"));
                transaction.add(R.id.root_container, commentShelterFragment, TAG_COMMENT_FRAGMENT);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }
}
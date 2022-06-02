package com.example.shelterholonandrishonlezion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;
    private Activity mActivity;
    private FirebaseDatabase database;
    private DatabaseReference shelter;
    private TextView name_shelter, address_shelter,distance_shelter;

    public CustomInfoWindowAdapter(Context context, Activity activity){
        mContext = context;
        mActivity = activity;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
    }

    private void rendowWindowText(Marker marker,View view){
        if(marker.getSnippet()!=null) {
            if(Locale.getDefault().getLanguage()=="en"){
                LinearLayout linearLayout = view.findViewById(R.id.linear);
                linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            else{
                LinearLayout linearLayout = view.findViewById(R.id.linear);
                linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            name_shelter = view.findViewById(R.id.shelter_name_window_info);
            address_shelter = view.findViewById(R.id.shelter_address_window_info);
            distance_shelter = view.findViewById(R.id.shelter_distance_window_info);
            PlaceDetails placeDetails = (PlaceDetails) marker.getTag();
            name_shelter.setText(placeDetails.getName());
            address_shelter.setText(marker.getSnippet());
            double number = Double.parseDouble(marker.getTitle());
            int number_D = (int)(number);
            distance_shelter.setText(String.valueOf(number_D)+"מטר ");
        }
    }
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        rendowWindowText(marker,mWindow);
        if(marker.getSnippet()==null){
            return null;
        }
        return mWindow;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        rendowWindowText(marker,mWindow);
        if(marker.getSnippet()==null){
            return null;
        }
        return mWindow;
    }
}

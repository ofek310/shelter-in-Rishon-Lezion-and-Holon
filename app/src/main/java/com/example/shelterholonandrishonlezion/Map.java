package com.example.shelterholonandrishonlezion;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class Map extends Fragment {

    GoogleMap map;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 16f;
    ProgressDialog progressDialog;
    private final String MORE_DETAILS_FRAGMENT="more_details_fragment";
    private final int DISTANCE_MAX=200;

    private static final int TAG_LOCAL_PLACE=1;
    private static final int TAG_NEW_PLACE=2;
    private int NUMBER_SELECTED,number_city;
    private LatLng yourLocation;

    String city;

    private LocationRequest locationRequest;
    public static final int REQUEST_CHECK_SETTING =1001;
    public static Map newInstance(int number_selected,String address,String user_name,int numberCity){
        Map map = new Map();
        Bundle args = new Bundle();
        args.putInt("number_selected", number_selected);
        args.putString("address",address);
        args.putString("user_name",user_name);
        args.putInt("number_city",numberCity);
        map.setArguments(args);
        return map;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_activty, container, false);
        NUMBER_SELECTED = this.getArguments().getInt("number_selected");
        number_city = getArguments().getInt("number_city");
        getLocationPermission();
        return view;
    }


    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                requestPermissions(permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            requestPermissions(permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map = googleMap;

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle(getResources().getString(R.string.Loading_information));
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.show();

                if(NUMBER_SELECTED == TAG_LOCAL_PLACE){
                    getLocationSetting();
                }
                else if (NUMBER_SELECTED==TAG_NEW_PLACE){
                    addMarkerToNewPlace(getArguments().getString("address"));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());
        try{
            if(mLocationPermissionsGranted){
               requestCurrentLocation();
            }
        }catch (SecurityException e){
            Log.d("aaaa","aaa6666666666");
        }
    }
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    private void requestCurrentLocation() {
        // Request permission
        if (ActivityCompat.checkSelfPermission(
                this.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            // Main code
            Task<Location> currentLocationTask = mFusedLocationProviderClient.getCurrentLocation(
                    PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.getToken());

            currentLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    String result = "";

                    try {
                        if (location!=null) {
                            LatLng l = new LatLng(location.getLatitude(), location.getLongitude());
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            moveCamera(l, DEFAULT_ZOOM);
                            yourLocation = l;
                            checkNumberCityInLocalPosition(addresses.get(0).getAddressLine(0));
                        } else {
                            Log.d("aaaa","44444444");
                        }
                    }catch (IOException e){

                    }
                }});
        }else {
            // TODO: Request fine location permission
            Log.d("aaaa", "Request fine location permission.");
        }
    }

    private void moveCamera(LatLng latLng,float zoom){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(getResources().getString(R.string.Your_location))
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.user_pic)));
        Marker m = map.addMarker(markerOptions);

    }
    private void addMarkerPlace(){
        DatabaseReference database = FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/").getReference().child("shelter")
                .child(String.valueOf(number_city));
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberPlace=0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlaceDetails placeDetails = dataSnapshot.getValue(PlaceDetails.class);
                    LatLng place_latlng = new LatLng(placeDetails.getLatitude(), placeDetails.getLongitude());
                    Double distance = SphericalUtil.computeDistanceBetween(yourLocation, place_latlng);
                    if(distance<=DISTANCE_MAX){
                        numberPlace++;
                        String number_home = placeDetails.getNumber_home().split("Q")[1];
                        MarkerOptions markerOptions = new MarkerOptions().position(place_latlng)
                                .title(String.valueOf(distance))
                                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.shelter_color)))
                                .snippet(placeDetails.getAddress() + " " + number_home + " " + placeDetails.getCity());
                        Marker m = map.addMarker(markerOptions);
                        m.setTag(placeDetails);
                        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext(), getActivity()));
                        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(@NonNull Marker marker) {
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                PlaceDetails placeDetailsFromMarker = (PlaceDetails)marker.getTag();
                                String number_home = placeDetailsFromMarker.getNumber_home().split("Q")[1];
                                MoreDetailsShelterFragment moreDetailsShelterFragment = MoreDetailsShelterFragment.newInstance(
                                        placeDetailsFromMarker.getName(),placeDetailsFromMarker.getAddress()+" "+number_home,
                                        placeDetailsFromMarker.getCity(),placeDetailsFromMarker.getAccessibility(),placeDetailsFromMarker.getArea(),
                                        placeDetailsFromMarker.getCount_peoples(),placeDetailsFromMarker.getNum_shelter(),
                                        placeDetailsFromMarker.getColumn(), getArguments().getString("user_name"));
                                transaction.add(R.id.root_container, moreDetailsShelterFragment, MORE_DETAILS_FRAGMENT);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        });
                    }
                }
                if(numberPlace==0){
                    Toast.makeText(getContext(),getResources().getString(R.string.no_shelter), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addMarkerToNewPlace(String currentAddress){
            Geocoder coder = new Geocoder(this.getContext());
            List<Address> address;

            try {
                address = coder.getFromLocationName(currentAddress, 10);
                if (address.size()<=0){
                    Log.d("lll","no");
                }else {
                    Address location = address.get(0);
                    LatLng place_latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(place_latlng).title(getResources().getString(R.string.The_location_you_requested))
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.user_pic)));
                    Marker m = map.addMarker(markerOptions);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(place_latlng,DEFAULT_ZOOM));
                    yourLocation = place_latlng;
                    addMarkerPlace();
                }

            } catch ( IOException e) {
                e.printStackTrace();
                Log.d("lll",e.getMessage());
            }
    }
    private void checkNumberCityInLocalPosition(String address){
        city =address.split(",")[1].split(" ")[1];
        FirebaseDatabase.getInstance("https://sheltergisproject-ec6a8-default-rtdb.firebaseio.com/")
                .getReference().child("cities").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if((dataSnapshot.child("name_city_heb").getValue().toString().equals(city)) ||
                            (dataSnapshot.child("name_city_en").getValue().toString().equals(city))){
                        number_city = Integer.parseInt(dataSnapshot.getKey());
                        addMarkerPlace();
                    }
                }
                if(number_city==0){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),getResources().getString(R.string.Your_location_does_not_match_the_cities_listed_in_the_app), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getLocationSetting(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getActivity().getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    getDeviceLocation();
                } catch (ApiException e) {
                    switch (e.getStatusCode()){
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED: {
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                //resolvableApiException.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTING);
                                startIntentSenderForResult(resolvableApiException.getResolution().getIntentSender(), REQUEST_CHECK_SETTING, null, 0, 0, 0, null);
                            } catch (IntentSender.SendIntentException sendIntentException) {

                            }
                            break;
                        }
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:{
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CHECK_SETTING){
            switch (resultCode){
                case Activity.RESULT_OK:{
                    getDeviceLocation();
                    break;
                }
                case Activity.RESULT_CANCELED:{
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), getResources().getString(R.string.the_location_should_enable), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }
}

package com.bullhu.android.fragments.consignor;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bullhu.android.R;
import com.bullhu.android.activities.consignor.ConsignorMainActivity;
import com.bullhu.android.common.Commons;
import com.bullhu.android.databinding.ActivityMapsBinding;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;
import com.bullhu.android.utils.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeConsignorFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMapClickListener, GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.txv_pick_up)
    TextView txv_pick_up;
    @BindView(R.id.txv_drop_off)
    TextView txv_drop_off;
    @BindView(R.id.txv_return_location)
    TextView txv_return_location;
    @BindView(R.id.edt_size)
    TextInputEditText edt_size;
    @BindView(R.id.edt_weight)
    TextInputEditText edt_weight;
    @BindView(R.id.txv_tdo)
    TextView txv_tdo;
    @BindView(R.id.txv_pickup_time)
    TextView txv_pickup_time;
    View view;
    ConsignorMainActivity activity;

    private static final int pickup_code = 22;
    private static final int dropoff_code = 23;
    private static final int return_location_code = 24;

    int[] strIds = {R.string.select_TDO, R.string.consignor};

    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private static final int ACCESS_COARSE_LOCATION_PERMISSION_REQUEST = 7001;
    String pick_up_location = "", drop_off_location = "", empty_return_location = "", container_size = "", container_weight = "", preferred_time = "";
    boolean tdo_ready = false;
    Marker pickUpMarker, dropoffMarker, returnMarker;

    public HomeConsignorFragment() {
        //this.activity = activity;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_consignor, container, false);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag_map);

        mapFragment.getMapAsync(this);
        ButterKnife.bind(this, view);
        loadLayout();

        if (mapFragment != null) {

            buildGoogleApiClient();
            mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (mLocationManager != null){
                mapFragment.getMapAsync(this);
            }

        }

        return view;
    }

    private void loadLayout() {

        String apiKey = getString(R.string.place_api_key);
        if (!Places.isInitialized()) {
            Places.initialize(activity, apiKey);
        }

        ImageView transparentImageView = (ImageView) view.findViewById(R.id.transparent_image);


        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scroll.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scroll.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scroll.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }


    @OnClick(R.id.txv_tdo)
    void selectTDO() {


        String[][] itemsArray = {getResources().getStringArray(R.array.TDO_array)};
        final String[] items = itemsArray[0];

        //final TextView txv = textView;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(strIds[0]);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int item) {

                txv_tdo.setText(items[item]);
                String str_item = txv_tdo.getText().toString();

                tdo_ready = str_item.equalsIgnoreCase("Yes");
            }
        });

        android.app.AlertDialog alert = builder.create();

        alert.show();
    }

    @OnClick(R.id.txv_pickup_time)
    void pickUpTime() {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, this, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month + 1;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;

        txv_pickup_time.setText(myHour + ":" + myMinute + " " + myday + "/" + myMonth + "/" + myYear);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        try {
            Date date = (Date) formatter.parse(txv_pickup_time.getText().toString());
            preferred_time = String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.txv_pick_up)
    void pickUpSearch() {
        onSearchCalled(pickup_code);
    }

    @OnClick(R.id.txv_drop_off)
    void dropOffSearch() {
        onSearchCalled(dropoff_code);
    }

    @OnClick(R.id.txv_return_location)
    void returnLocationSearch() {
        onSearchCalled(return_location_code);
    }

    public void onSearchCalled(int request_code) {

        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(activity);
        startActivityForResult(intent, request_code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            String address = place.getAddress();

            LatLng latLng = place.getLatLng();

            if (requestCode == pickup_code) {

                txv_pick_up.setText(address);

                pick_up_location = latLng.latitude + "," + latLng.longitude;

                if (pickUpMarker != null)
                    pickUpMarker.remove();

                pickUpMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(activity.getString(R.string.pickup_location)));
                pickUpMarker.showInfoWindow();
            }

            else if (requestCode == dropoff_code) {

                txv_drop_off.setText(address);
                drop_off_location = latLng.latitude + "," + latLng.longitude;

                if (dropoffMarker != null)
                    dropoffMarker.remove();
                dropoffMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(activity.getString(R.string.dropoff_location)));
                dropoffMarker.showInfoWindow();
            }

            else if (requestCode == return_location_code) {

                txv_return_location.setText(address);
                empty_return_location = latLng.latitude + "," + latLng.longitude;

                if (returnMarker != null)
                    returnMarker.remove();
                returnMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(activity.getString(R.string.return_location)));
                returnMarker.showInfoWindow();
            }

            CameraPosition position = CameraPosition.builder()
                    .target(latLng)
                    .zoom(12f)
                    .bearing(30)                // Sets the orientation of the camera to east
                    .tilt(30)
                    .build();
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), null);


        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(activity, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }

    }

    private boolean checkedValid(){

        if (pick_up_location.isEmpty()){
            activity.showToast(R.string.enter_pickup_location);
            return false;
        }
        else if (drop_off_location.isEmpty()){
            activity.showToast(R.string.enter_dropoff_location);
            return false;
        }
        else if (empty_return_location.isEmpty()){
            activity.showToast(R.string.enter_empty_location);
            return false;
        }
        else if (container_size.isEmpty()){
            activity.showToast(R.string.enter_size);
            return false;
        }
        else if (container_weight.isEmpty()){
            activity.showToast(R.string.enter_weight);
            return false;
        }
        else if (preferred_time.isEmpty()){
            activity.showToast(R.string.select_preferred_time);
            return false;
        }
        else if (txv_tdo.getText().toString().isEmpty()){
            activity.showToast(R.string.select_TDO);
            return false;
        }
        return true;
    }

    @OnClick(R.id.btn_request) void deliveryRequest(){

        container_size = edt_size.getText().toString();
        container_weight = edt_weight.getText().toString();

        if (checkedValid())
            processRequest();
    }

    private void processRequest(){

        activity.showHUD();
        ApiManager.deliveryRequest(pick_up_location, drop_off_location, empty_return_location, container_size, container_weight,
                preferred_time, tdo_ready, Commons.ACCESS_TOKEN, new ICallback() {
                    @Override
                    public void onCompletion(RESULT result, Object resultParam) {
                        activity.hideHUD();
                        switch (result){
                            case SUCCESS:
                                activity.showToast("Request sent");
                                break;

                            case FAILURE:
                                activity.showToast((String) resultParam);
                                break;
                        }
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        checkForLocationPermission();
    }

    private void checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_PERMISSION_REQUEST);

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);

        } else {
            mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location == null) location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null) location = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                try {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    ///////////////////////////////////////////////////////////////////////////////////////////////

                    setUpMap();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setUpMap() {

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        //mGoogleApiClient.connect();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            ActivityCompat.requestPermissions(activity,
                    new String[]{ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_PERMISSION_REQUEST);
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.ACCESS_BACKGROUND_LOCATION
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (ConsignorMainActivity) context;
    }


}
package com.fras.msbm.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fras.msbm.R;
import com.fras.msbm.activities.general.LocationDetailActivity;
import com.fras.msbm.activities.general.ExpandableTravelListViewActivity;
import com.fras.msbm.events.network.NetworkConnectEvent;
import com.fras.msbm.models.User;
import com.fras.msbm.models.locations.Coordinates;
import com.fras.msbm.models.locations.Location;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.MyBearingTracking;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class LocationFragment extends BaseFragment {
    public static final String TAG = LocationFragment.class.getName();
    private static final int PERMISSION_FINE_LOCATION = 1234;
    private static final int PERMISSIONS_LOCATION = 0;
    private static final int REQUEST_CODE = 0;
    ShowcaseView sv;

    private Bundle extras;

    @BindView(R.id.map_view) MapView mapView;

    public static String moodleLocation;
    private Marker currentLocationMarker;
    private MapboxMap map;
    FloatingActionButton floatingActionButton;
    LocationServices locationServices;
    List<Location> locations;
    private FirebaseUser user;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    final DatabaseReference userRef = database.getReference("users");


    SharedPreferences prefs = null;

    public LocationFragment() {
        // Required empty public constructor
    }

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        prefs = getActivity().getSharedPreferences("com.fras.msbm", MODE_PRIVATE);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null ) findUserById(user.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locationServices = LocationServices.getLocationServices(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);
        ButterKnife.bind(this, rootView);
        setToolbarTitle("Locations");

        extras = getArguments();

        if(extras != null){
            Toast.makeText(getContext(), extras.getString("FROM_LOCATIONS"), Toast.LENGTH_SHORT).show();

            Toast.makeText(getContext(), extras.getString("FROM_LOCATIONS_CHILD"), Toast.LENGTH_SHORT).show();
        }

        MapboxAccountManager.start(getContext(), getString(R.string.mapbox_key));
        setupMainLayout(savedInstanceState);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
//                    Toast.makeText(getContext(), "Add location", Toast.LENGTH_SHORT).show();

                }
            }
        });

        return rootView;
    }

    private void findUserById(@NonNull String userId) {
        userRef.child(userId).child("moodle")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        Log.i("TAG", "userCourses:" + user.toString());
                        moodleLocation = user.getMoodleLocation();
                        Log.e(TAG, "Location:" + moodleLocation);
//                        Toast.makeText(BookingsActivity.this, "Token: " + user.getToken(), Toast.LENGTH_SHORT).show();
//                        loadUserCourses(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//                        showErrorViews();
                        FirebaseCrash.report(databaseError.toException());
                    }
                });
    }
    //Instructional overlay that triggers when using the app for the first time.
    public void firstDemoView(){
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        sv =  new ShowcaseView.Builder(getActivity())
                .setTarget(new ViewTarget(floatingActionButton))
//                .withMaterialShowcase()
                .setContentTitle("My Location")
                .setContentText("Tap the location button to toggle your current location on the map.")
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme2)
                .replaceEndButton(R.layout.view_custom_button)
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
//                        ((LocationFragment) getActivity()).onHiddenFirstShowcase();
                        secondDemoView();
                    }

                })
                .build();

        sv.setButtonPosition(lps);
    }

    public void secondDemoView(){
//        MenuItem menuItem = (MenuItem) view.findViewById(R.id.action_add_location);

        new ShowcaseView.Builder(getActivity())
                .setTarget(new ViewTarget(R.id.action_add_location, getActivity()))
                .withMaterialShowcase()
                .setContentTitle("Available Locations")
                .setContentText("Tap the locations button to see the list of available locations on campus.")
                .setStyle(R.style.CustomShowcaseTheme2)
                .replaceEndButton(R.layout.view_custom_button)
                .hideOnTouchOutside()
                .build();
    }

    public void toggleGps(boolean enableGps) {
        if (enableGps) {
            final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) //if gps not turned on
            {
                buildAlertMessageNoGps();
            } else {
                if (!locationServices.areLocationPermissionsGranted()) {  //if permissions not granted
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
                } else {
                    enableLocation(true);
                }
            }
            // Check if user has granted location permission
        } else {
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            // Disable tracking dismiss on map gesture
            // map.getTrackingSettings().setDismissAllTrackingOnGesture(false);
            map.getTrackingSettings().setDismissBearingTrackingOnGesture(false);

            // Enable location and bearing tracking
            map.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
            map.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);


            locationServices.addLocationListener(new com.mapbox.mapboxsdk.location.LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    if (location != null) {
                        // Move the map camera to where the user location is
                        if (map.isMyLocationEnabled()) {
                            map.setCameraPosition(new CameraPosition.Builder()
                                    .target(new LatLng(location))
                                    .zoom(16)
                                    .build());
                        }

                    }
                }
            });
            floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
        } else {
            if(!moodleLocation.isEmpty()) {
                Log.e(TAG, "Found Location: " + moodleLocation);
                switch (moodleLocation) {
                    case "mona":
                        map.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(18.007999, -76.747349))
                                .zoom(16)
                                .build());
                        floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
                        break;
                    case "wcj":
                        map.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(18.495302, -77.914520))
                                .zoom(16)
                                .build());
                        floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
                }
            }else{
                map.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(18.007999, -76.747349))
                        .zoom(16)
                        .build());
                floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
            }
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);

    }

    private void updateMap(double latitude, double longitude) {
        // Build marker
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Geocoder result"));

        // Animate camera to geocoder result location
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void setupMainLayout(Bundle savedInstanceState) {
        mapView.setAccessToken(getString(R.string.mapbox_key));
        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady (MapboxMap mapboxMap){
                mapboxMap.setStyleUrl(Style.LIGHT);
                mapboxMap.setAccessToken(getString(R.string.mapbox_key));
                if(extras != null){
                    if(!moodleLocation.isEmpty()){ // check for campus location
                        switch(moodleLocation) { // based on location, get specific locations
                            case "mona":
                                switch (extras.getString("FROM_LOCATIONS")){
                                    case "Halls":
                                        switch(extras.getString("FROM_LOCATIONS_CHILD")){
                                            case "All":
                                                drawHallMarkers(mapboxMap);
                                                break;
                                            default:
                                                getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                                break;
                                        }
                                        break;
                                    case "Faculties/Departments":
                                        switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                            case "All":
                                                drawFacultyMarkers(mapboxMap);
                                                break;
                                            default:
                                                getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                                break;
                                        }
                                        break;
                                    case "Where to eat":
                                        switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                            case "All":
                                                drawFoodMarkers(mapboxMap);
                                                break;
                                            default:
                                                getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                                break;
                                        }
                                        break;
                                    case "Where to drink":
                                        switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                            case "All":
                                                drawDrinkMarkers(mapboxMap);
                                                break;
                                            default:
                                                Log.e(TAG, "Made It");
                                                getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                                break;
                                        }
                                        break;
                                    case "Banks/ATM":
                                        switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                            case "All":
                                                drawBankMarkers(mapboxMap);
                                                break;
                                            default:
                                                getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                                break;
                                        }
                                        break;
                                    case "Classes":
                                        switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                            case "All":
                                                drawMonaClassMarkers(mapboxMap);
                                                break;
                                            default:
                                                getSelectedMonaClassMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                                break;
                                        }
                                        break;
                                    default:
                                        drawMarkersOnMap(mapboxMap);
                                        break;
                                }
                                break;
                            case "wcj":
                                Log.e(TAG, "case wcj: getSelectedMarker");
                                switch (extras.getString("FROM_LOCATIONS")){
                                    case "Faculties/Departments":
                                        switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                            case "All":
                                                drawWesternFacultyMarkers(mapboxMap);
                                                break;
                                            default:
                                                getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                                break;
                                        }
                                        break;
                                    case "Where to eat":
                                        switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                            case "All":
//                                                drawFoodMarkers(mapboxMap);
                                                break;
                                            default:
                                                getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                                break;
                                        }
                                        break;
                                    case "Banks/ATM":
                                        switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                            case "All":
                                                drawWesternBankMarkers(mapboxMap);
                                                break;
                                            default:
                                                getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                                break;
                                        }
                                        break;
                                    case "Classes":
                                        switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                            case "All":
                                                drawWesternClassMarkers(mapboxMap);
                                                break;
                                            default:
                                                getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                                break;
                                        }
                                        break;

                                }
//                                getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                break;

                        }
                    }else{
                        switch (extras.getString("FROM_LOCATIONS")){
                            case "Halls":
                                switch(extras.getString("FROM_LOCATIONS_CHILD")){
                                    case "All":
                                        drawHallMarkers(mapboxMap);
                                        break;
                                    default:
                                        getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                        break;
                                }
                                break;
                            case "Faculties/Departments":
                                switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                    case "All":
                                        drawFacultyMarkers(mapboxMap);
                                        break;
                                    default:
                                        getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                        break;
                                }
                                break;
                            case "Where to eat":
                                switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                    case "All":
                                        drawFoodMarkers(mapboxMap);
                                        break;
                                    default:
                                        getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                        break;
                                }
                                break;
                            case "Where to drink":
                                switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                    case "All":
                                        drawDrinkMarkers(mapboxMap);
                                        break;
                                    default:
                                        Log.e(TAG, "Made It");
                                        getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                        break;
                                }
                                break;
                            case "Banks/ATM":
                                switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                    case "All":
                                        drawBankMarkers(mapboxMap);
                                        break;
                                    default:
                                        getSelectedMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                        break;
                                }
                                break;
                            case "Classes":
                                switch(extras.getString("FROM_LOCATIONS_CHILD")) {
                                    case "All":
                                        drawMonaClassMarkers(mapboxMap);
                                        break;
                                    default:
                                        getSelectedMonaClassMarker(mapboxMap,extras.getString("FROM_LOCATIONS_CHILD"));
                                        break;
                                }
                                break;
                            default:
                                drawMarkersOnMap(mapboxMap);
                                break;
                        }

                    }
                }else{
                    if(!moodleLocation.isEmpty()){
                        Log.e(TAG, "Found Location: " + moodleLocation);
                        switch(moodleLocation){
                            case "mona":
                                drawMarkersOnMap(mapboxMap);
                                break;
                            case "wcj":
                                drawWestMarkers(mapboxMap);
                                break;
                        }
                    }else{
                        Log.e(TAG, "Null Location: " + moodleLocation);
                        drawMarkersOnMap(mapboxMap);
                    }
                }

                map = mapboxMap;
                //initPointsOfInterest();
//            new LoadPlacesTask(view.getContext()).execute();
                //markPlacesOfInterest();
                map.setOnInfoWindowClickListener(new MapboxMap.OnInfoWindowClickListener() {
                    @Override
                    public boolean onInfoWindowClick(@NonNull Marker marker) {
                        Location place = findPlaceByPosition(marker.getPosition());
                        Log.e(TAG,"Marker Position: " + marker.getPosition() + "Place Name : " + place.getName());
//                        map.getMarkers().
                        Intent intent = new Intent(getActivity(), LocationDetailActivity.class);
                        intent.putExtra("NAME",place.getName());
                        intent.putExtra("SHORT_NAME",place.getShortName());
                        intent.putExtra("DESCRIPTION",place.getDescription());
                        startActivity(intent);
//                        Toast.makeText(getContext()," Pressed !", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        });
    }

//    @Override
//    public void onMapReady(MapboxMap mapboxMap) {
//        mapboxMap.setStyleUrl(Style.LIGHT);
//        mapboxMap.setAccessToken(getString(R.string.mapbox_key));
//        drawMarkersOnMap(mapboxMap);
//        map = mapboxMap;
//    }

    //fix shortname vs full name check bug
    private void drawMarkersOnMap(MapboxMap map) {
        locations = Location.getDiningLocations();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));
        }
    }

    private void drawWestMarkers(MapboxMap map){
        locations = Location.getWesternLocations();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));
        }

        map.setCameraPosition(new CameraPosition.Builder()
                .target(new LatLng(convertCoordinatesToLatLng(locations.get(0).getCoordinates())))
                .zoom(16)
                .build());
    }

    private void drawHallMarkers(MapboxMap map) {
        locations = Location.getHallLocations();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));
        }
    }

    private void drawFacultyMarkers(MapboxMap map) { //Mona campus
        locations = Location.getFacultyLocations();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));
        }
    }

    private void drawWesternFacultyMarkers(MapboxMap map) { //Mona campus
        locations = Location.getWesternFaculties();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
//            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();
            final String name = location.getName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));

            map.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(18.495302, -77.914520))
                    .zoom(16)
                    .build());

        }
    }

    private void drawFoodMarkers(MapboxMap map) { //Mona Campus
        locations = Location.getFoodLocations();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));
        }
    }

    private void drawDrinkMarkers(MapboxMap map) {
        locations = Location.getDrinkLocations();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));
        }
    }

    private void drawBankMarkers(MapboxMap map) { //Mona Campus
        locations = Location.getBankLocations();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));
        }
    }

    private void drawWesternBankMarkers(MapboxMap map) {
        locations = Location.getWesternBanks();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
//            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();
            final String name = location.getName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));

            map.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(18.495302, -77.914520))
                    .zoom(16)
                    .build());
        }
    }

    private void drawMonaClassMarkers(MapboxMap map) {
        locations = Location.getMonaClassLocations();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));
        }
    }

    private void drawWesternClassMarkers(MapboxMap map) {
        locations = Location.getWesternClasses();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
//            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();

            final String name = location.getName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .snippet("View Details"));

            map.setCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(18.495302, -77.914520))
                    .zoom(16)
                    .build());
        }
    }

    private void getSelectedMarker(MapboxMap map, String childName) {
        if(!moodleLocation.isEmpty()){
            Log.e(TAG, "getSelectedMarker Found Location: " + moodleLocation);
            switch(moodleLocation){
                case "mona":
                    locations = Location.getAllLocations(); // Mona locations
                    break;
                case "wcj":
                    Log.e(TAG, "case wcj: getSelectedMarker");
                    locations = Location.getWesternLocations();
                    map.setCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(18.495302, -77.914520))
                            .zoom(16)
                            .build());
                    break;
            }
        }else{
            locations = Location.getAllLocations();
        }
//        locations = Location.getAllLocations();
        Log.e(TAG, "Getting Location");
        for (Location location : locations) {
            Log.e(TAG, location.getName());
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
//            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();
            final String name = location.getName();
            String shortName = "";
            try {
                shortName  = location.getShortName();
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            Log.e(TAG, "Checking Names... " + location.getName() + " vs " + childName);
            if(name.equals(childName) ){
                Log.e(TAG, "Adding Markers..." + latLng.toString());
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .snippet("View Details"));
                break;
            }

        }
    }

    private void getSelectedMonaClassMarker(MapboxMap map, String childName) {
        locations = Location.getMonaClassLocations();
        Log.e(TAG, "Getting Location");
        for (Location location : locations) {
            Log.e(TAG, location.getName());
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
//            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();
            final String name = location.getName();
            String shortName = "";
            try {
                shortName  = location.getShortName();
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            Log.e(TAG, "Checking Names...");
            if(name.equals(childName) ){
                Log.e(TAG, "Adding Markers...: " + childName);
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .snippet("View Details"));
                break;
            }

        }
    }

    private LatLng convertCoordinatesToLatLng(Coordinates coordinates) {
        return new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
    }

    private Location findPlaceByPosition(LatLng position) {
        for (int i = 0; i < locations.size(); i++) {
//            Log.e(TAG," Size: " + Integer.toString(locations.size()) + " Count " + Integer.toString(i));
//            Log.e(TAG,"Position: " + locations.get(i).convertCoordinatesToLatLng());
//            Log.e(TAG, Boolean.toString(locations.get(i).convertCoordinatesToLatLng().equals(position)));
//            Log.e(TAG, position.toString());
            if (locations.get(i).convertCoordinatesToLatLng().equals(position)){
                Log.e(TAG,"Position Found" );
                return locations.get(i);
            }
//            Log.e(TAG, "After If Statement");
        }
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_location, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_location:
//                addLocationToMap();
                Intent intent = new Intent(getActivity(), ExpandableTravelListViewActivity.class);
                intent.putExtra("LOCATION",moodleLocation);
                startActivity(intent);
//                Toast.makeText(getContext(), "Add location", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void addLocationToMap() {
//        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
//            }
//        } else {
//            requestDeviceLocation();
//        }
//    }
//
//    private void requestDeviceLocation() {
//        LocationManager locationManager = (LocationManager)
//                getActivity().getSystemService(Context.LOCATION_SERVICE);
//
//        LocationListener locationListener = new MyLocationListener();
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSION_FINE_LOCATION:
//                requestDeviceLocation();
//                break;
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            Log.e(TAG,"Running Demo");
            firstDemoView();
            // using the following line to edit/commit prefs
            Log.e(TAG,"Changing Preferences");
            prefs.edit().putBoolean("firstrun", false).apply();//.commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Subscribe
    public void onEvent(NetworkConnectEvent event) {

    }

//    private class MyLocationListener implements LocationListener {
//
//        @Override
//        public void onLocationChanged(Location location) {
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//
//            Toast.makeText(getContext(),
//                    "Location changed: Lat: " + latitude + " Lng: "
//                            + longitude, Toast.LENGTH_SHORT).show();
//
//            Log.i(TAG, "latitude: " + latitude);
//            Log.i(TAG, "longitude: " +longitude);
//
//            IconFactory iconFactory = IconFactory.getInstance(getContext());
//            Drawable iconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_person_pin_circle_light_blue_900_24dp);
//            Icon icon = iconFactory.fromDrawable(iconDrawable);
//
//            if (currentLocationMarker != null) {
//                map.removeMarker(currentLocationMarker);
//            }
//
//            if (map != null) {
//                LatLng position = new LatLng(latitude, longitude);
//
//                MarkerOptions options = new MarkerOptions()
//                        .title("Me")
//                        .position(position)
//                        .icon(icon);
//
//                currentLocationMarker = map.addMarker(options);
//
//                map.setCameraPosition(new CameraPosition.Builder()
//                        .target(position)
//                        .build());
//            }
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String s) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String s) {
//
//        }
//    }
}

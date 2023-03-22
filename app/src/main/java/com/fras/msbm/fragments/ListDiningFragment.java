package com.fras.msbm.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fras.msbm.R;
import com.fras.msbm.models.locations.Coordinates;
import com.fras.msbm.models.locations.Location;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListDiningFragment extends Fragment
        implements OnMapReadyCallback {
    public static final String TAG = ListDiningFragment.class.getName();

    @BindView(R.id.map_view) MapView mapView;

    public ListDiningFragment() {
        // Required empty public constructor
    }

    public static ListDiningFragment newInstance() {
        return new ListDiningFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(getContext(), getString(R.string.mapbox_key));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentwsa
        final View rootView = inflater.inflate(R.layout.fragment_list_dining, container, false);
        initialConfigurations(rootView);
        setupMap(savedInstanceState);
        return rootView;
    }

    private void initialConfigurations(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    private void setupMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dining, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mapboxMap.setStyleUrl(Style.LIGHT);
        mapboxMap.setAccessToken(getString(R.string.mapbox_key));
        drawMarkersOnMap(mapboxMap);
    }

    private void drawMarkersOnMap(MapboxMap map) {
        final List<Location> locations = Location.getDiningLocations();

        for (Location location : locations) {
            final LatLng latLng = convertCoordinatesToLatLng(location.getCoordinates());
            final String name = (location.getShortName() == null) ? location.getName() : location.getShortName();

            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name));
        }
    }

    private LatLng convertCoordinatesToLatLng(Coordinates coordinates) {
        return new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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
}

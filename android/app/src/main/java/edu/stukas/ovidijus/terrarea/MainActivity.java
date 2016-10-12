package edu.stukas.ovidijus.terrarea;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import edu.stukas.ovidijus.terrarea.data.MarkingMode;
import edu.stukas.ovidijus.terrarea.handler.GoogleMapButtonVisibilityHandler;
import edu.stukas.ovidijus.terrarea.handler.GoogleMapTerritoryHandler;
import edu.stukas.ovidijus.terrarea.handler.GooglePlacesSearchHandler;

/**
 * @author Ovidijus Stukas
 */

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GooglePlacesSearchHandler googlePlacesSearchHandler;
    private GoogleMapTerritoryHandler googleMapTerritoryHandler;
    private GoogleMapButtonVisibilityHandler googleMapButtonVisibilityHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton addLocationFab = (FloatingActionButton)
                findViewById(R.id.btn_add_location);
        FloatingActionButton saveLocationFab = (FloatingActionButton)
                findViewById(R.id.btn_save_location);
        FloatingActionButton abortLocationFab = (FloatingActionButton)
                findViewById(R.id.btn_abort_location);

        googleMapButtonVisibilityHandler = new GoogleMapButtonVisibilityHandler();
        googleMapButtonVisibilityHandler.registerFAB(addLocationFab, MarkingMode.OFF);
        googleMapButtonVisibilityHandler.registerFAB(saveLocationFab, MarkingMode.ON);
        googleMapButtonVisibilityHandler.registerFAB(abortLocationFab, MarkingMode.ON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                googlePlacesSearchHandler.onSearchClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addLocation(View v) {
        googleMapButtonVisibilityHandler.applicationChanged(MarkingMode.ON);

        googleMapTerritoryHandler.startSession();
    }

    public void abortLocation(View v) {
        googleMapButtonVisibilityHandler.applicationChanged(MarkingMode.OFF);

        googleMapTerritoryHandler.clearSession();
    }

    public void saveLocation(View v) {
        googleMapButtonVisibilityHandler.applicationChanged(MarkingMode.OFF);

        SaveLocationDialogFragment saveLocationDialogFragment = new SaveLocationDialogFragment();
        saveLocationDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        saveLocationDialogFragment.show(getSupportFragmentManager(), "Dialog");

        googleMapTerritoryHandler.clearSession();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            googleMap.setMyLocationEnabled(true);

        // Camera movement:
        googleMap.setOnCameraIdleListener(googleMapButtonVisibilityHandler);
        googleMap.setOnCameraMoveListener(googleMapButtonVisibilityHandler);

        googlePlacesSearchHandler = new GooglePlacesSearchHandler(this, googleMap);
        googleMapTerritoryHandler = new GoogleMapTerritoryHandler(this, googleMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GooglePlacesSearchHandler.REQUEST_SELECT_PLACE_CODE)
        {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                googlePlacesSearchHandler.onPlaceSelected(place);
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                googlePlacesSearchHandler.onError(status);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

package edu.stukas.ovidijus.terrarea;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import edu.stukas.ovidijus.terrarea.fragment.SettingsFragment;
import edu.stukas.ovidijus.terrarea.fragment.TerritoryListFragment;
import edu.stukas.ovidijus.terrarea.handler.GoogleMapButtonVisibilityHandler;
import edu.stukas.ovidijus.terrarea.handler.GoogleMapTerritoryHandler;
import edu.stukas.ovidijus.terrarea.handler.GooglePlacesSearchHandler;

/**
 * @author Ovidijus Stukas
 */

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GooglePlacesSearchHandler googlePlacesSearchHandler;
    private GoogleMapTerritoryHandler googleMapTerritoryHandler;
    private GoogleMapButtonVisibilityHandler googleMapButtonVisibilityHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setupGoogleMapUI();
    }

    private void setupGoogleMapUI() {
        FloatingActionButton addLocationFab = (FloatingActionButton)
                findViewById(R.id.btn_add_location);

        addLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMapButtonVisibilityHandler.applicationChanged(MarkingMode.ON);
                googleMapTerritoryHandler.startSession();
            }
        });

        FloatingActionButton saveLocationFab = (FloatingActionButton)
                findViewById(R.id.btn_save_location);
        saveLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMapButtonVisibilityHandler.applicationChanged(MarkingMode.OFF);

                AlertDialog saveLocationDialog = getSaveLocationDialog();
                saveLocationDialog.show();

                googleMapTerritoryHandler.clearSession();
            }
        });

        FloatingActionButton abortLocationFab = (FloatingActionButton)
                findViewById(R.id.btn_abort_location);
        abortLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMapButtonVisibilityHandler.applicationChanged(MarkingMode.OFF);
                googleMapTerritoryHandler.clearSession();
            }
        });

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        super.onBackPressed();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case R.id.drawer_territories:
                drawer.closeDrawer(GravityCompat.START);

                DialogFragment territoryListFragment = new TerritoryListFragment();
                territoryListFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                territoryListFragment.show(getSupportFragmentManager(), "Dialog");
                break;
            case R.id.drawer_settings:
                drawer.closeDrawer(GravityCompat.START);

                DialogFragment settingsFragment = new SettingsFragment();
                settingsFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                settingsFragment.show(getSupportFragmentManager(), "Settings");
                break;
            case R.id.drawer_signin:
                drawer.closeDrawer(GravityCompat.START);

                getLoginDialog().show();
                break;
        }

        return false;
    }

    private AlertDialog getSaveLocationDialog() {
        Context context = MainActivity.this;

        return new AlertDialog.Builder(context)
                .setView(LayoutInflater
                    .from(context)
                    .inflate(R.layout.save_location_layout, null))
                .setTitle(R.string.save_location_dialog_title)
                .setCancelable(true)
                .setPositiveButton(R.string.save_location_dialog_action_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.save_location_dialog_action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
    }

    private AlertDialog getLoginDialog() {
        Context context = MainActivity.this;

        return new AlertDialog.Builder(context)
                .setView(LayoutInflater
                        .from(context)
                        .inflate(R.layout.login_layout, null))
                .setTitle(R.string.drawer_sign_in)
                .setCancelable(true)
                .setPositiveButton(R.string.login_submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.login_register, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        getRegistrationDialog().show();
                    }
                })
                .create();
    }

    private AlertDialog getRegistrationDialog() {
        Context context = MainActivity.this;

        return new AlertDialog.Builder(context)
                .setView(LayoutInflater
                        .from(context)
                        .inflate(R.layout.register_layout, null))
                .setTitle(R.string.register_title)
                .setCancelable(true)
                .setPositiveButton(R.string.login_register, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton(R.string.save_location_dialog_action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
    }
}

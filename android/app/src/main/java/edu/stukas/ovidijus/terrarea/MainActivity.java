package edu.stukas.ovidijus.terrarea;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import edu.stukas.ovidijus.terrarea.data.MarkingMode;
import edu.stukas.ovidijus.terrarea.data.Territory;
import edu.stukas.ovidijus.terrarea.database.TerrareaDatabaseHelper;
import edu.stukas.ovidijus.terrarea.fragment.SettingsFragment;
import edu.stukas.ovidijus.terrarea.fragment.TerritoryListFragment;
import edu.stukas.ovidijus.terrarea.handler.GoogleMapButtonVisibilityHandler;
import edu.stukas.ovidijus.terrarea.handler.GoogleMapTerritoryHandler;
import edu.stukas.ovidijus.terrarea.handler.GooglePlacesSearchHandler;
import edu.stukas.ovidijus.terrarea.network.LoginTask;
import edu.stukas.ovidijus.terrarea.util.ConversionUtil;
import edu.stukas.ovidijus.terrarea.util.TerrareaSettingKeys;

/**
 * @author Ovidijus Stukas
 */

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private TerrareaDatabaseHelper databaseHelper;
    private GooglePlacesSearchHandler googlePlacesSearchHandler;
    private GoogleMapTerritoryHandler googleMapTerritoryHandler;
    private GoogleMapButtonVisibilityHandler googleMapButtonVisibilityHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        databaseHelper = new TerrareaDatabaseHelper(this);

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
                if (googleMapTerritoryHandler.getSessionPositions().size() < 3)
                {
                    Toast.makeText(MainActivity.this, getString(R.string.need_three_points), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                googleMapButtonVisibilityHandler.applicationChanged(MarkingMode.OFF);

                AlertDialog saveLocationDialog = getSaveLocationDialog();
                saveLocationDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialogInterface) {
                        AlertDialog dialog = (AlertDialog) dialogInterface;
                        Territory territory = googleMapTerritoryHandler.getTerritory();
                        if (territory != null)
                        {
                            EditText id = (EditText) dialog.findViewById(R.id.input_territory_id);
                            EditText name = (EditText) dialog.findViewById(R.id.input_territory_name);

                            id.setText(String.valueOf(territory.getId()));
                            name.setText(territory.getName());
                        }

                        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LinearLayout layout = (LinearLayout) view.getParent().getParent();
                                EditText id = (EditText) layout.findViewById(R.id.input_territory_id);
                                EditText name = (EditText) layout.findViewById(R.id.input_territory_name);
                                EditText area = (EditText) layout.findViewById(R.id.input_territory_area);
                                EditText perimeter = (EditText) layout.findViewById(R.id.input_territory_perimeter);

                                Spinner areaSpinner = (Spinner) layout.findViewById(R.id.spinner_territory_area);
                                Spinner perimeterSpinner = (Spinner) layout.findViewById(R.id.spinner_territory_perimeter);

                                if (name.getText().length() == 0)
                                {
                                    Toast.makeText(MainActivity.this, getString(R.string.territory_validation), Toast.LENGTH_SHORT)
                                            .show();
                                    return;
                                }

                                Territory territory = new Territory(name.getText().toString(),
                                        ConversionUtil.ConvertAreaToMeters(areaSpinner.getSelectedItemPosition(), Double.parseDouble(area.getText().toString())),
                                        ConversionUtil.ConvertPerimeterToMeters(perimeterSpinner.getSelectedItemPosition(), Double.parseDouble(perimeter.getText().toString())));
                                territory.setPositions(googleMapTerritoryHandler.getSessionPositions());

                                googleMapTerritoryHandler.clearSession();
                                if (id.getText().length() == 0)
                                {
                                    databaseHelper.insertTerritory(territory);
                                    Toast.makeText(view.getContext(), getString(R.string.territory_saved), Toast.LENGTH_SHORT)
                                            .show();
                                }
                                else
                                {
                                    territory.setId(Long.parseLong(id.getText().toString()));
                                    databaseHelper.updateTerritory(territory);
                                    Toast.makeText(view.getContext(), getString(R.string.territory_updated), Toast.LENGTH_SHORT)
                                            .show();
                                }


                                dialogInterface.dismiss();
                            }
                        });
                    }
                });
                saveLocationDialog.show();
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

        final View view = LayoutInflater.from(context).inflate(R.layout.save_location_layout, null);
        EditText name = (EditText) view.findViewById(R.id.input_territory_name);
        final EditText area = (EditText) view.findViewById(R.id.input_territory_area);
        final EditText perimeter = (EditText) view.findViewById(R.id.input_territory_perimeter);

        Spinner areaType = (Spinner) view.findViewById(R.id.spinner_territory_area);
        Spinner perimeterType = (Spinner) view.findViewById(R.id.spinner_territory_perimeter);

        areaType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                double areaAmount = googleMapTerritoryHandler.getSessionArea();
                area.setText(String.valueOf(ConversionUtil.ConvertArea(i, areaAmount)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        perimeterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                double perimeterAmount = googleMapTerritoryHandler.getSessionPerimeter();
                perimeter.setText(String.valueOf(ConversionUtil.ConvertPerimeter(i, perimeterAmount)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int areaTypePosition = Integer.parseInt(preferences.getString(TerrareaSettingKeys.SETTING_KEY_AREA_POSITION, "0"));
        int perimeterTypePosition = Integer.parseInt(preferences.getString(TerrareaSettingKeys.SETTING_KEY_PERIMETER_POSITION, "0"));

        areaType.setSelection(areaTypePosition);
        perimeterType.setSelection(perimeterTypePosition);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TextInputLayout textInputLayout =  (TextInputLayout) view.findViewById(R.id.input_territory_name_layout);
                if (charSequence.length() == 0)
                {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError(getString(R.string.empty_territory_name));
                }
                else
                {
                    textInputLayout.setErrorEnabled(false);
                    textInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return new AlertDialog.Builder(context)
                .setView(view)
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
                        googleMapTerritoryHandler.clearSession();
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
                        AlertDialog dialog = (AlertDialog) dialogInterface;

                        EditText login = (EditText) dialog.findViewById(R.id.input_user_username);
                        EditText password = (EditText) dialog.findViewById(R.id.input_user_password);

                        String requestUrl = String.format("%s?username=%s&password=%s",
                                "http://158.129.18.239:8080/user/login",
                                login.getText().toString(), password.getText().toString());

                        LoginTask restTask = new LoginTask(MainActivity.this);
                        restTask.execute(requestUrl);
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

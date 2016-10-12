package edu.stukas.ovidijus.terrarea.handler;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

/**
 * @author Ovidijus Stukas
 */

public class GooglePlacesSearchHandler implements PlaceSelectionListener {
    public static final int REQUEST_SELECT_PLACE_CODE = 1;

    private Activity activity;
    private GoogleMap googleMap;

    public GooglePlacesSearchHandler(Activity activity, GoogleMap googleMap) {
        this.activity = activity;
        this.googleMap = googleMap;
    }

    public void onSearchClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                .build(activity);

            activity.startActivityForResult(intent, REQUEST_SELECT_PLACE_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16.0f);
        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(activity, "Failed to select place", Toast.LENGTH_SHORT).show();
    }
}

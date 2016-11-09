package edu.stukas.ovidijus.terrarea.handler;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import edu.stukas.ovidijus.terrarea.R;
import edu.stukas.ovidijus.terrarea.data.MarkingMode;
import edu.stukas.ovidijus.terrarea.data.Territory;

/**
 * @author Ovidijus Stukas
 */

public class GoogleMapTerritoryHandler implements GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener {

    private Context context;
    private GoogleMap googleMap;
    private Polygon sessionPolygon;
    private Polyline sessionPolyline;
    private List<Marker> sessionMarkers;

    private double sessionArea;
    private double sessionPerimeter;
    private Territory territory;

    public static GoogleMapTerritoryHandler instance;

    public GoogleMapTerritoryHandler(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        this.sessionMarkers = new ArrayList<>();
        instance = this;
    }

    public void startSession() {
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapClickListener(this);
    }

    public void clearSession() {
        sessionArea = 0;
        googleMap.clear();
        sessionPerimeter = 0;
        sessionMarkers.clear();
        sessionPolyline = null;
        territory = null;
        googleMap.setOnMarkerClickListener(null);
        googleMap.setOnMarkerDragListener(null);
        googleMap.setOnMapClickListener(null);
    }

    private void drawTerritory() {
        List<LatLng> positions = getSessionPositions();

        sessionPolygon = googleMap.addPolygon(new PolygonOptions()
                .addAll(positions)
                .add(positions.get(0))
                .strokeWidth(0.0f)
                .fillColor(ContextCompat.getColor(context, R.color.colorTerFill))
                .geodesic(true));

        sessionArea = SphericalUtil.computeArea(sessionPolygon.getPoints());
        sessionPerimeter = SphericalUtil.computeLength(sessionPolygon.getPoints());
    }

    private void updateSessionPositions() {
        List<LatLng> positions = getSessionPositions();

        if (sessionPolygon != null)
            sessionPolygon.remove();

        if (positions.size() > 2) {
            positions.add(positions.get(0));
            sessionPolyline.setPoints(positions);
            drawTerritory();
        } else {
            sessionPolyline.setPoints(positions);
        }
    }

    private void addSessionLocation(List<LatLng> positions)
    {
        for (LatLng latLng : positions)
        {
            sessionMarkers.add(googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_white_36dp))
                    .draggable(true)));

            if (sessionPolyline == null) {
                sessionPolyline = googleMap.addPolyline(new PolylineOptions()
                        .add(latLng)
                        .width(3.5f)
                        .color(ContextCompat.getColor(context, R.color.colorTerStroke))
                        .geodesic(true));
            }
            else {
                updateSessionPositions();
            }
        }
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        List<LatLng> positions = new ArrayList<>(1);
        positions.add(latLng);
        addSessionLocation(positions);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        updateSessionPositions();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        updateSessionPositions();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        updateSessionPositions();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (sessionMarkers.contains(marker))
        {
            sessionMarkers.remove(marker);
            updateSessionPositions();
            marker.remove();
        }
        return true;
    }

    public double getSessionArea() {
        return sessionArea;
    }

    public double getSessionPerimeter() {
        return sessionPerimeter;
    }

    public List<LatLng> getSessionPositions() {
        List<LatLng> positions = new ArrayList<>(sessionMarkers.size());
        for(Marker m : sessionMarkers)
            positions.add(m.getPosition());
        return positions;
    }

    public void focusTerritory(Territory territory) {
        clearSession();
        startSession();

        this.territory = territory;
        List<LatLng> positions = territory.getPositions();
        addSessionLocation(positions);

        GoogleMapButtonVisibilityHandler.instance.applicationChanged(MarkingMode.ON);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(positions.get(0), 16.0f));
    }

    public Territory getTerritory()
    {
        return territory;
    }
}

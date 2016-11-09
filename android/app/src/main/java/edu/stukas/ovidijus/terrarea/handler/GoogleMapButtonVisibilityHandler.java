package edu.stukas.ovidijus.terrarea.handler;

import android.support.design.widget.FloatingActionButton;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

import edu.stukas.ovidijus.terrarea.data.MarkingMode;

/**
 * @author Ovidijus Stukas
 */

public class GoogleMapButtonVisibilityHandler implements GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener {

    private MarkingMode markingMode;
    private List<FloatingActionButton> statusOnFABs;
    private List<FloatingActionButton> statusOffFABs;

    public static GoogleMapButtonVisibilityHandler instance;

    public GoogleMapButtonVisibilityHandler() {
        markingMode = MarkingMode.OFF;
        statusOnFABs = new ArrayList<>();
        statusOffFABs = new ArrayList<>();

        instance = this;
    }

    public void registerFAB(FloatingActionButton fab, MarkingMode markingMode) {
        switch (markingMode) {
            case ON:
                statusOnFABs.add(fab);
                break;
            case OFF:
                statusOffFABs.add(fab);
                break;
        }
    }

    @Override
    public void onCameraIdle() {
        changeVisibility();
    }

    @Override
    public void onCameraMove() {
        for (FloatingActionButton fab : statusOffFABs) {
            fab.hide();
        }
        for (FloatingActionButton fab : statusOnFABs) {
            fab.hide();
        }
    }

    public void applicationChanged(MarkingMode markingMode) {
        this.markingMode = markingMode;
        changeVisibility();
    }

    private void changeVisibility() {
        if (this.markingMode == MarkingMode.ON) {
            for (FloatingActionButton fab : statusOffFABs) {
                fab.hide();
            }
            for (FloatingActionButton fab : statusOnFABs) {
                fab.show();
            }
        }
        else {
            for (FloatingActionButton fab : statusOffFABs) {
                fab.show();
            }
            for (FloatingActionButton fab : statusOnFABs) {
                fab.hide();
            }
        }
    }
}

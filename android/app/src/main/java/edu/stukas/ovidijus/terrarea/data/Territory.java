package edu.stukas.ovidijus.terrarea.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * @author Ovidijus Stukas
 */

public class Territory {
    private String name;
    private double area;
    private double perimeter;
    private List<LatLng> positions;

    public Territory() {
    }

    public Territory(String name, double area, double perimeter) {
        this.name = name;
        this.area = area;
        this.perimeter = perimeter;
    }

    public String getName() {
        return name;
    }

    public double getArea() {
        return area;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public List<LatLng> getPositions() {
        return positions;
    }

    public void setPositions(List<LatLng> positions) {
        this.positions = positions;
    }
}

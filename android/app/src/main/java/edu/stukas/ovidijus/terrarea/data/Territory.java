package edu.stukas.ovidijus.terrarea.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ovidijus Stukas
 */

public class Territory {
    private long id;
    private long user_id;
    private String name;
    private double area;
    private double perimeter;
    private List<LatLng> positions;

    public Territory(String name, double area, double perimeter) {
        this.name = name;
        this.area = area;
        this.perimeter = perimeter;

        this.positions = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
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

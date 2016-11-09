package edu.stukas.ovidijus.terrarea.util;

/**
 * @author Ovidijus Stukas
 */

public class ConversionUtil {

    public static double ConvertAreaToMeters(int spinnerPosition, double area)
    {
        double convertedArea = 0;
        switch (spinnerPosition) {
            case 0: //Ha
                convertedArea = area / 0.0001;
                break;
            case 1: //a
                convertedArea = area / 0.01;
                break;
            case 2: //km2
                convertedArea = area * 1000000;
                break;
            case 3: //m2
                convertedArea = area;
                break;
        }

        return convertedArea;
    }

    public static double ConvertArea(int spinnerPosition, double area)
    {
        double convertedArea = 0;
        switch (spinnerPosition) {
            case 0: //Ha
                convertedArea = area * 0.0001;
                break;
            case 1: //a
                convertedArea = area * 0.01;
                break;
            case 2: //km2
                convertedArea = area / 1000000;
                break;
            case 3: //m2
                convertedArea = area;
                break;
        }

        return convertedArea;
    }

    public static double ConvertPerimeterToMeters(int spinnerPosition, double perimeter)
    {
        double convertedPerimeter = 0;
        switch (spinnerPosition) {
            case 0: //m
                convertedPerimeter = perimeter;
                break;
            case 1: //km
                convertedPerimeter = perimeter * 1000;
                break;
        }

        return convertedPerimeter;
    }

    public static double ConvertPerimeter(int spinnerPosition, double perimeter)
    {
        double convertedPerimeter = 0;
        switch (spinnerPosition) {
            case 0: //m
                convertedPerimeter = perimeter;
                break;
            case 1: //km
                convertedPerimeter = perimeter / 1000;
                break;
        }

        return convertedPerimeter;
    }
}

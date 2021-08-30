package com.hsproject.proximity.helper;

public class LocationDistance {

    public double distance(double input_lat1, double input_lon1, double input_lat2, double input_lon2, String input_unit) {
        double lat1, lon1, lat2, lon2;
        String unit;
        lat1 = input_lat1; lon1 = input_lon1;
        lat2 = input_lat2; lon2 = input_lon2;
        unit = input_unit;


        switch (unit) {
            case "" :
                // 마일(Mile) 단위
                double distanceMile = private_distance(lat1, lon1, lat2, lon2, "");
                return (distanceMile);
            case "meter" :
                // 미터(Meter) 단위
                double distanceMeter = private_distance(lat1, lon1, lat2, lon2, "meter");
                return (distanceMeter);
            case "kilometer" :
                // 킬로미터(Kilo Meter) 단위
                double distanceKiloMeter = private_distance(lat1, lon1, lat2, lon2, "kilometer");
                return (distanceKiloMeter);
        }
        return 0;

    }

    private double private_distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }
        return (dist);
    }

    // This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}
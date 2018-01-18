package demo;

public class Haversine {

    public static final double R = 3959; // use 6372.8 in kilometers, 6378137 in meters

    // Calculates the great-circle distance between two points on Earth using their latitudes and longitudes.
    // Source: https://en.wikipedia.org/wiki/Haversine_formula
    public static double distance (double latitude1, double longitude1, double latitude2, double longitude2) {
        double deltaLat = Math.toRadians(latitude2 - latitude1);
        double deltaLon = Math.toRadians(longitude2 - longitude1);

        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));

        return R * c;
    }
}
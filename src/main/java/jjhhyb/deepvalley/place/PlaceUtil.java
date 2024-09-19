package jjhhyb.deepvalley.place;

public class PlaceUtil {

    private static final double[] latLimits = {Math.toRadians(-90), Math.toRadians(90)};
    private static final double[] lonLimits = {Math.toRadians(-180), Math.toRadians(180)};

    public static double[] getBoundingBoxVertexByCircle(double lon, double lat, Long distance) {

        double radLon = Math.toRadians(lon);
        double radLat = Math.toRadians(lat);

        if(radLat < latLimits[0] || radLat > latLimits[1]
        || radLon < lonLimits[0] || radLon > lonLimits[1]) {
            throw new IllegalArgumentException("Invalid position");
        }

        double angular = distance / 6371000.0;

        double minLat = radLat - angular;
        double maxLat = radLat + angular;

        double minLon;
        double maxLon;

        if(minLat > latLimits[0] && maxLat < latLimits[1]) {
            double deltaLon = Math.asin(Math.sin(angular) / Math.cos(radLat));
            minLon = radLon - deltaLon;

            if(minLon < lonLimits[0]) {
                minLon += 2 * Math.PI;
            }

            maxLon = radLon + deltaLon;

            if(maxLon > lonLimits[1]) {
                maxLon -= 2 * Math.PI;
            }

        } else {
            minLat = Math.max(minLat, latLimits[0]);
            maxLat = Math.min(maxLat, latLimits[1]);
            minLon = lonLimits[0];
            maxLon = lonLimits[1];
        }
        return new double[]{Math.toDegrees(minLon), Math.toDegrees(minLat), Math.toDegrees(maxLon), Math.toDegrees(maxLat)};
    }
}

package fr.jlm2017.pap.GeoLocalisation;

/**
 * Created by thoma on 23/02/2017.
 * Project : Porte à Porte pour JLM2017
 */

public class GeoData {
    public GeoComponents components = new GeoComponents();
    public int confidence=0;
    public String formatted="";
    public GeoGeometry geometry = new GeoGeometry();

    public class GeoComponents {
        public String city="";
        public String house_number="";
        public String road="";
        public String village="";
    }

    public class GeoGeometry {
        public double lat=0;
        public double lng=0;
    }

    @Override
    public String toString() {
        return "GeoData{ city ='" + components.city + '\'' +
                ", road='" + components.road + '\'' +
                ", house_number ='" + components.house_number + '\'' +
                ", latitude =" + geometry.lat +
                ", longitude='" + geometry.lng + "'}";
    }

    //Conversion des degrés en radian
    public static double convertRad(double input){
        return (Math.PI * input)/180;
    }

    public static double distanceGPS(double lat_a_degre, double lon_a_degre, double lat_b_degre, double lon_b_degre){

        double R = 6378000; //Rayon de la terre en mètre

        double lat_a = convertRad(lat_a_degre);
        double lon_a = convertRad(lon_a_degre);
        double lat_b = convertRad(lat_b_degre);
        double lon_b = convertRad(lon_b_degre);

       double d = R * (Math.PI/2 - Math.asin( Math.sin(lat_b) * Math.sin(lat_a) + Math.cos(lon_b - lon_a) * Math.cos(lat_b) * Math.cos(lat_a)));
        return d;
    }
}

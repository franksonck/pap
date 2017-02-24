package fr.jlm2017.pap.GeoLocalisation;

/**
 * Created by thoma on 23/02/2017.
 * Project : Porte à Porte pour JLM2017
 */

public class GeoData {
    public GeoComponents components;
    public int confidence=0;
    public String formatted="";
    public GeoGeometry geometry;

    public class GeoComponents {
        public String city="";
        public String house_number="";
        public String road="";
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
}

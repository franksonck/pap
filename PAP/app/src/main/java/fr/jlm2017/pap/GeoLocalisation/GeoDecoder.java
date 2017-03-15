package fr.jlm2017.pap.GeoLocalisation;

import fr.jlm2017.pap.utils.Encoder;

/**
 * Created by thoma on 23/02/2017.
 * Project : Porte à Porte pour JLM2017
 */


public class GeoDecoder {

    private String baseURL = "https://api.opencagedata.com/geocode/v1/json?q=";
    private String optionsNokey = "&pretty=1&no_record=1&min_confidence=9&no_annotations=1&key=";
    private String key = "OWFTFxbFeu9-lrMEHYvOFlB36ZoTuZ9E3nySC5JjAITUOKiHqMSSig==";
    private String options = optionsNokey + Encoder.decodeString(key);
    private String option_FR = "&countrycode=fr,bl,gf,gp,mf,mq,nc,pf,pm,re,tf,wf,yt";
    public boolean fr;
    public GeoDecoder(boolean en_france_uniquement) {
        fr=en_france_uniquement;
    }

    GeoDecoder() {
        fr=false;
    }

    String getReverseURL(double latitude, double longitude) {
        if (fr) {
            return baseURL + latitude + "," + longitude + option_FR + options;
        } else {
            return baseURL + latitude + "," + longitude + options;
        }
    }
    String getForwardURL(String num, String street, String city) {
        if (fr) {
            return baseURL + num +" "+ street +" , "+city  + option_FR + options;
        } else {
            return baseURL + num +" "+ street +" , "+city+ options;
        }
    }

}

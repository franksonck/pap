package fr.jlm2017.pap;

import fr.jlm2017.pap.MongoDB.DataObject;

/**
 * Created by thoma on 15/02/2017.
 */

public class Porte extends DataObject {

    public String adresse;
    public String ville;
    public Boolean ouverte;
    public Boolean revenir;
    public double latitude;
    public double longitude;

    public Porte(String adresse, String ville, Boolean ouverte, Boolean revenir, double latitude, double longitude) {
        this.adresse = adresse;
        this.ville = ville;
        this.ouverte = ouverte;
        this.revenir = revenir;
        this.latitude = latitude;
        this.longitude = longitude;

    }
}

package fr.jlm2017.pap.MongoDB;

import fr.jlm2017.pap.MongoDB.DataObject;

/**
 * Created by thoma on 15/02/2017.
 */

public class Porte extends DataObject {

    public String adresseResume;
    String numS;
    String numA;
    String complement;
    String nom_rue;
    String nom_ville;
    Boolean ouverte;
    Boolean revenir;
    double latitude;
    double longitude;

    public Porte(String adresseResume, String numS, String numA, String complement, String nom_rue, String nom_ville, Boolean ouverte, Boolean revenir, double latitude, double longitude) {
        this.adresseResume = adresseResume;
        this.numS = numS;
        this.numA = numA;
        this.complement = complement;
        this.nom_rue = nom_rue;
        this.nom_ville = nom_ville;
        this.ouverte = ouverte;
        this.revenir = revenir;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

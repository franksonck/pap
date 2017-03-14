package fr.jlm2017.pap.MongoDB;

/**
 * Created by thoma on 15/02/2017.
 */

public class Porte {

    public String id_ ="";
    public String adresseResume="";
    public String numS="";
    public String numA="";
    public String complement="";
    public String nom_rue="";
    public String nom_ville="";
    public Boolean ouverte=false;
    public double latitude=0;
    public double longitude=0;
    public String user_id="";

    public Porte(String adresseResume, String numS, String numA, String complement, String nom_rue, String nom_ville, Boolean ouverte, double latitude, double longitude, String user_id) {
        this.adresseResume = adresseResume;
        this.numS = numS;
        this.numA = numA;
        this.complement = complement;
        this.nom_rue = nom_rue;
        this.nom_ville = nom_ville;
        this.ouverte = ouverte;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user_id = user_id;
    }
}

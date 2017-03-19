package fr.jlm2017.pap.MongoDB;

import android.util.Pair;

/**
 * Created by thoma on 14/02/2017.
 */

public class QueryBuilder {


    /**
     * Specify your MongoLab API here
     * @return
     */
/*    private String getApiKey() {
        return MathTool.decodeString("ccPdsJPdnD-dinB0kFpRz7gBSjf0dMBO5XpaXeS6vG_UOKiHqMSSig==");
    }*/

    /**
     * This constructs the URL that allows you to manage your database,
     * collections and documents
     * @return
     */
    private String getBaseUrl()
    {
        return "https://pap.jlm2017.fr/";
        //return "http://192.168.10.102:8000/";
    }


    /**
     * Builds a complete URL using the methods specified above
     * @return
     */
    String buildPorteSaveURL()
    {
        return getBaseUrl()+"portes";
    }


    /**
     * Formats the contact details for MongoHQ Posting
     * @param contact: Details of the person
     * @return
     */

    String createPorte(Porte contact)
    {
        return String
                .format("{\"porte\"  : {\"adresseResume\": \"%s\", \"complement\": \"%s\",\"nom_rue\": \"%s\",\"nom_ville\": \"%s\",\"numA\": \"%s\",\"numS\": \"%s\", "
                                + "\"ouverte\": \"%b\", "
                                + "\"latitude\": \"%f\","
                                + "\"longitude\": \"%f\", \"person\": \"%s\"}}",
                        contact.adresseResume,contact.complement, contact.nom_rue, contact.nom_ville, contact.numA, contact.numS, contact.ouverte, contact.latitude, contact.longitude,contact.user_id);
    }

    public String buildOAuthURL() {
        return getBaseUrl()+"verifier";
    }

    public String buildProcheURL(Pair<Double, Double> first) {
        return getBaseUrl()+"proches/?lat="+first.first+"&lon="+first.second;
    }

    public String buildGetMyPorteURL(String first) {
        return getBaseUrl()+"user_porte/?person="+first;
    }

    public String buildConnexionURL(String token) {
        return getBaseUrl()+"connexion/?device="+token;
    }
    public String buildConnexionTokenHeaderURL() {
        return getBaseUrl()+"connexion/";
    }
}


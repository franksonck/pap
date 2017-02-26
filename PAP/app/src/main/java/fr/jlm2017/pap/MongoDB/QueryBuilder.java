package fr.jlm2017.pap.MongoDB;

import android.util.Pair;

import java.util.ArrayList;

import fr.jlm2017.pap.utils.Encoder;

/**
 * Created by thoma on 14/02/2017.
 */

public class QueryBuilder {

    /**
     * Specify your database name here
     * @return String databaseName
     */
    private String getDatabaseName() {
        return "papjlm";
    }

    /**
     * Specify your MongoLab API here
     * @return
     */
    private String getApiKey() {
        return Encoder.decodeString("ccPdsJPdnD-dinB0kFpRz7gBSjf0dMBO5XpaXeS6vG_UOKiHqMSSig==");
    }

    /**
     * This constructs the URL that allows you to manage your database,
     * collections and documents
     * @return
     */
    private String getBaseUrl()
    {
        return "https://api.mlab.com/api/1/databases/"+getDatabaseName()+"/collections/";
    }

    /**
     * Completes the formating of your URL and adds your API key at the end
     * @return
     */
    private String docApiKeyUrl()
    {
        return "?apiKey="+getApiKey();
    }

    /**
     * Builds a complete URL using the methods specified above
     * @return
     */
    String buildObjectsSaveURL(DataObject objects) //objects = "militants" ou "portes"
    {
        String solution;
        if(objects.getClass()==Militant.class)
        {
            solution = "militants";
        }
        else {
            solution = "portes";
        }
        return buildObjectsSaveURL(solution);
    }

    private String buildObjectsSaveURL(String objects) //objects = "militants" ou "portes"
    {
        return getBaseUrl()+objects+docApiKeyUrl();
    }

    String builObjectGetAllURL(String objects) {
        return buildObjectsSaveURL(objects);
    }

    String builObjectGetFilteredURL(String objects, ArrayList<Pair<String, String>> ids) { // on donne une collection et des couples id, valeur pour filtrer
        String id;
        if(objects.equals("militants"))
        {
            id="militant";
        }
        else {
            id="porte";
        }
        String res = getBaseUrl()+objects+"?q={$and:[";
        for(int i = 0; i<ids.size();i++)
        {
            res =res + "{\""+id+"."+ids.get(i).first+"\":\""+ids.get(i).second+"\"}";
            if(i<ids.size()-1) res=res+",";
        }
        res=res+"]}&apiKey="+getApiKey();
        return res;
    }


    String buildObjectsUpdateURL(DataObject objects) //URL d'update
    {
        String solution;
        if(objects.getClass()==Militant.class)
        {
            solution = "militants";
        }
        else {
            solution = "portes";
        }
        return getBaseUrl()+solution+"/"+objects.id_ +docApiKeyUrl();
    }

    /**
     * Formats the contact details for MongoHQ Posting
     * @param contact: Details of the person
     * @return
     */

    String createObject(DataObject contact) {
        if(contact.getClass()==Militant.class)
        {
            return createMilitant((Militant) contact);
        }
        else {
            return createPorte((Porte) contact);
        }
    }

    private String createMilitant(Militant contact)
    {
        return String
                .format("{\"militant\"  : {\"pseudo\": \"%s\", "
                                + " \"email\": \"%s\", "
                                + "\"password\": \"%s\",\"admin\": \"%b\" }}",
                        contact.pseudo, contact.email, contact.password, contact.admin);
    }

    private String createPorte(Porte contact)
    {
        return String
                .format("{\"porte\"  : {\"adresseResume\": \"%s\", \"complement\": \"%s\",\"nom_rue\": \"%s\",\"nom_ville\": \"%s\",\"numA\": \"%s\",\"numS\": \"%s\", "
                                + "\"ouverte\": \"%b\", "
                                + "\"revenir\": \"%b\", \"latitude\": \"%f\","
                                + "\"longitude\": \"%f\"}}",
                        contact.adresseResume,contact.complement, contact.nom_rue, contact.nom_ville, contact.numA, contact.numS, contact.ouverte, contact.revenir, contact.latitude, contact.longitude);
    }


    //pour l'update on rajoute "set" devant :

    String setObject(DataObject contact) {
        if(contact.getClass()==Militant.class)
        {
            return setMilitant((Militant) contact);
        }
        else {
            return setPorte((Porte) contact);
        }
    }

    private String setMilitant(Militant contact)
    {
        return String
                .format("{\"$set\" : {\"militant\" : {\"pseudo\": \"%s\", "
                                + " \"email\": \"%s\", "
                                + "\"password\": \"%s\",\"admin\": \"%b\" }}}",
                        contact.pseudo, contact.email, contact.password, contact.admin);
    }

    private String setPorte(Porte contact)
    {
        return String
                .format("{\"$set\" : {\"porte\"  : {\"adresseResume\": \"%s\", \"complement\": \"%s\",\"nom_rue\": \"%s\",\"nom_ville\": \"%s\",\"numA\": \"%s\",\"numS\": \"%s\", \"\n" +
                                "                                + \"\\\"ouverte\\\": \\\"%b\\\", \"\n" +
                                "                                + \"\\\"revenir\\\": \\\"%b\\\", \\\"latitude\\\": \\\"%f\\\",\"\n" +
                                "                                + \"\"longitude\": \"%f\"}}}",
                        contact.adresseResume,contact.complement, contact.nom_rue, contact.nom_ville, contact.numA, contact.numS, contact.ouverte, contact.revenir, contact.latitude, contact.longitude);
    }
}


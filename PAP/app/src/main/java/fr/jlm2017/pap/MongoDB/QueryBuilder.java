package fr.jlm2017.pap.MongoDB;

import android.util.Pair;

import java.util.ArrayList;

import fr.jlm2017.pap.Militant;
import fr.jlm2017.pap.Porte;

/**
 * Created by thoma on 14/02/2017.
 */

public class QueryBuilder {

    /**
     * Specify your database name here
     * @return
     */
    public String getDatabaseName() {
        return "papjlm";
    }

    /**
     * Specify your MongoLab API here
     * @return
     */
    public String getApiKey() {
        return "UWo1YV1Xuga85HQdNKS_W252YIvDJB3h";
    }

    /**
     * This constructs the URL that allows you to manage your database,
     * collections and documents
     * @return
     */
    public String getBaseUrl()
    {
        return "https://api.mlab.com/api/1/databases/"+getDatabaseName()+"/collections/";
    }

    /**
     * Completes the formating of your URL and adds your API key at the end
     * @return
     */
    public String docApiKeyUrl()
    {
        return "?apiKey="+getApiKey();
    }

    /**
     * Builds a complete URL using the methods specified above
     * @return
     */
    public String buildObjectsSaveURL(DataObject objects) //objects = "militants" ou "portes"
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

    public String buildObjectsSaveURL(String objects) //objects = "militants" ou "portes"
    {
        return getBaseUrl()+objects+docApiKeyUrl();
    }

    public String builObjectGetAllURL(String objects) {
        return buildObjectsSaveURL(objects);
    }

    public String builObjectGetFilteredURL(String objects, ArrayList<Pair<String,String>> ids) { // on donne une collection et des couples id, valeur pour filtrer
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


    public String buildObjectsUpdateURL(DataObject objects) //URL d'update
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

    public String createObject(DataObject contact) {
        if(contact.getClass()==Militant.class)
        {
            return createMilitant((Militant) contact);
        }
        else {
            return createPorte((Porte) contact);
        }
    }

    public String createMilitant(Militant contact)
    {
        return String
                .format("{\"militant\"  : {\"pseudo\": \"%s\", "
                                + " \"email\": \"%s\", "
                                + "\"password\": \"%s\",\"admin\": \"%b\" }}",
                        contact.pseudo, contact.email, contact.password, contact.admin);
    }

    public String createPorte(Porte contact)
    {
        return String
                .format("{\"porte\"  : {\"adresse\": \"%s\", "
                                + "\"ville\": \"%s\", \"ouverte\": \"%b\", "
                                + "\"revenir\": \"%b\", \"latitude\": \"%f\","
                                + "\"longitude\": \"%f\"}}",
                        contact.adresse, contact.ville, contact.ouverte, contact.revenir, contact.latitude, contact.longitude);
    }


    //pour l'update on rajoute "set" devant :

    public String setObject(DataObject contact) {
        if(contact.getClass()==Militant.class)
        {
            return setMilitant((Militant) contact);
        }
        else {
            return setPorte((Porte) contact);
        }
    }

    public String setMilitant(Militant contact)
    {
        return String
                .format("{\"$set\" : {\"militant\" : {\"pseudo\": \"%s\", "
                                + " \"email\": \"%s\", "
                                + "\"password\": \"%s\",\"admin\": \"%b\" }}}",
                        contact.pseudo, contact.email, contact.password, contact.admin);
    }

    public String setPorte(Porte contact)
    {
        return String
                .format("{\"$set\" : {\"porte\" :{\"adresse\": \"%s\", "
                                + "\"ville\": \"%s\", \"ouverte\": \"%b\", "
                                + "\"revenir\": \"%b\", \"latitude\": \"%f\","
                                + "\"longitude\": \"%f\"}}}",
                        contact.adresse, contact.ville, contact.ouverte, contact.revenir, contact.latitude, contact.longitude);
    }
}


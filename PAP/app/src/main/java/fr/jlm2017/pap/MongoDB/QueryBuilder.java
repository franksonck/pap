package fr.jlm2017.pap.MongoDB;

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
        return "V7bh9JO4qPAMtmJ-hn2MiWW1DY-oHZh6";
    }

    /**
     * This constructs the URL that allows you to manage your database,
     * collections and documents
     * @return
     */
    public String getBaseUrl()
    {
        return "https://api.mongolab.com/api/1/databases/"+getDatabaseName()+"/collections/";
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
    public String buildObjectsSaveURL(String objects) //objects = "militants" ou "portes"
    {
        return getBaseUrl()+objects+docApiKeyUrl();
    }

    /**
     * Formats the contact details for MongoHQ Posting
     * @param contact: Details of the person
     * @return
     */

    public String createObject(DBObject contact) {
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
                .format("{\"document\"  : {\"pseudo\": \"%s\", "
                                + " \"email\": \"%s\", "
                                + "\"password\": \"%s\"}, \"safe\" : true}",
                        contact.pseudo, contact.email, contact.password);
    }

    public String createPorte(Porte contact)
    {
        return String
                .format("{\"document\"  : {\"adresse\": \"%s\", "
                                + "\"ville\": \"%s\", \"ouverte\": \"%b\", "
                                + "\"revenir\": \"%b\", \"latitude\": \"%f\","
                                + "\"longitude\": \"%f\"}, \"safe\" : true}",
                        contact.adresse, contact.ville, contact.ouverte, contact.revenir, contact.latitude, contact.longitude);
    }



}

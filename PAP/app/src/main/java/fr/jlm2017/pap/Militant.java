package fr.jlm2017.pap;

/**
 * Created by thoma on 14/02/2017.
 */
import

import fr.jlm2017.pap.MongoDB.DBObject;

public class Militant extends DBObject {

    public String pseudo;
    public String email;
    public String password;

    public Militant(String email, String password) {
        pseudo = email.substring(0,email.indexOf('@'));
        this.email = email;
        this.password = password;
    }

    public Militant(String pseudo, String email, String password) {
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
    }
}
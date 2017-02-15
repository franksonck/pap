package fr.jlm2017.pap;

/**
 * Created by thoma on 14/02/2017.
 */

import fr.jlm2017.pap.MongoDB.DBObject;

public class Militant extends DBObject {

    public String pseudo;
    public String email;
    public String password;
    public boolean isAdmin;

    public Militant(String email, String password) {
        pseudo = email.substring(0,email.indexOf('@'));
        this.email = email;
        this.password = password;
        isAdmin =false;
    }

    public Militant(String pseudo, String email, String password, boolean isAdmin) {
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "Militant{" +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
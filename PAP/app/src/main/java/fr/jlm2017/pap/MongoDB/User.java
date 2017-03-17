package fr.jlm2017.pap.MongoDB;

import com.google.gson.Gson;

/**
 * Created by thoma on 17/03/2017.
 * Project : Porte à Porte pour JLM2017
 */

public class User {
    public String _id;
    public String last_name;
    public String first_name;

    User() {
        _id="";
        last_name="";
        first_name="";
    }

    static User fromJson(String s) {
        User dw = new Gson().fromJson(s, User.class);
        return dw;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}

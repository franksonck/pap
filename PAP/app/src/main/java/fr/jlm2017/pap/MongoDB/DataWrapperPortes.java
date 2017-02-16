package fr.jlm2017.pap.MongoDB;

import com.google.gson.Gson;

import java.util.List;

import fr.jlm2017.pap.Militant;
import fr.jlm2017.pap.Porte;

/**
 * Created by thoma on 16/02/2017.
 */

public class DataWrapperPortes {
        public List<BigDataPorte> data;

        public static DataWrapperPortes fromJson(String s) {
            return new Gson().fromJson(s, DataWrapperPortes.class);
        }

        public static String IDfromJson(String s){
            BigDataPorte dataWrapper = new Gson().fromJson(s, BigDataPorte.class);
            return dataWrapper._id.$oid;

        }

        public String toString() {
            return new Gson().toJson(this);
        }

    public class BigDataPorte {
        public Identifier _id ;
        public Porte porte;

    }

}

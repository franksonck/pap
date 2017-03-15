package fr.jlm2017.pap.MongoDB;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 16/02/2017.
 */

public class DataWrapperPortes {
        public List<BigDataPorte> data;

        static DataWrapperPortes fromJson(String s) {
            DataWrapperPortes dw = new Gson().fromJson(s, DataWrapperPortes.class);
            return dw;
        }

        public static String IDfromJson(String s){
            BigDataPorte dataWrapper = new Gson().fromJson(s, BigDataPorte.class);
            return dataWrapper.id_;

        }

        public String toString() {
            return new Gson().toJson(this);
        }

    class BigDataPorte {
        public String id_ ="";
        public String adresseResume="";
        public String numS="";
        public String numA="";
        public String complement="";
        public String nom_rue="";
        public String nom_ville="";
        public Boolean ouverte=false;
        public List<Double> location;
        public String user_id="";
    }

}

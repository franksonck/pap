package fr.jlm2017.pap.MongoDB;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by thoma on 16/02/2017.
 */

public class DataWrapperPortes {
        public List<BigDataPorte> data;

        static DataWrapperPortes fromJson(String s) {
            DataWrapperPortes dw = new Gson().fromJson(s, DataWrapperPortes.class);
            for(BigDataPorte big : dw.data) {
                big.porte.id_=big._id.$oid;
            }
            return dw;
        }

        public static String IDfromJson(String s){
            BigDataPorte dataWrapper = new Gson().fromJson(s, BigDataPorte.class);
            return dataWrapper._id.$oid;

        }

        public String toString() {
            return new Gson().toJson(this);
        }

    class BigDataPorte {
        public Identifier _id = new Identifier();
        public Porte porte = new Porte("","","","","","",false,0,0,"");

    }

}

package fr.jlm2017.pap.GeoLocalisation;

import com.google.gson.Gson;

import java.util.List;

import fr.jlm2017.pap.MongoDB.Identifier;
import fr.jlm2017.pap.MongoDB.Militant;

/**
 * Created by thoma on 16/02/2017.
 * Cette classe permet d'interpréter les réponses des requetes effectuées sur la base
 */

class GeoDataWrapper {
        public List<GeoData> results;
        public StatusData status;

        static GeoDataWrapper fromJson(String s) {
            GeoDataWrapper dw = new Gson().fromJson(s, GeoDataWrapper.class);
            for(GeoData data : dw.results)
            {
                if(data.components.house_number==null) {
                    data.components.house_number="";
                }
                if(data.components.city==null) {
                    data.components.city="";
                }
                if(data.components.road==null) {
                    data.components.road="";
                }
            }
            return dw;
        }

        public String toString() {
            return new Gson().toJson(this);
        }

        public class StatusData {
            public int code=0;
            public String message="";
        }

}

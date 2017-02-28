package fr.jlm2017.pap.MongoDB;

/**
 * Created by thoma on 14/02/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class Militant extends DataObject implements Parcelable {

    public String pseudo="";
    public String email="";
    public String password="";
    public boolean admin=false;


    public Militant(String email, String password) {
        pseudo = email.substring(0,email.indexOf('@'));
        this.email = email;
        this.password = password;
        admin =false;
    }

    public Militant(String pseudo, String email, String password, boolean isAdmin) {
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.admin = isAdmin;
    }

    @Override
    public String toString() {
        return "Militant{ pseudo ='" + pseudo + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + admin +
                ", id='" + this.id_ + "'}";
    }
    // pour pouvoir etre Parcelable et s'envoyer avec des Intent
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(pseudo);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(String.valueOf(admin));
        dest.writeString(this.id_);
    }

    public static final Parcelable.Creator<Militant> CREATOR = new Parcelable.Creator<Militant>() {
        @Override
        public Militant createFromParcel(Parcel source) {
            return new Militant(source);
        }

        @Override
        public Militant[] newArray(int size) {
            return new Militant[size];
        }
    };

    public Militant(Parcel in) {
        pseudo = in.readString();
        email = in.readString();
        password = in.readString();
        admin=Boolean.parseBoolean(in.readString());
        this.id_=in.readString();
    }
}
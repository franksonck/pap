package fr.jlm2017.pap.MongoDB;

/**
 * Created by thoma on 24/02/2017.
 * Project : Porte à Porte pour JLM2017
 */

public interface InterfaceReceivedData<T> {

    void onResponseReceived(T result);
}

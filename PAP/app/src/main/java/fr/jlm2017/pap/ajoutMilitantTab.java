package fr.jlm2017.pap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by thoma on 15/02/2017.
 */

public class ajoutMilitantTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_admin_1, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.addMilitant);

        return rootView;
    }
}

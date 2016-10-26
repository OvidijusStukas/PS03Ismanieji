package edu.stukas.ovidijus.terrarea.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import edu.stukas.ovidijus.terrarea.R;

/**
 * @author Ovidijus Stukas
 */

public class SettingsFragment extends TerrareaDialogFragment {

    public SettingsFragment() {
        super(R.layout.settings_layout, R.string.drawer_settings);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new PrefFragment())
                .commit();
    }

    public static class PrefFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.settings);
        }
    }
}

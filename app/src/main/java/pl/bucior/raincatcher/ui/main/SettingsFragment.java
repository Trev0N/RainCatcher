package pl.bucior.raincatcher.ui.main;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import pl.bucior.raincatcher.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_pref);

    }
}

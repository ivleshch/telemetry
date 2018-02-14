package com.ivleshch.telemetry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.ivleshch.telemetry.data.DbContract;

/**
 * Created by Ivleshch on 13.02.2018.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        fillSettings(DbContract.SETTINGS_SERVER_KEY);
        fillSettings(DbContract.SETTINGS_WEBSERVICE_KEY);

    }

    public void fillSettings(String settingsKey){
        String setting = sharedPref.getString(settingsKey, "");
        findPreference(settingsKey).setSummary(setting);
        findPreference(settingsKey).setDefaultValue(setting);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        switch (key) {
            case DbContract.SETTINGS_SERVER_KEY:
                updateSummary(pref);
                break;
            case DbContract.SETTINGS_WEBSERVICE_KEY:
                updateSummary(pref);
                break;
            default:
                break;
        }
    }

    public void updateSummary(Preference pref){
        EditTextPreference etTextSetting = (EditTextPreference) pref;
        String textPhone = etTextSetting.getText();
        pref.setSummary(textPhone);

        sendToServer(textPhone,pref);
    }

    // Save settings
    private void sendToServer(String settingsValue, Preference pref){
//        db.child(Const.CHILD_SETTINGS).child(pref.getKey()).setValue(settingsValue);
    }

}

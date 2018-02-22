package com.ivleshch.telemetry.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.ivleshch.telemetry.R;
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

        fillSettingsString(DbContract.SETTINGS_SERVER_KEY);
        fillSettingsString(DbContract.SETTINGS_WEBSERVICE_KEY);
        fillSettingsBoolean(DbContract.SETTINGS_AUTO_UPDATE_KEY);
        fillSettingsString(DbContract.SETTINGS_AUTO_UPDATE_INTERVAL_KEY);

    }

    public void fillSettingsString(String settingsKey){
        String settingString;
        settingString = sharedPref.getString(settingsKey, "");
        findPreference(settingsKey).setSummary(settingString);
        findPreference(settingsKey).setDefaultValue(settingString);
    }

    public void fillSettingsBoolean(String settingsKey){
        boolean booleanSetting;
        booleanSetting = sharedPref.getBoolean(settingsKey, false);
        findPreference(settingsKey).setDefaultValue(booleanSetting);
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
                updateSummary(pref,false,"","");
                break;
            case DbContract.SETTINGS_WEBSERVICE_KEY:
                updateSummary(pref,false,"","");
                break;
            case DbContract.SETTINGS_AUTO_UPDATE_KEY:
//                updateSummary(pref);
                break;
            case DbContract.SETTINGS_AUTO_UPDATE_INTERVAL_KEY:
                if(sharedPreferences.getString(DbContract.SETTINGS_AUTO_UPDATE_INTERVAL_KEY,"").equals("")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(DbContract.SETTINGS_AUTO_UPDATE_INTERVAL_KEY, "0").apply();
                }else{
                    updateSummary(pref,true,"0","");
                }
                break;
            default:
                break;
        }
    }

    public void updateSummary(Preference pref, boolean setDefaultvalue, String defaultValue, String emptyValue){
        EditTextPreference etTextSetting = (EditTextPreference) pref;
        String text = etTextSetting.getText();
        if(setDefaultvalue && text.equals(emptyValue)){
            pref.setSummary(defaultValue);
            pref.setDefaultValue(defaultValue);
        }else{
            pref.setSummary(text);
        }
    }
}

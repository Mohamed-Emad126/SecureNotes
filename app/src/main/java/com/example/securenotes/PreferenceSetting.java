package com.example.securenotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class PreferenceSetting extends PreferenceFragmentCompat {

    SwitchPreferenceCompat switchPreference;

    Preference textPreference;

    SharedPreferences preferences;

    SharedPreferences.Editor editor;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.setting_prefrence, rootKey);

        switchPreference = findPreference("default_password");

        textPreference = findPreference("input_password");

        preferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        textPreference.setEnabled(preferences.getBoolean( "default_password", false));




        switchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                switchPreference.setChecked(!switchPreference.isChecked());

                editor = preferences.edit();
                editor.putBoolean("default_password",switchPreference.isChecked());
                editor.apply();

                /*Toast.makeText(getActivity().getApplicationContext(), "clicked " +
                                String.valueOf(preferences.getBoolean("default_password",false)),
                        Toast.LENGTH_LONG).show();*/

                if(getParentFragment() instanceof SettingFragment){
                    ((SettingFragment) getParentFragment()).showPasswordDialog();
                }
                return true;
            }
        });



        textPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if(getParentFragment() instanceof SettingFragment){
                    ((SettingFragment) getParentFragment()).showChangPasswordDialog();
                }

                return true;
            }
        });


    }







    public void changePassNow(String newPass){
        editor = preferences.edit();
        editor.putString("input_password", newPass);
        editor.apply();
    }



    public void disableItNow(){
        switchPreference.setChecked(false);
        textPreference.setEnabled(false);

        editor = preferences.edit();
        editor.putBoolean("default_password",false);
        editor.apply();

        editor = preferences.edit();
        editor.putString("input_password", "com.example.securenotes.password");
        editor.apply();
    }



    public void enableItNow(String pass){
        switchPreference.setChecked(true);
        textPreference.setEnabled(true);

        editor = preferences.edit();
        editor.putBoolean("default_password",true);
        editor.apply();

        editor = preferences.edit();
        editor.putString("input_password", pass);
        editor.apply();
    }



}

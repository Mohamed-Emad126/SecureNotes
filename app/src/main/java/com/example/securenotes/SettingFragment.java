package com.example.securenotes;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;

public class SettingFragment extends Fragment {


    Dialog passwordDialog;
    Dialog changePasswordDialog;
    SharedPreferences preferences;

    PreferenceSetting setting;




    public SettingFragment() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("DIALOG", passwordDialog.isShowing());
        outState.putBoolean("CHANGE_DIALOG", changePasswordDialog.isShowing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        setUpToolBar(rootView);

        preferences = PreferenceManager
                .getDefaultSharedPreferences(rootView.getContext());
        setting = (PreferenceSetting)
                getChildFragmentManager()
                        .findFragmentById(R.id.fragment_preference_in_fragment_setting);


        createPasswordDialog();
        createChangePasswordDialog();




        if(savedInstanceState != null && savedInstanceState.getBoolean("DIALOG")){
            passwordDialog.show();
        }
        if(savedInstanceState != null && savedInstanceState.getBoolean("CHANGE_DIALOG"))
            changePasswordDialog.show();



        return rootView;
    }







    private void setUpToolBar(final View rootView) {

        Toolbar toolbar= rootView.findViewById(R.id.fragment_setting_toolbar);

        final AppCompatActivity activity=(AppCompatActivity) getActivity();

        if(activity != null){
            activity.setSupportActionBar(toolbar);
            toolbar.setTitle("Settings");
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(rootView).popBackStack();
                }
            });

        }
    }












    private void createPasswordDialog() {

        passwordDialog = new Dialog(SettingFragment.this.getContext());

        Objects.requireNonNull(passwordDialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        passwordDialog.setContentView(R.layout.dialog_password);

        ((TextView)passwordDialog.findViewById(R.id.title_password_dialog_preference))
                .setText(getString(R.string.set_default_password));

        passwordDialog.setCanceledOnTouchOutside(false);

        //passwordDialog.findViewById(R.id.image_password_dialog).setVisibility(View.VISIBLE);
        passwordDialog.findViewById(R.id.previous_password).setVisibility(View.GONE);

        ((TextInputLayout)passwordDialog.findViewById(R.id.edit)).setHint(getString(R.string.default_password));


        final TextInputEditText text = passwordDialog.findViewById(R.id.dialog_password_preference_text);

        final TextInputLayout layout = passwordDialog.findViewById(R.id.edit);

        passwordDialog.findViewById(R.id.password_cancel_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                passwordDialog.dismiss();
                layout.setErrorEnabled(false);
                text.getText().clear();
                text.clearFocus();

                /*Toast.makeText(getActivity().getApplicationContext(),
                String.valueOf(preferences.getBoolean("default_password",false)),
                        Toast.LENGTH_LONG).show();*/

            }
        });


        passwordDialog.findViewById(R.id.password_done_dialog)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        /*Toast.makeText(getActivity().getApplicationContext(),
                                String.valueOf(preferences.getBoolean("default_password",false)),
                                Toast.LENGTH_LONG).show();*/

                        if(text.getText().toString().length() > 0){

                            //Log.i("IN DONE >0 ","TRUE");

                            if(preferences.getBoolean("default_password", false)){

                                //Log.i("IN DONE defaultpassword","TRUE");

                                if(preferences.getString("input_password",
                                        "com.example.securenotes.password")
                                        .equals(text.getText().toString())){

                                    //Log.i("IN DONE compare","TRUE");

                                    setting.disableItNow();
                                    layout.setErrorEnabled(false);
                                    text.getText().clear();
                                    text.clearFocus();
                                    passwordDialog.dismiss();
                                    Snackbar.make(SettingFragment.this.requireView(),
                                            getString(R.string.disable_default_pasword_snack),
                                            Snackbar.LENGTH_SHORT).show();

                                }
                                else{
                                    //Log.i("IN DONE compare","false");
                                    layout.setErrorEnabled(true);
                                    layout.setError(getString(R.string.wrong_password));
                                }

                            }
                            else{
                               // Log.i("IN DONE defaultpassword","fals");

                                setting.enableItNow(text.getText().toString());
                                layout.setErrorEnabled(false);
                                text.getText().clear();
                                text.clearFocus();
                                passwordDialog.dismiss();
                                Snackbar.make(SettingFragment.this.requireView(),
                                        getString(R.string.enable_default_pasword_snack),
                                        Snackbar.LENGTH_SHORT).show();


                            }
                        }
                        else{
                            //Log.i("IN DONE >0 ","FALSE");
                            layout.setErrorEnabled(true);
                            layout.setError(getString(R.string.empty_pass));
                        }
                    }
                });

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    public void showPasswordDialog(){
        /*Toast.makeText(getActivity().getApplicationContext(),
                String.valueOf(preferences.getBoolean("default_password",false)),
                Toast.LENGTH_LONG).show();*/

        passwordDialog.show();
    }










    private void createChangePasswordDialog() {

        changePasswordDialog = new Dialog(SettingFragment.this.getContext());

        Objects.requireNonNull(changePasswordDialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        changePasswordDialog.setContentView(R.layout.dialog_password);

        ((TextView)changePasswordDialog.findViewById(R.id.title_password_dialog_preference))
                .setText(getString(R.string.Change_default_password));

        changePasswordDialog.setCanceledOnTouchOutside(false);

        changePasswordDialog.findViewById(R.id.image_password_dialog).setVisibility(View.GONE);
        changePasswordDialog.findViewById(R.id.previous_password).setVisibility(View.VISIBLE);

        ((TextInputLayout)changePasswordDialog.findViewById(R.id.edit)).setHint(getString(R.string.new_password));


        final TextInputEditText textNew = changePasswordDialog.findViewById(R.id.dialog_password_preference_text);

        final TextInputEditText textOld = changePasswordDialog.findViewById(R.id.previous_dialog_password_text);

        final TextInputLayout layoutPrev = changePasswordDialog.findViewById(R.id.previous_password);


        changePasswordDialog.findViewById(R.id.password_cancel_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordDialog.dismiss();
                layoutPrev.setErrorEnabled(false);
                textNew.getText().clear();
                textOld.getText().clear();
                textNew.clearFocus();
                textOld.clearFocus();

            }
        });


        changePasswordDialog.findViewById(R.id.password_done_dialog)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(textOld.getText().toString()
                                .equals(preferences.getString(
                                        "input_password", "com.example.securenotes.password"))){
                            setting.changePassNow(textNew.getText().toString());
                            changePasswordDialog.dismiss();
                            layoutPrev.setErrorEnabled(false);
                            textNew.getText().clear();
                            textOld.getText().clear();
                            textNew.clearFocus();
                            textOld.clearFocus();
                            Snackbar.make(SettingFragment.this.requireView(),
                                    getString(R.string.sucess_change), Snackbar.LENGTH_SHORT).show();
                        }
                        else{
                            layoutPrev.setError(getString(R.string.wrong_password));
                            layoutPrev.setErrorEnabled(true);
                        }

                    }
                });

        textOld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layoutPrev.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void showChangPasswordDialog(){

        changePasswordDialog.show();
    }

}

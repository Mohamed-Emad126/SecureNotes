package com.example.securenotes;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import com.example.securenotes.ViewModel_Database.Entities.Notes;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class EditFragment extends Fragment {


    private static final String TAGS_KEY = "TAG_IN_CHIP_GROUP_KEY";
    private static final String DIALOG_KEY = "DIALOG_IS_SHOWN?";
    public static final String NOTE_TITLE = "TITLE_KEY";
    public static final String NOTE_CONTENT = "CONTENT_KEY";
    public static final String NOTE_ID = "ID_KEY";
    public static final String NOTE_COLOR = "COLOR_KEY";
    public static final String NOTE_STATE = "STATE_KEY";
    public static final String NOTE_TIME = "TIME_KEY";
    public static final String NOTE_TAGS= "TAGS_KEY";

    int noteId = -1;

    ArrayList<String> saveChip;
    private TextInputEditText mText;
    private TextInputLayout mTextLayout;
    private TextInputEditText mTitle;
    private TextInputEditText mPassword;
    SwitchMaterial privateSwitch;
    TextInputLayout passwordLayout;
    private ChipGroup mChipGroup;
    private Chip mChip;
    String tag = "";
    Spannable mspanable;
    int hashTagIsComing = 0;
    NavController mNavController;
    OnBackPressedCallback callback;
    AlertDialog discardDialog;
    ClipData mClipData;

    SharedPreferences preferences;






    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            noteId = getArguments().getInt(NOTE_ID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        final ClipboardManager clipboardManager = (ClipboardManager)
                Objects.requireNonNull(getActivity()).getSystemService(Context.CLIPBOARD_SERVICE);

        privateSwitch = rootView.findViewById(R.id.private_switch_material);
        passwordLayout = rootView.findViewById(R.id.edit_password_layout);
        mPassword = rootView.findViewById(R.id.edit_password_text);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        privateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b && (!preferences.getBoolean("default_password", false))){
                    passwordLayout.setVisibility(View.VISIBLE);
                }
                else{
                    passwordLayout.setVisibility(View.GONE);
                }
            }
        });

        discardDialog = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogCustom)
                .setMessage(requireActivity().getResources().getString(R.string.warning))
                .setPositiveButton(getResources().getString(R.string.discard),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hideKeyboard();
                                Navigation.findNavController(rootView).popBackStack();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                .create();
        discardDialog.setCanceledOnTouchOutside(false);

        if(savedInstanceState != null && savedInstanceState.containsKey(DIALOG_KEY)){
            discardDialog.onRestoreInstanceState(savedInstanceState.getBundle(DIALOG_KEY));
        }

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(mText.getText().toString().trim().length() > 0 ||
                        mTitle.getText().toString().trim().length() > 0 ||
                        mChipGroup.getChildCount() > 0){
                    if(thereIsChanges()){
                        discardDialog.show();
                    }
                    else{
                        hideKeyboard();
                        Navigation.findNavController(rootView).popBackStack();
                    }
                }
                else{
                    hideKeyboard();
                    Navigation.findNavController(rootView).popBackStack();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(EditFragment.this, callback);



        setHasOptionsMenu(true);


        mText = rootView.findViewById(R.id.edit_content_input);
        mTitle = rootView.findViewById(R.id.edit_title_input);
        mspanable = mText.getText();
        mChipGroup = rootView.findViewById(R.id.edit_chip_group);
        mTextLayout = rootView.findViewById(R.id.edit_content_layout);

        mTextLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        mTextLayout.setEndIconDrawable(rootView.getResources().getDrawable(R.drawable.ic_copy));
        mTextLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClipData = ClipData.newPlainText("text", mText.getText().toString().trim());
                clipboardManager.setPrimaryClip(mClipData);
                Snackbar.make(rootView, R.string.copied, Snackbar.LENGTH_SHORT).show();
            }
        });

        if(mText.getText().toString().length() != 0 ){
            mTextLayout.setEndIconVisible(true);
        }
        else{
            mTextLayout.setEndIconVisible(false);
        }


        /*
          restore the chips that was included in the chip group before Rotation
         */
        if(savedInstanceState != null && savedInstanceState.containsKey(TAGS_KEY)){
            saveChip = savedInstanceState.getStringArrayList(TAGS_KEY);

            if(saveChip != null && (!saveChip.isEmpty())){
                for(int i=0 ; i<saveChip.size(); i++){
                    chipCreator(rootView , saveChip.get(i));
                }
            }
        }
        else if(getArguments() != null){
            saveChip = getArguments().getStringArrayList(NOTE_TAGS);

            if(saveChip != null && (!saveChip.isEmpty())){
                for(int i=0 ; i<saveChip.size(); i++){
                    chipCreator(rootView , saveChip.get(i));
                }
            }

        }


        mText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int end, int count) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if(mText.getText().toString().length() != 0 ){
                    mTextLayout.setEndIconVisible(true);
                }
                else{
                    mTextLayout.setEndIconVisible(false);
                }
                mspanable = mText.getText();
                String startChar = null;

                try{
                    startChar = Character.toString(s.charAt(start));
                    //Log.i(getClass().getSimpleName(), "CHARACTER OF NEW WORD: " + startChar);
                }
                catch(Exception ex){
                    startChar = "";
                }

                if (startChar.equals("#")) {
                    changeTheColor(s.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                    return;
                }

                if(startChar.equals("")){

                    if(hashTagIsComing != 0){
                        chipCreator(rootView , tag);
                    }
                    hashTagIsComing = 0;
                    tag = "";
                    return;

                }

                if(hashTagIsComing != 0) {
                    tag = s.toString().substring(start);
                    changeTheColor(s.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                }


            }
            private void changeTheColor(String s, int start, int end) {
                mspanable.setSpan(new ForegroundColorSpan(
                        getResources().getColor(android.R.color.holo_red_dark)),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setUpToolBar(rootView);

        if(getArguments() != null){
            mTitle.setText(getArguments().getString(NOTE_TITLE,""));
            mText.setText(getArguments().getString(NOTE_CONTENT, ""));
            privateSwitch.setChecked(getArguments().getBoolean(NOTE_STATE));
            if(getArguments().getBoolean(NOTE_STATE)){
                mPassword.setText(getArguments().getString(NOTE_COLOR));
            }

        }

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
    }

    private void chipCreator(View rootView , String tag) {
        mChip = new Chip(rootView.getContext());
        mChip.setCloseIconEnabled(true);
        mChip.setText(tag);
        mChip.setChipBackgroundColorResource(android.R.color.white);
        mChip.setChipStrokeColorResource(android.R.color.black);
        mChip.setChipStrokeWidth(1);
        mChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChipGroup.removeView(view);

            }
        });
        mChipGroup.addView(mChip);
    }

    private void setUpToolBar(final View rootView) {
        MaterialToolbar toolbar= rootView.findViewById(R.id.edit_tool_bar);

        final AppCompatActivity activity=(AppCompatActivity) getActivity();

        if(activity != null){
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setTitle("");
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if((mText.getText().toString().trim().length() > 0 ||
                            mTitle.getText().toString().trim().length() > 0 ||
                            mChipGroup.getChildCount() > 0)){

                        if(thereIsChanges()){
                            discardDialog.show();
                        }
                        else{
                            hideKeyboard();
                            Navigation.findNavController(rootView).popBackStack();
                        }
                    }
                    else{
                        hideKeyboard();
                        Navigation.findNavController(rootView).popBackStack();
                    }
                }
            });

        }
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if(mChipGroup.getChildCount() > 0){
            saveChip = new ArrayList<>();
            for(int i=0 ; i<mChipGroup.getChildCount(); i++){
                saveChip.add( ((Chip)mChipGroup.getChildAt(i)).getText().toString() );
            }
            outState.putStringArrayList(TAGS_KEY, saveChip);
        }
        outState.putBundle(DIALOG_KEY, discardDialog.onSaveInstanceState());

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.fragment_edit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.edit_menu_done){
            if ( !emptyNote()){

                if(thereIsChanges()){
                    if(noteId != -1){
                        Notes note = getNote();
                        note.setNoteId(noteId);
                        note.setTime(new Date(getArguments().getLong(NOTE_TIME)));
                        NotesAsyncTasks notesAsyncTasks =
                                new NotesAsyncTasks(EditFragment.this.getContext(),
                                        "update",
                                        note,
                                        getTags()
                                );
                        notesAsyncTasks.execute();
                        hideKeyboard();
                        Navigation.findNavController(EditFragment.this.requireView() ).navigateUp();
                    }
                    else{
                        NotesAsyncTasks notesAsyncTasks =
                                new NotesAsyncTasks(EditFragment.this.getContext(),
                                        "Insertion",
                                        getNote(),
                                        getTags()
                                );
                        notesAsyncTasks.execute();
                        hideKeyboard();
                        Navigation.findNavController(EditFragment.this.requireView() ).navigateUp();
                    }
                }
                else{
                    hideKeyboard();
                    Navigation.findNavController(EditFragment.this.requireView() ).navigateUp();
                }

            }
            else{
                Snackbar.make(EditFragment.this.requireView(),
                        getResources().getString(R.string.empty_discarded),
                        Snackbar.LENGTH_SHORT).show();
                hideKeyboard();
                Navigation.findNavController(EditFragment.this.requireView() ).navigateUp();

            }
        }
        else if(item.getItemId() == R.id.edit_menu_delete){
            if(noteId != -1){
                final Notes note = getNote();
                note.setNoteId(noteId);
                new NotesAsyncTasks(EditFragment.this.getContext(), "delete", note,null).execute();

                note.setTime(new Date(getArguments().getLong(NOTE_TIME)));

                Snackbar.make(EditFragment.this.requireView(),
                        getResources().getString(R.string.deleted),
                        Snackbar.LENGTH_LONG).setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new NotesAsyncTasks(EditFragment.this.getContext(),
                                "Insertion",
                                note,
                                getTags()
                        ).execute();
                    }
                }).show();


                hideKeyboard();
                Navigation.findNavController(EditFragment.this.requireView() ).navigateUp();

            }
            else{
                if(emptyNote()){
                    hideKeyboard();
                    Navigation.findNavController(EditFragment.this.requireView() ).navigateUp();
                }
                else{
                    discardDialog.show();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null && inputMethodManager.isActive()) {

            inputMethodManager.hideSoftInputFromWindow(
                    EditFragment.this.requireView().getWindowToken(),
                    0);
        }
    }

    private boolean emptyNote() {
        if ( mTitle.getText().toString().trim().length() > 0 ||
             mText.getText().toString().trim().length() > 0 ){

            return false;
        }
        return true;
    }

    private Notes getNote(){
        String title = mTitle.getText().toString().trim();
        String content = mText.getText().toString().trim();
        Date date = new Date();

        String password = mPassword.getText().toString();

        if(preferences.getBoolean("default_password", false)){
            password = preferences.getString("input_password","com.example.securenotes.password");
        }

        return new Notes(title, content, date, password ,privateSwitch.isChecked() );
    }

    private ArrayList<String> getTags(){
        ArrayList<String> tagChips = new ArrayList<>();
        for(int i=0 ; i<mChipGroup.getChildCount(); i++){
            tagChips.add( ((Chip)mChipGroup.getChildAt(i)).getText().toString());
        }
        return tagChips;
    }

    private boolean thereIsChanges(){
        if(getArguments()!=null){
            if(getArguments().getString(NOTE_TITLE,"").equals(mTitle.getText().toString().trim())
                && getArguments().getString(NOTE_CONTENT, "").equals(mText.getText().toString().trim())
                    && getArguments().getBoolean(NOTE_STATE) == privateSwitch.isChecked()
                        && isTagsEquals()
            ){

                if(privateSwitch.isChecked() && (!getArguments().getString(NOTE_COLOR).equals(mPassword.getText().toString()))){
                    return true;
                }
                else{
                    return false;
                }
            }


                /* mTitle.setText(getArguments().getString(NOTE_TITLE,""));
            mText.setText(getArguments().getString(NOTE_CONTENT, ""));

            privateSwitch.setChecked(getArguments().getBoolean(NOTE_STATE));*/
        }
        return true;
    }

    private boolean isTagsEquals(){
        ArrayList<String> tags = getTags();
        ArrayList<String> getterTags = getArguments().getStringArrayList(NOTE_TAGS);

        if(tags.size() != getterTags.size()){
            return false;
        }
        for(int i=0; i<tags.size(); i++){
            if(!getterTags.get(i).equals(tags.get(i))){
                return false;
            }
        }
        return true;
    }


}

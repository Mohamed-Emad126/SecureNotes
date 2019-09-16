package com.example.securenotes;


import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.securenotes.ViewModel_Database.Entities.Notes;
import com.example.securenotes.ViewModel_Database.Entities.NotesWithTags;
import com.example.securenotes.ViewModel_Database.Entities.Tags;
import com.example.securenotes.ViewModel_Database.MainViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MainFragment extends Fragment {

    boolean isActionMode = false;
    private final String ACTION_MODE_KEY = "ACTION_MODE";
    private final String SELECTED_KEY = "SELECTED_KEY";
    private final String DIALOG_KEY = "DIALOG_SHOWN?";
    private final String PRESSED_NOTE = "PRESSED_NOTE_STATE";
    private final String ABOUT_US_DIALOG = "ABOUT_US_DIALOG_KAY";
    private final String CONTACT_US_DIALOG = "CONTACT_US_DIALOG_KAY";



    public static final String NOTE_TITLE = "TITLE_KEY";
    public static final String NOTE_CONTENT = "CONTENT_KEY";
    public static final String NOTE_ID = "ID_KEY";
    public static final String NOTE_COLOR = "COLOR_KEY";
    public static final String NOTE_STATE = "STATE_KEY";
    public static final String NOTE_TIME = "TIME_KEY";
    public static final String NOTE_TAGS= "TAGS_KEY";

    int pressedPosition = -1;
    private RecyclerView mRecyclerView;
    private NoteAdapter mNotes ;
    private CustomActionMode mActionMode;
    ActionMode actionModeTitle ;
    LinearLayoutManager layoutManager;
    ExtendedFloatingActionButton fab;
    Dialog sDialog;
    Dialog aboutUsDialog;
    Dialog contactUsDialog;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActionMode = new CustomActionMode();
        if(savedInstanceState != null){
            /**
             * check if the user rotate the device while action mode or not
             * if true restore the action mode
             */
            if( savedInstanceState.containsKey(ACTION_MODE_KEY) &&
                    savedInstanceState.getBoolean(ACTION_MODE_KEY) ){
                actionModeTitle = Objects.requireNonNull(MainFragment.this.getActivity()).startActionMode(mActionMode);

                if(savedInstanceState.containsKey(SELECTED_KEY)){
                    mNotes.setSelectedNotes(savedInstanceState.getIntegerArrayList(SELECTED_KEY));
                    setActionModeTitle();
                }
            }
        }
        setHasOptionsMenu(true);
    }

    private void setActionModeTitle() {
        actionModeTitle.setTitle(String.valueOf(mNotes.getSelectedSize()) + " " +
                getResources().getString(R.string.selected));
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if(savedInstanceState!= null && savedInstanceState.containsKey(PRESSED_NOTE)){
            pressedPosition = savedInstanceState.getInt(PRESSED_NOTE);
        }

        sDialog = new Dialog(rootView.getContext());
        Objects.requireNonNull(sDialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);
        sDialog.setContentView(R.layout.dialog_layout);
        sDialog.setCanceledOnTouchOutside(false);
        ((TextInputEditText)sDialog.findViewById(R.id.dialog_password_text))
                .addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((TextInputLayout)sDialog.findViewById(R.id.dialog_password_layout)).setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        createAboutUsDialog(rootView);
        if(savedInstanceState!= null && savedInstanceState.containsKey(ABOUT_US_DIALOG)
                &&savedInstanceState.getBoolean(ABOUT_US_DIALOG)){
            aboutUsDialog.show();
        }



        createContactUsDialog(rootView);
        if(savedInstanceState!= null && savedInstanceState.containsKey(CONTACT_US_DIALOG)
                &&savedInstanceState.getBoolean(CONTACT_US_DIALOG)){
            contactUsDialog.show();
        }




        sDialog.findViewById(R.id.ic_cancel_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextInputEditText)sDialog.findViewById(R.id.dialog_password_text)).getText().clear();
                (sDialog.findViewById(R.id.dialog_password_layout)).clearFocus();
                ((TextInputLayout)sDialog.findViewById(R.id.dialog_password_layout)).setErrorEnabled(false);
                sDialog.dismiss();
            }
        });
        sDialog.findViewById(R.id.ic_done_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // COMPLETED: implement the method
                if(((TextInputEditText)sDialog.findViewById(R.id.dialog_password_text)).getText().toString().equals(
                        mNotes.getNoteAdapter(pressedPosition).getColor())){
                    sDialog.dismiss();
                    gotoEditFragment(rootView, pressedPosition);
                }
                else{
                    ((TextInputLayout)sDialog.findViewById(R.id.dialog_password_layout))
                            .setError(getResources().getString(R.string.error_password));
                }
            }
        });

        if(savedInstanceState!= null && savedInstanceState.containsKey(DIALOG_KEY)){
            sDialog.onRestoreInstanceState(savedInstanceState.getBundle(DIALOG_KEY));
        }

        //Set the ToolBar of the Fragment as SupportedActionBar
        fab = rootView.findViewById(R.id.fragment_main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(actionModeTitle != null)
                    actionModeTitle.finish();

                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_editFragment);
            }
        });

        initializeRecyclerView(rootView);

        setUpToolBar(rootView);


        ViewModelProviders.of(this).get(MainViewModel.class).
                getAllNotesWithTags().observe(this, new Observer<List<NotesWithTags>>() {
            @Override
            public void onChanged(List<NotesWithTags> notesWithTags) {

                if(notesWithTags.size() != 0){
                    rootView.findViewById(R.id.fragment_main_empty_view).setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                else{
                    rootView.findViewById(R.id.fragment_main_empty_view).setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }

                mNotes.setNotesWithTags(notesWithTags);

            }
        });

        return rootView;
    }

    private void createAboutUsDialog(View rootView) {
        aboutUsDialog = new Dialog(rootView.getContext());
        Objects.requireNonNull(aboutUsDialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);
        aboutUsDialog.setContentView(R.layout.about_us_dialog);
        aboutUsDialog.setCanceledOnTouchOutside(false);

        aboutUsDialog.findViewById(R.id.btn_cancel_about_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutUsDialog.dismiss();
            }
        });
    }

    private void createContactUsDialog(View rootView) {
        contactUsDialog = new Dialog(rootView.getContext());
        Objects.requireNonNull(contactUsDialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);
        contactUsDialog.setContentView(R.layout.contact_us_dialog);
        contactUsDialog.setCanceledOnTouchOutside(false);

        contactUsDialog.findViewById(R.id.btn_cancel_contact_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactUsDialog.dismiss();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        pressedPosition = -1;
    }

    private void gotoEditFragment(View rootView, int pressedPosition){
        Bundle bundle = new Bundle();
        bundle.putString(NOTE_TITLE, mNotes.getNoteAdapter(pressedPosition).getTitle());
        bundle.putString(NOTE_CONTENT, mNotes.getNoteAdapter(pressedPosition).getContent());
        bundle.putInt(NOTE_ID, mNotes.getNoteAdapter(pressedPosition).getNoteId());
        bundle.putString(NOTE_COLOR, mNotes.getNoteAdapter(pressedPosition).getColor());

        bundle.putBoolean(NOTE_STATE, mNotes.getNoteAdapter(pressedPosition).isPrivate_());
        bundle.putLong(NOTE_TIME, mNotes.getNoteAdapter(pressedPosition).getTime().getTime());
        bundle.putStringArrayList(NOTE_TAGS, mNotes.getTags(pressedPosition));

        Navigation.findNavController(rootView).navigate(R.id.action_mainFragment_to_editFragment, bundle);

    }


    /**
     * initialize the ToolBar of the Fragment as the Default ToolBar
     * @param rootView the root view of the fragment
     */
    private void setUpToolBar(View rootView) {
        Toolbar toolbar= rootView.findViewById(R.id.fragment_main_toolbar);

        AppCompatActivity activity=(AppCompatActivity) getActivity();

        if(activity != null){
            activity.setSupportActionBar(toolbar);
        }
    }

    /**
     * initialize the RecyclerView and the Adapter
     * @param rootView the root view of the fragment
     */
    private void initializeRecyclerView(final View rootView) {
        mNotes = new NoteAdapter(rootView);

        mRecyclerView=rootView.findViewById(R.id.fragment_main_recycle_view);

        layoutManager = new LinearLayoutManager(rootView.getContext());

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mNotes);

        mRecyclerView.setSaveEnabled(true);

        /**
         * implement the interface in the adapter class
         */
        mNotes.setOnNotesClickListener(new NoteAdapter.OnNotesClickListener() {
            @Override
            public void OnOrdinaryClick(int position) {
                /**
                 * if the user click on item while ActionMode then he/she need to just select it
                 * if the user click on it and the item is selected then he/she want to un select it
                 */
                //Log.i("Action mode running: ", String.valueOf(isActionMode));
                if(isActionMode){
                    //Log.i("item selected: ", String.valueOf(mNotes.isSelected(position)));
                    if(mNotes.isSelected(position)){
                        mNotes.unSelect(position);
                        if(mNotes.getSelectedSize()==0){
                            actionModeTitle.finish();
                        }
                        else{
                            setActionModeTitle();
                        }
                    }
                    else{
                        mNotes.select(position);
                        setActionModeTitle();
                    }
                }
                else{

                    if(mNotes.getNoteAdapter(position).isPrivate_()){
                        pressedPosition = position;
                        sDialog.show();
                    }
                    else{

                    /*Bundle editedNote = new Bundle();
                    editedNote.putString();
                    Navigation.findNavController(rootView).navigate(R.id.action_mainFragment_to_editFragment, );*/
                    //COMPLETED(1): Start EditFragment create it as method
                        gotoEditFragment(rootView, position);
                    }
                }

            }

            @Override
            public void onLongClick(int position) {
                //Log.i("Long Action mode runng:", String.valueOf(isActionMode));
                if(isActionMode){
                    //Log.i("Long item selected: ", String.valueOf(mNotes.isSelected(position)));
                    if(mNotes.isSelected(position)){
                        mNotes.unSelect(position);
                        if(mNotes.getSelectedSize() == 0){
                            actionModeTitle.finish();
                        }
                    }
                    else{
                        mNotes.select(position);
                    }
                    setActionModeTitle();
                }
                else {
                    if(!mNotes.getNoteAdapter(position).isPrivate_()){
                        actionModeTitle = Objects.requireNonNull(MainFragment.this.getActivity())
                                .startActionMode(mActionMode);
                        mNotes.select(position);
                        setActionModeTitle();
                    }
                    else{
                        Snackbar.make(rootView, R.string.selection_denied,Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if(mNotes.getNoteAdapter(viewHolder.getAdapterPosition()).isPrivate_()) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();
                final Notes note = mNotes.getNoteAdapter(position);
                final ArrayList<Tags> tags = (ArrayList<Tags>) mNotes.getTagAdapter(position);

                new NotesAsyncTasks(MainFragment.this.getContext(), "delete",
                        note,
                        null
                        ).execute();

                mNotes.notifyItemRemoved(viewHolder.getAdapterPosition());
                //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(mNotes.getItemCount()), Toast.LENGTH_SHORT).show();

                Snackbar.make(rootView,
                        getResources().getString(R.string.deleted),
                        Snackbar.LENGTH_LONG).setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new NotesAsyncTasks(MainFragment.this.getContext(),
                                "InsertionOne",
                                note,
                                tags
                                ,false
                        ).execute();
                        mRecyclerView.smoothScrollToPosition(position);
                        //mNotes.notifyItemInserted(mNotes.getItemCount()-1);
                    }
                }).show();

            }
        })
                .attachToRecyclerView(mRecyclerView);
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if(isActionMode){
            outState.putBoolean(ACTION_MODE_KEY, isActionMode);
            outState.putIntegerArrayList(SELECTED_KEY, mNotes.getSelectedNotes());
        }
        outState.putInt(PRESSED_NOTE, pressedPosition);
        if(sDialog != null){
            outState.putBundle(DIALOG_KEY, sDialog.onSaveInstanceState());
        }
        if(aboutUsDialog != null && aboutUsDialog.isShowing()){
            outState.putBoolean(ABOUT_US_DIALOG,true);
        }
        if(contactUsDialog != null && contactUsDialog.isShowing()){
            outState.putBoolean(CONTACT_US_DIALOG,true);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //if (item.getItemId() == R.id.settings_main_toolbar)
        if(item.getItemId() == R.id.about_us){
            aboutUsDialog.show();
        }
        else if(item.getItemId() == R.id.contact_us){
            contactUsDialog.show();
        }
        else if(item.getItemId() == R.id.settings_main_toolbar){
            Navigation.findNavController(MainFragment.this.requireView())
                    .navigate(R.id.action_mainFragment_to_settingFragment);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Class that implements the action mode of the fragment
     * using the R.menu.action_mode as the action bar when action mode started
     */
    protected class CustomActionMode implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            isActionMode = true;
            fab.hide(true);
            actionMode.getMenuInflater().inflate(R.menu.action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            if(menuItem.getItemId() == R.id.action_mode_delete){

                final ArrayList<Notes> notes = new ArrayList<>();
                final ArrayList<Tags> tags = new ArrayList<>();

                for(int i=0; i<mNotes.getSelectedSize(); i++){
                    notes.add(mNotes.getNoteAdapter(mNotes.getSelectedNotes().get(i)));
                    tags.addAll(mNotes.getTagAdapter(mNotes.getSelectedNotes().get(i)));
                }
                new NotesAsyncTasks(MainFragment.this.getContext(), "deleteList", notes).execute();
                mNotes.notifyItemRangeRemoved(Collections.min(mNotes.getSelectedNotes()),
                        Collections.max(mNotes.getSelectedNotes()));

                Snackbar.make(MainFragment.this.requireView(),
                        R.string.deleted, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new NotesAsyncTasks(MainFragment.this.getContext(),
                                        "InsertionList",notes, tags).execute();
                            }
                        }).show();

                actionModeTitle.finish();

            }
            else if(menuItem.getItemId() == R.id.select_all){
                if(mNotes.getSelectedSize() != 30){
                    mNotes.selectAll();
                    menuItem.setIcon(R.drawable.ic_all_selected);
                }
                else{
                    mNotes.removeSelection();
                    menuItem.setIcon(R.drawable.ic_select_all);
                }
            }
            else if(menuItem.getItemId() == R.id.home){
                mNotes.removeSelection();
            }
            setActionModeTitle();
            return false;
        }


        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            isActionMode = false;
            mNotes.removeSelection();

            fab.show(true);
        }
    }

}
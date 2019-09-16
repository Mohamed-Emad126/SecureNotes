package com.example.securenotes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securenotes.ViewModel_Database.Entities.Notes;
import com.example.securenotes.ViewModel_Database.Entities.NotesWithTags;
import com.example.securenotes.ViewModel_Database.Entities.Tags;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.CustomViewHolder> {

    private OnNotesClickListener listener;
    private ArrayList<Integer> selected = new ArrayList<>();
    private List<NotesWithTags> notesWithTags;
    ColorStateList oldColors;
    Notes note;
    List<Tags> tags;
    View view;
    Chip mChip;

    public Notes getNoteAdapter(int position) {
        return notesWithTags.get(position).getNote();
    }

    /**
     * weired method to get the tags of a pressed note to send it to the edit fragment
     * @param position the pressed position
     * @return String ArrayList of the tags Name
     */
    public ArrayList<String> getTags(int position) {
        ArrayList<String> mTags = new ArrayList<>();
        for(int i=0 ; i< notesWithTags.get(position).getTags().size() ; i++){
            mTags.add(notesWithTags.get(position).getTags().get(i).getTagName());
        }
        return mTags;
    }

    public List<Tags> getTagAdapter(int position){
        return notesWithTags.get(position).getTags();
    }

    /**
     * select custom element in the adapter and put it in the selected ArrayList
     * @param position the position that you want to select
     */
    public void select(Integer position){
        if(notesWithTags.get(position).getNote().isPrivate_()){
            Snackbar.make(view, R.string.selection_denied,Snackbar.LENGTH_SHORT).show();
        }else{
            selected.add(position);
            notifyItemChanged(position);
        }
        //Log.i("item selected:", String.valueOf(position));
    }


    /**
     * unSelect custom element in the adapter and remove it from the selected ArrayList
     * @param position the position that you want to unselect
     */
    public void unSelect(Integer position){
        //Log.i("item unselected:", String.valueOf(position));
        selected.remove(Integer.valueOf(position));
        //Log.i("size of selected: ", String.valueOf(selected.size()));
        notifyItemChanged(position);
    }

    /**
     * check if the item is curren selected and in the selected ArrayList or not
     * @param position: position in Adapter that checked if selected or not
     * @return if the ArrayList contains the position return true if not return false
     */
    public Boolean isSelected(Integer position){
        return selected.contains(position);
    }

    /**
     * select all items in Adapter but First clear it so you don't duplicate any one
     */
    public void selectAll(){
        selected.clear();
        for(int i=0;i<getItemCount();i++){
            if(!notesWithTags.get(i).getNote().isPrivate_()){
                selected.add(i);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * remove all selected element in the ArrayList
     */
    public void removeSelection(){
        //Log.i("Remove all selected: ",String.valueOf(selected.size()));
        selected.clear();
        //Log.i("after Removed: ",String.valueOf(selected.size()));
        notifyDataSetChanged();
    }

    /**
     * getter of the selected ArrayList
     * @return the Selected ArrayList
     */
    public ArrayList<Integer> getSelectedNotes(){
        return selected;
    }

    public int getSelectedSize(){
        return selected.size();
    }

    public void setSelectedNotes(ArrayList<Integer> selected){
        this.selected=selected;
        notifyDataSetChanged();
    }




    public void setNotesWithTags(List<NotesWithTags> notesWithTags) {
        this.notesWithTags = notesWithTags;
        notifyDataSetChanged();
    }


    public NoteAdapter(View view) {
        this.view = view;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.note_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        note = notesWithTags.get(position).getNote();
        tags = notesWithTags.get(position).getTags();

        if(selected.contains(position)){
                //Log.i("Edit selected itemNum: ", String.valueOf(position));
                holder.mConstraintLayout.setBackgroundResource(R.drawable.action_mode_background);

                holder.mCardView.setCardElevation(8);
        }
        else{
            holder.mConstraintLayout.setBackgroundResource(R.drawable.card_background);
            holder.mCardView.setCardElevation(0);
        }

        holder.noteTitle.setText(note.getTitle());
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a dd/MM/yyyy");
        holder.noteDate.setText(formatter.format(note.getTime()));

        if(note.isPrivate_()){
            holder.noteContent.setText(holder.itemView.getResources().getString(R.string.hiden_content));
            holder.noteContent.setTextColor(holder.itemView.getResources().getColor(R.color.customColorHighlight));
            //holder.noteTitle.setCompoundDrawablesRelativeWithIntrinsicBounds( ,null,null,null);
            holder.mChipGroup.removeAllViews();

            for(int i = 0; i<tags.size(); i++){
                holder.mChipGroup.addView(chipCreator(view, "******"));
            }
        }
        else{
            holder.noteContent.setTextColor(oldColors);
            holder.noteContent.setText(note.getContent());
            holder.mChipGroup.removeAllViews();

            for(int i = 0; i<tags.size(); i++){
                holder.mChipGroup.addView(chipCreator(view, tags.get(i).getTagName()));
            }
        }

    }

    private Chip chipCreator(View rootView , String tag) {
        mChip = new Chip(rootView.getContext());
        mChip.setText(tag);
        mChip.setChipBackgroundColorResource(android.R.color.white);
        mChip.setChipStrokeColorResource(android.R.color.black);
        mChip.setChipStrokeWidth(1);
        return mChip;
    }

    @Override
    public int getItemCount() {
        if(notesWithTags == null){
            return 0;
        }
        return notesWithTags.size();
    }

    /**
     * set the adapter listener to user clicks
     * @param listener the listener of the Adapter
     */
    public void setOnNotesClickListener(OnNotesClickListener listener) {
        this.listener = listener;
    }


    /**
     * Interface that handle user's clicks on the items in Adapter
     * we implement two methods:
     * 1- if the user click ordinary or any click
     * 2- if the user hold (perform Long click on it) the item in the RecyclerView
     */
    public interface OnNotesClickListener{
        /**
         * if the user click on item this method will be implemented to handle the action
         * that will be performed
         * @param position position of the item in adapter
         */
        void OnOrdinaryClick(int position);

        /**
         * if the user hold any item item this method will be implemented to handle the action
         * that will start the action mode callback {@link MainFragment.CustomActionMode}
         * @param position position of the item in adapter
         */
        void onLongClick(int position);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout mConstraintLayout;
        public TextView noteContent;
        public CardView mCardView;
        public TextView noteDate;
        public TextView noteTitle;
        public ChipGroup mChipGroup;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            mCardView = itemView.findViewById(R.id.note_card);
            mConstraintLayout = itemView.findViewById(R.id.note_constraint_layout);
            noteContent = itemView.findViewById(R.id.note_content);
            noteDate = itemView.findViewById(R.id.note_date);
            noteTitle = itemView.findViewById(R.id.note_title);
            mChipGroup = itemView.findViewById(R.id.chips_layout);
            oldColors =  noteContent.getTextColors();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnOrdinaryClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onLongClick(getAdapterPosition());
                    return true;
                }
            });

        }
    }

}

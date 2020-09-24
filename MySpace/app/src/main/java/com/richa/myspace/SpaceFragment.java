package com.richa.myspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.richa.myspace.model.Note;
import com.richa.myspace.note.AddNote;
import com.richa.myspace.note.NoteDetails;


public class SpaceFragment extends Fragment {

    RecyclerView noteLists;
    ImageView addNote;
    TextView name;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirestoreRecyclerAdapter<Note,NoteViewHolder> noteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_space, container, false);

        noteLists=view.findViewById(R.id.notelist);
        addNote=view.findViewById(R.id.addNote);
        name=view.findViewById(R.id.name);

        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();

        Query query=fStore.collection("Notes").document(user.getUid()).collection("myNotes")
            .orderBy("DateTime",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> allNotes=new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();

        noteAdapter=new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {
                noteViewHolder.noteTitle.setText(note.getTitle());

                noteViewHolder.noteDateTime.setText(note.getDateTime());
                noteViewHolder.noteContent.setText(note.getContent());

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i= new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("Title",note.getTitle());
                        i.putExtra("DateTime",note.getDateTime());
                        i.putExtra("Content",note.getContent());
                        v.getContext().startActivity(i);
                    }
                });

                ImageView menuIcon=noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(View v) {
                        final String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu=new PopupMenu(v.getContext(),v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference docRef=fStore.collection("Notes").document(user.getUid()).collection("myNotes").document(docId);
                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),"Error in Deleting Note!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        menu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        noteLists.setAdapter(noteAdapter);

        if (user.isAnonymous()){
            name.setText("Guest");
        } else {
            name.setText(user.getDisplayName());
        }

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AddNote.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView noteTitle,noteContent,noteDateTime;
        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle=itemView.findViewById(R.id.titles);
            noteContent=itemView.findViewById(R.id.content);
            noteDateTime=itemView.findViewById(R.id.datetime);
            mCardView=itemView.findViewById(R.id.noteCard);
            view=itemView;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (noteAdapter !=null){
            noteAdapter.stopListening();
        }
    }

}
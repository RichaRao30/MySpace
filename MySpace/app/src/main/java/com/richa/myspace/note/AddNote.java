package com.richa.myspace.note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.richa.myspace.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddNote extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseUser user;

    private TextView noteDateTime;
    private ImageView imageBack;
    private ImageView imageSave;
    private ImageView imageAudio;
    EditText noteTitle,noteContent;
    ProgressBar progressBarSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        fStore=FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();

        imageBack=findViewById(R.id.imageBack);
        imageSave=findViewById(R.id.imageSave);
        imageAudio=findViewById(R.id.imageAudio);
        progressBarSave=findViewById(R.id.progressBar);

        noteTitle=findViewById(R.id.addNoteTitle);
        noteContent=findViewById(R.id.addNoteContent);
        noteDateTime=findViewById(R.id.noteDateTime);

        noteDateTime.setText(
                new SimpleDateFormat("EEEE,dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hi Speak Something");
                try {
                    startActivityForResult(intent,1);
                } catch (ActivityNotFoundException e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String nTitle=noteTitle.getText().toString();
               String nContent=noteContent.getText().toString();
               String nDateTime=noteDateTime.getText().toString();

               if (nTitle.isEmpty() || nContent.isEmpty()){
                   Toast.makeText(AddNote.this,"Cannot Save with Empty Fields!",Toast.LENGTH_SHORT).show();
                   return;
               }

               progressBarSave.setVisibility(View.VISIBLE);

               DocumentReference docref=fStore.collection("Notes").document(user.getUid()).collection("myNotes").document();
               Map<String,Object> note=new HashMap<>();
               note.put("Title",nTitle);
               note.put("DateTime",nDateTime);
               note.put("Content",nContent);

               docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Toast.makeText(AddNote.this,"Note Added Successfully.",Toast.LENGTH_SHORT).show();
                       onBackPressed();
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(AddNote.this,"Error, Try Again!",Toast.LENGTH_SHORT).show();
                       progressBarSave.setVisibility(View.VISIBLE);
                   }
               });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK && null!=data){
                    ArrayList<String> result=
                            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    noteContent.append(" "+result.get(0));
                }
                break;
        }
    }
}
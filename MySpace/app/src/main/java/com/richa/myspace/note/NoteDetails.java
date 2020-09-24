package com.richa.myspace.note;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.richa.myspace.R;

public class NoteDetails extends AppCompatActivity {

    Intent data;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        data=getIntent();

        TextView content=findViewById(R.id.noteDetailsContent);
        TextView title=findViewById(R.id.noteDetailsTitle);
        TextView datetime=findViewById(R.id.noteDetailsDateTime);
        content.setMovementMethod(new ScrollingMovementMethod());

        title.setText(data.getStringExtra("Title"));
        content.setText(data.getStringExtra("Content"));
        datetime.setText(data.getStringExtra("DateTime"));

    }
}
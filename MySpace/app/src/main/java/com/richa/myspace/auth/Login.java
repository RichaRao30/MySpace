package com.richa.myspace.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.richa.myspace.MainActivity;
import com.richa.myspace.R;
import com.richa.myspace.Splash;

public class Login extends AppCompatActivity {

    EditText lEmail,lPassword;
    Button loginBtn,createAccount;
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lEmail=findViewById(R.id.lEmail);
        lPassword=findViewById(R.id.lPassword);
        loginBtn=findViewById(R.id.loginBtn);
        createAccount=findViewById(R.id.createAccount);

        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();

        showWarning();

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Register.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail=lEmail.getText().toString();
                String mPassword=lPassword.getText().toString();

                if (mEmail.isEmpty() || mPassword.isEmpty()){
                    Toast.makeText(Login.this,"All fields are required!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (user.isAnonymous()){
                    fStore.collection("Notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this,"All Temporary Notes Deleted!",Toast.LENGTH_SHORT).show();
                        }
                    });

                    user.delete();
                }

                fAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                       startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this,"Login Failed! "+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    private void showWarning() {
        AlertDialog.Builder warning=new AlertDialog.Builder(this)
                .setTitle("Are you Sure?")
                .setMessage("Logging into existing account will delete all your temporary notes! Create a new account to save them.")
                .setPositiveButton("Create new Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Continue Anyway", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        warning.show();
    }
}
package com.richa.myspace.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.richa.myspace.MainActivity;
import com.richa.myspace.R;

public class Register extends AppCompatActivity {

    EditText rUserName,rUserEmail,rUserPass,rUserConfPass;
    Button register,login;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth=FirebaseAuth.getInstance();

        rUserName=findViewById(R.id.userName);
        rUserEmail=findViewById(R.id.userEmail);
        rUserPass=findViewById(R.id.password);
        rUserConfPass=findViewById(R.id.passwordConfirm);
        register=findViewById(R.id.createAccount);
        login=findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uUserName=rUserName.getText().toString();
                String uUserEmail=rUserEmail.getText().toString();
                String uUserPass=rUserPass.getText().toString();
                String uConfPass=rUserConfPass.getText().toString();

                if (uUserEmail.isEmpty() || uUserName.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty()){
                    Toast.makeText(Register.this,"All fields are required!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!uUserPass.equals(uConfPass)){
                    rUserConfPass.setError("Password does not match!");
                    return;
                }

                AuthCredential credential= EmailAuthProvider.getCredential(uUserEmail,uUserPass);
                fAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Register.this,"Notes are synced",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        FirebaseUser usr= fAuth.getCurrentUser();
                        UserProfileChangeRequest request=new UserProfileChangeRequest.Builder()
                                .setDisplayName(uUserName).build();
                        usr.updateProfile(request);

                      //  startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this,"Failed to Connect! Try Again.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
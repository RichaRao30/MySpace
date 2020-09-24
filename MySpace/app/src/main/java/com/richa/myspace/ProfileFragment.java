package com.richa.myspace;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.richa.myspace.auth.Login;
import com.richa.myspace.auth.Register;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Compressor;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    Button logout,sync;
    FirebaseUser user;
    FirebaseAuth fAuth;
    TextView name,email;
    CircleImageView image;

    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        logout=view.findViewById(R.id.logout);
        name=view.findViewById(R.id.name);
        email=view.findViewById(R.id.email);
        sync=view.findViewById(R.id.sync);
        image=view.findViewById(R.id.image);
        fAuth=FirebaseAuth.getInstance();
        user=fAuth.getCurrentUser();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                        String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions,PERMISSION_CODE);
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    //System OS is less than marshmallow
                    pickImageFromGallery();
                }
            }
        });

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.isAnonymous()){
                    startActivity(new Intent(getContext(), Login.class));
                } else {
                    Toast.makeText(getContext(),"Your Notes are Synced.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });

        if (user.isAnonymous()){
            name.setText("Guest");
            email.setText("guest@guest.com");
        } else {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }

        return view;
    }

    private void pickImageFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    //Handle result of Runtime Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                } else {
                    Toast.makeText(getActivity(),"Permission Denied!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Handle result of Picked Image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK && requestCode==IMAGE_PICK_CODE){
            image.setImageURI(data.getData());
        }
    }

    private void checkUser() {
        if (user.isAnonymous()){
            displayAlert();
        } else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(),Splash.class));
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning=new AlertDialog.Builder(getContext())
                .setTitle("Are you sure you want Logout?")
                .setMessage("You are logged in with a temporary account. Logging out will delete all your data!")
                .setPositiveButton("Sync Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getContext(), Register.class));
                        getActivity().finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getContext(),Splash.class));
                                getActivity().finish();
                            }
                        });
                    }
                });
        warning.show();
    }
}
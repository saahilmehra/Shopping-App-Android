package com.saahil.ssshopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    ImageView ivProfileImage;
    EditText etContact, etAddress, etName;
    TextView tvCancel, tvUpdate, tvChangeProfileImage;

    StorageReference imageReference;
    StorageTask uploadTask;
    DatabaseReference userReference;

    ProgressDialog progressDialog;

    Uri imageUri;
    String imageUrl="", checker="";

    HashMap<String, Object> userMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressDialog=new ProgressDialog(this);

        imageReference= FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        userReference=FirebaseDatabase.getInstance().getReference().child("Users");

        userMap=new HashMap<>();

        ivProfileImage=findViewById(R.id.ivProfileImage);
        etName=findViewById(R.id.etName);
        etContact=findViewById(R.id.etContact);
        etAddress=findViewById(R.id.etAddress);
        tvCancel=findViewById(R.id.tvCancel);
        tvUpdate=findViewById(R.id.tvUpdate);
        tvChangeProfileImage=findViewById(R.id.tvChangeProfileImage);

        displayPreviousData();

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });

        tvChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(imageUri).setAspectRatio(1, 1).start(SettingsActivity.this);
            }
        });
    }

    private void checkValidations() {
        if(TextUtils.isEmpty(etName.getText().toString())){
            etName.setError("Required Field...");
            return;
        }
        if(TextUtils.isEmpty(etContact.getText().toString())){
            etContact.setError("Required Field...");
            return;
        }
        if(TextUtils.isEmpty(etAddress.getText().toString())){
            etAddress.setError("Required Field...");
            return;
        }

        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(checker.equals("clicked")){
            uploadImage();
        }
        else{
            saveUserInfo();
        }
    }

    private void saveUserInfo() {
        userMap.put("name", etName.getText().toString());
        userMap.put("address", etAddress.getText().toString());
        userMap.put("contactOrder", etContact.getText().toString());

        userReference.child(Prevalent.currentOnlineUser.getContact()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        finish();
    }

    private void uploadImage() {
        if(imageUri != null){
            final StorageReference fileRef=imageReference.child(Prevalent.currentOnlineUser.getContact()+".jpg");
            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        imageUrl=task.getResult().toString();

                        userMap.put("image", imageUrl);
                        saveUserInfo();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Image not selected!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void displayPreviousData() {
        final DatabaseReference preUserReference=FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getContact());
        preUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("image").exists()){
                        String imageUrl=dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(imageUrl).placeholder(R.drawable.profile).into(ivProfileImage);
                    }
                    if(dataSnapshot.child("contactOrder").exists()){
                        String orderContact=dataSnapshot.child("contactOrder").getValue().toString();
                        etContact.setText(orderContact);
                    }
                    else{
                        String contact=dataSnapshot.child("contact").getValue().toString();
                        etContact.setText(contact);
                    }
                    if(dataSnapshot.child("address").exists()){
                        String address=dataSnapshot.child("address").getValue().toString();
                        etAddress.setText(address);
                    }

                    String name=dataSnapshot.child("name").getValue().toString();
                    etName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            ivProfileImage.setImageURI(imageUri);
            checker="clicked";
        }
        else{
            Toast.makeText(this, "Failed!!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }
}

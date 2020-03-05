package com.saahil.ssshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText etUsername, etContact, etPassword;
    Button btnRegister;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog=new ProgressDialog(this);

        etUsername=findViewById(R.id.etUsername);
        etContact=findViewById(R.id.etContact);
        etPassword=findViewById(R.id.etPassword);
        btnRegister=findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String name=etUsername.getText().toString().trim();
        String contact=etContact.getText().toString().trim();
        String password=etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            etUsername.setError("Required Field!");
        }
        else if(TextUtils.isEmpty(contact)){
            etContact.setError("Required Field!");
        }
        else if(TextUtils.isEmpty(password)){
            etPassword.setError("Required Field!");
        }
        else{
            progressDialog.setTitle("Creating Account");
            progressDialog.setMessage("Processing...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            validatePhoneNumber(name, contact, password);
        }
    }

    private void validatePhoneNumber(final String name, final String contact, final String password) {
        final DatabaseReference databaseReference;
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Users").child(contact).exists()){
                    HashMap<String, Object> userDataMap=new HashMap<>();
                    userDataMap.put("contact", contact);
                    userDataMap.put("name", name);
                    userDataMap.put("password", password);

                    databaseReference.child("Users").child(contact).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Account successfully created!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "This "+contact+" already exits. Please login!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

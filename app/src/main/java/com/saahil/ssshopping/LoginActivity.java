package com.saahil.ssshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saahil.ssshopping.Model.Users;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    EditText etContact, etPassword;
    CheckBox cbRememberMe;
    Button btnLogin;
    TextView tvNotAdminPanelLink, tvAdminPanelLink;
    ProgressDialog progressDialog;
    public String parentDbName="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etContact=findViewById(R.id.etContact);
        etPassword=findViewById(R.id.etPassword);
        cbRememberMe=findViewById(R.id.cbRememberMe);
        tvAdminPanelLink=findViewById(R.id.tvAdminPanelLink);
        tvNotAdminPanelLink=findViewById(R.id.tvNotAdminPanelLink);
        btnLogin=findViewById(R.id.btnLogin);

        Paper.init(this);

        progressDialog=new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        tvAdminPanelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setText("Login Admin");
                tvAdminPanelLink.setVisibility(View.INVISIBLE);
                tvNotAdminPanelLink.setVisibility(View.VISIBLE);
                parentDbName="Admins";
            }
        });

        tvNotAdminPanelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setText("Login");
                tvNotAdminPanelLink.setVisibility(View.INVISIBLE);
                tvAdminPanelLink.setVisibility(View.VISIBLE);
                parentDbName="Users";
            }
        });
    }

    private void loginUser() {
        String contact=etContact.getText().toString().trim();
        String password=etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(contact)) {
            etContact.setError("Required Field!");
        }
        else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required Field!");
        }
        else{
            progressDialog.setTitle("Login");
            progressDialog.setMessage("Processing...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            allowAccessToAccount(contact, password);
        }
    }

    private void allowAccessToAccount(final String contact, final String password) {
        final DatabaseReference databaseReference;
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parentDbName).exists()){
                    Users userData=dataSnapshot.child(parentDbName).child(contact).getValue(Users.class);
                    if(userData.getContact().equals(contact)){
                        if(userData.getPassword().equals(password)){
                            if(cbRememberMe.isChecked()){
                                Paper.book().write(Prevalent.userContactKey, contact);
                                Paper.book().write(Prevalent.userPasswordKey, password);
                            }

                            if(parentDbName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent=new Intent(LoginActivity.this, AdminAddNewProductActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDbName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "Login Successfull!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Password Incorrect!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Account with this "+contact+" number does not exists!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

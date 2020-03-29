package com.saahil.ssshopping.UserData;

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
import com.saahil.ssshopping.Admin.AdminCategoryActivity;
import com.saahil.ssshopping.HomeActivity;
import com.saahil.ssshopping.Model.Users;
import com.saahil.ssshopping.Prevalent.Prevalent;
import com.saahil.ssshopping.R;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    EditText etContact, etPassword;
    CheckBox cbRememberMe;
    Button btnLogin;
    TextView tvNotAdminPanelLink, tvAdminPanelLink, tvForgotPassword;
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
        tvForgotPassword=findViewById(R.id.tvForgetPassword);

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
                parentDbName="Admin";
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

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
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
                    if(dataSnapshot.child(parentDbName).child(contact).exists()) {
                        Users userData = dataSnapshot.child(parentDbName).child(contact).getValue(Users.class);
                        if (userData.getContact().equals(contact)) {
                            if (userData.getPassword().equals(password)) {
                                if (cbRememberMe.isChecked()) {
                                    Paper.book().write(Prevalent.userContactKey, contact);
                                    Paper.book().write(Prevalent.userPasswordKey, password);
                                    Paper.book().write(Prevalent.userTypeKey, parentDbName);
                                }

                                if (parentDbName.equals("Admin")) {
                                    Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (parentDbName.equals("Users")) {
                                    Toast.makeText(LoginActivity.this, "Welcome "+userData.getName(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    Prevalent.currentOnlineUser=userData;
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Password Incorrect!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Paper.book().destroy();
                        Toast.makeText(LoginActivity.this, "Account with this " + contact + " number does not exists!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

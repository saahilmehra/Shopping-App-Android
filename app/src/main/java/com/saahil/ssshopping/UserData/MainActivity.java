package com.saahil.ssshopping.UserData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {
    Button btnLogin, btnJoinNow;
    ProgressDialog progressDialog;
    public String parentDbName="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        progressDialog=new ProgressDialog(this);

        btnLogin=findViewById(R.id.btnLogin);
        btnJoinNow=findViewById(R.id.btnJoinNow);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnJoinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String userContactKey=Paper.book().read(Prevalent.userContactKey);
        String userPasswordKey=Paper.book().read(Prevalent.userPasswordKey);
        parentDbName=Paper.book().read(Prevalent.userTypeKey);
        if(userContactKey!="" && userPasswordKey!=""){
            if(!TextUtils.isEmpty(userContactKey) && !TextUtils.isEmpty(userPasswordKey)){
                allowAccess(userContactKey, userPasswordKey);
                progressDialog.setTitle("Logging In");
                progressDialog.setMessage("Loading...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }
        }
    }

    private void allowAccess(final String userContactKey, final String userPasswordKey) {
        final DatabaseReference databaseReference;
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parentDbName).exists()){
                    if(dataSnapshot.child(parentDbName).child(userContactKey).exists()) {
                        Users userData = dataSnapshot.child(parentDbName).child(userContactKey).getValue(Users.class);
                        if (userData.getContact().equals(userContactKey)) {
                            if (userData.getPassword().equals(userPasswordKey)) {
                                if (parentDbName.equals("Admin")) {
                                    Toast.makeText(MainActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(MainActivity.this, AdminCategoryActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (parentDbName.equals("Users")) {
                                    Toast.makeText(MainActivity.this, "Welcome "+userData.getName(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    Prevalent.currentOnlineUser=userData;
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                progressDialog.dismiss();
                                Paper.book().destroy();
                                Toast.makeText(MainActivity.this, "Password Incorrect!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Paper.book().destroy();
                        Toast.makeText(MainActivity.this, "Account with this " + userContactKey + " number does not exists!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

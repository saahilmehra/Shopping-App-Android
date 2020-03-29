package com.saahil.ssshopping.UserData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saahil.ssshopping.HomeActivity;
import com.saahil.ssshopping.Prevalent.Prevalent;
import com.saahil.ssshopping.R;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextView tvPageTitle, tvTitle;
    EditText etContact, etQues1, etQues2;
    Button btnVerify;
    String check="";
    DatabaseReference userReference;
    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        check=getIntent().getStringExtra("check");

        layoutInflater=this.getLayoutInflater();

        tvPageTitle=findViewById(R.id.tvPageTitle);
        etContact=findViewById(R.id.etContact);
        tvTitle=findViewById(R.id.tvTitle);
        etQues1=findViewById(R.id.etQues1);
        etQues2=findViewById(R.id.etQues2);
        btnVerify=findViewById(R.id.btnVerify);
    }

    @Override
    protected void onStart() {
        super.onStart();

        etContact.setVisibility(View.GONE);

        if(check.equals("settings")){
            userReference= FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getContact());
            displayPresentData();

            tvPageTitle.setText("Security Questions");
            tvTitle.setText("Please set answers for the following questions");
            btnVerify.setText("Update");

            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnswers();
                }
            });
        }
        else if(check.equals("login")){
            etContact.setVisibility(View.VISIBLE);
            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyUser();
                }
            });
        }
    }

    private void verifyUser() {
        final String contact=etContact.getText().toString().trim();
        final String answer1=etQues1.getText().toString().trim();
        final String answer2=etQues2.getText().toString().trim();

        if(TextUtils.isEmpty(contact)){
            etContact.setError("Required field!");
            return;
        }
        if(TextUtils.isEmpty(answer1)){
            etQues1.setError("Required field!");
            return;
        }
        if(TextUtils.isEmpty(answer2)){
            etQues2.setError("Required field!");
            return;
        }

        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(contact);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("Security Questions")){
                        String ans1=dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                        String ans2=dataSnapshot.child("Security Questions").child("answer2").getValue().toString();

                        if(answer1.equals(ans1) && answer2.equals(ans2)){
                            AlertDialog.Builder builder=new AlertDialog.Builder(ForgotPasswordActivity.this);

                            View view=layoutInflater.inflate(R.layout.password_reset_custom_view, null);
                            builder.setView(view);
                            builder.setTitle("Enter new password");

                            final EditText etPassword=view.findViewById(R.id.etPassword);
                            final EditText etRePassword=view.findViewById(R.id.etRePassword);

                            builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String pass=etPassword.getText().toString().trim();
                                    String pass2=etRePassword.getText().toString().trim();

                                    if(TextUtils.isEmpty(pass)){
                                        etPassword.setError("Required Field!");
                                        Toast.makeText(ForgotPasswordActivity.this, "Password cannnot be left blank!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if(TextUtils.isEmpty(pass2)){
                                        etRePassword.setError("Required Field!");
                                        Toast.makeText(ForgotPasswordActivity.this, "Password cannnot be left blank!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if(!pass.equals(pass2)){
                                        etPassword.setError("Passwords don't match!");
                                        etRePassword.setError("Passwords don't match!");
                                        Toast.makeText(ForgotPasswordActivity.this, "Password don't match!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    ref.child("password")
                                            .setValue(pass)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(ForgotPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent=new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });


                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                            builder.create();
                            builder.show();
                        }
                        else{
                            Toast.makeText(ForgotPasswordActivity.this, "Answers are wrong!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(ForgotPasswordActivity.this, "You have not set answers to security questions!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(ForgotPasswordActivity.this, "Contact not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAnswers() {
        String ans1=etQues1.getText().toString().trim();
        String ans2=etQues2.getText().toString().trim();

        if(TextUtils.isEmpty(ans1)){
            etQues1.setError("Required Field!");
            return;
        }
        if(TextUtils.isEmpty(ans2)){
            etQues2.setError("Required Field!");
            return;
        }

        HashMap<String, Object> quesMap=new HashMap<>();
        quesMap.put("answer1", ans1);
        quesMap.put("answer2", ans2);

        userReference.child("Security Questions").updateChildren(quesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Successfull", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ForgotPasswordActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void displayPresentData() {
        userReference.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String ans1=dataSnapshot.child("answer1").getValue().toString();
                    String ans2=dataSnapshot.child("answer2").getValue().toString();
                    etQues1.setText(ans1);
                    etQues2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

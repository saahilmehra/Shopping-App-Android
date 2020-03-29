package com.saahil.ssshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saahil.ssshopping.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    EditText etName, etContact, etAddress, etCity;
    Button btnConfirm;
    String totalPrice="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalPrice=getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price= "+totalPrice, Toast.LENGTH_SHORT).show();

        etName=findViewById(R.id.etName);
        etAddress=findViewById(R.id.etAddress);
        etCity=findViewById(R.id.etCity);
        etContact=findViewById(R.id.etContact);
        btnConfirm=findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });
    }

    private void checkValidations() {
        if(TextUtils.isEmpty(etName.getText().toString().trim())){
            etName.setError("Required Field!");
            return;
        }
        if(TextUtils.isEmpty(etContact.getText().toString().trim())){
            etContact.setError("Required Field!");
            return;
        }
        if(TextUtils.isEmpty(etAddress.getText().toString().trim())){
            etAddress.setError("Required Field!");
            return;
        }
        if(TextUtils.isEmpty(etCity.getText().toString().trim())){
            etCity.setError("Required Field!");
            return;
        }

        confirmOrder();
    }

    private void confirmOrder() {
        final String currentDate, currentTime;
        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat dateFormat=new SimpleDateFormat("MMM dd, yyyy");
        currentDate=dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss a");
        currentTime=timeFormat.format(calendar.getTime());

        final DatabaseReference ordersReference=FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getContact());

        HashMap<String, Object> ordersMap=new HashMap<>();
        ordersMap.put("totalAmount", totalPrice);
        ordersMap.put("name", etName.getText().toString().trim());
        ordersMap.put("contact", etContact.getText().toString().trim());
        ordersMap.put("address", etAddress.getText().toString().trim());
        ordersMap.put("city", etCity.getText().toString().trim());
        ordersMap.put("date", currentDate);
        ordersMap.put("time", currentTime);
        ordersMap.put("status", "Not Shipped");

        ordersReference.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getContact())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your order has been taken.", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }
}

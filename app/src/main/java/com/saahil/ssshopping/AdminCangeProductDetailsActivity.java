package com.saahil.ssshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminCangeProductDetailsActivity extends AppCompatActivity {
    Button btnApplyChanges;
    EditText etName, etPrice, etDescription;
    ImageView ivImage;
    String productId="";
    DatabaseReference productsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_cange_product_details);

        productId=getIntent().getStringExtra("pid");

        productsReference= FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        etName=findViewById(R.id.etName);
        etPrice=findViewById(R.id.etPrice);
        etDescription=findViewById(R.id.etDescription);
        btnApplyChanges=findViewById(R.id.btnApplyChanges);
        ivImage=findViewById(R.id.ivImage);

        showPresentProductData();

        btnApplyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });
    }

    private void checkValidations() {
        String name=etName.getText().toString().trim();
        String price=etPrice.getText().toString().trim();
        String description=etDescription.getText().toString().trim();

        if(name.equals("")){
            etName.setError("Required Fields...");
            return;
        }
        if(price.equals("")){
            etPrice.setError("Required Fields...");
            return;
        }
        if(description.equals("")){
            etDescription.setError("Required Fields...");
            return;
        }
        applyChanges();
    }

    private void applyChanges() {
        String name=etName.getText().toString().trim();
        String price=etPrice.getText().toString().trim();
        String description=etDescription.getText().toString().trim();

        HashMap<String, Object> productMap=new HashMap<>();
        productMap.put("pname", name);
        productMap.put("price", price);
        productMap.put("description", description);

        productsReference.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AdminCangeProductDetailsActivity.this, "Applied changes successfully", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(AdminCangeProductDetailsActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void showPresentProductData() {
        productsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name=dataSnapshot.child("pname").getValue().toString();
                    String price=dataSnapshot.child("price").getValue().toString();
                    String description=dataSnapshot.child("description").getValue().toString();
                    String image_url=dataSnapshot.child("image_url").getValue().toString();
                    etName.setText(name);
                    etPrice.setText(price);
                    etDescription.setText(description);
                    Picasso.get().load(image_url).into(ivImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

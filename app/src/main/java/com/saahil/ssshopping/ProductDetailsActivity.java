package com.saahil.ssshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saahil.ssshopping.Model.Products;
import com.squareup.picasso.Picasso;

public class ProductDetailsActivity extends AppCompatActivity {
    TextView tvName, tvDescription, tvPrice;
    ImageView ivProductImage;
    ElegantNumberButton elegantNumberButton;
    String productId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId=getIntent().getStringExtra("pid");

        ivProductImage=findViewById(R.id.ivProductImage);
        tvName=findViewById(R.id.tvName);
        tvDescription=findViewById(R.id.tvDescription);
        tvPrice=findViewById(R.id.tvPrice);
        elegantNumberButton=findViewById(R.id.elegantNumberButton);
        
        getProductDetails(productId);
    }

    private void getProductDetails(String productId) {
        DatabaseReference productReference= FirebaseDatabase.getInstance().getReference().child("Products");
        productReference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Products product=dataSnapshot.getValue(Products.class);
                    tvName.setText(product.getPname());
                    tvDescription.setText(product.getDescription());
                    tvPrice.setText(product.getPrice());

                    Picasso.get().load(product.getImage_url()).into(ivProductImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

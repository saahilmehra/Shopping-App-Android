package com.saahil.ssshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saahil.ssshopping.Model.Products;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    TextView tvName, tvDescription, tvPrice;
    ImageView ivProductImage;
    ElegantNumberButton elegantNumberButton;
    String productId="";
    Button btnAddToCart;
    DatabaseReference productReference;
    Products product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId=getIntent().getStringExtra("pid");

        productReference= FirebaseDatabase.getInstance().getReference().child("Products");

        ivProductImage=findViewById(R.id.ivProductImage);
        tvName=findViewById(R.id.tvName);
        tvDescription=findViewById(R.id.tvDescription);
        tvPrice=findViewById(R.id.tvPrice);
        elegantNumberButton=findViewById(R.id.elegantNumberButton);
        btnAddToCart=findViewById(R.id.btnAddToCart);

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });
        
        getProductDetails(productId);
    }

    private void addToCart() {
        final String currentTime, currentDate;
        final Calendar calendar=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("MMM dd, yyyy");
        currentDate=dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss a");
        currentTime=timeFormat.format(calendar.getTime());

        productReference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final DatabaseReference cartReference=FirebaseDatabase.getInstance().getReference().child("Cart");
                final HashMap<String, Object> cartMap=new HashMap<>();

                cartMap.put("pid", productId);
                cartMap.put("pname", product.getPname());
                cartMap.put("price", product.getPrice());
                cartMap.put("date", currentDate);
                cartMap.put("time", currentTime);
                cartMap.put("quantity", elegantNumberButton.getNumber());
                cartMap.put("discount", "");

                cartReference.child("User View")
                        .child(Prevalent.currentOnlineUser.getContact())
                        .child("Products")
                        .child(productId)
                        .updateChildren(cartMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            cartReference.child("Admin View")
                                    .child(Prevalent.currentOnlineUser.getContact())
                                    .child("Products")
                                    .child(productId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ProductDetailsActivity.this, "Added to Cart...", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getProductDetails(String productId) {
        productReference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    product=dataSnapshot.getValue(Products.class);
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

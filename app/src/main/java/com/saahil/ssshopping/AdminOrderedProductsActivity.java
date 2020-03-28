package com.saahil.ssshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saahil.ssshopping.Model.Cart;
import com.saahil.ssshopping.ViewHolder.CartViewHolder;

public class AdminOrderedProductsActivity extends AppCompatActivity {
    RecyclerView rvProducts;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference cartReference;
    String userId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ordered_products_activity);

        rvProducts=findViewById(R.id.rvProducts);
        rvProducts.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        rvProducts.setLayoutManager(layoutManager);

        userId=getIntent().getStringExtra("uid");
        cartReference= FirebaseDatabase.getInstance().getReference().child("Cart").child("Admin View")
                .child(userId)
                .child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options=new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartReference, Cart.class)
                .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                holder.tvName.setText(model.getPname());
                holder.tvPrice.setText("Price= "+model.getPrice());
                holder.tvQuantity.setText("Quantity= "+model.getQuantity());
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };

        rvProducts.setAdapter(adapter);
        adapter.startListening();
    }
}

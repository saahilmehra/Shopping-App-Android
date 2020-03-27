package com.saahil.ssshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saahil.ssshopping.Model.Cart;
import com.saahil.ssshopping.ViewHolder.CartViewHolder;

public class CartActivity extends AppCompatActivity {
    RecyclerView rvCart;
    RecyclerView.LayoutManager layoutManager;
    Button btnNext;
    TextView tvTotalPrice;
    int totalPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCart=findViewById(R.id.rvCart);
        rvCart.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        rvCart.setLayoutManager(layoutManager);

        btnNext=findViewById(R.id.btnNext);
        tvTotalPrice=findViewById(R.id.tvTotalPrice);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvTotalPrice.setText("Total Price ="+totalPrice);
                Intent intent=new Intent(CartActivity.this, ConfirmfinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(totalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartReference= FirebaseDatabase.getInstance().getReference().child("Cart");
        FirebaseRecyclerOptions<Cart> options=new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartReference.child("User View")
                .child(Prevalent.currentOnlineUser.getContact())
                .child("Products"), Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.tvName.setText(model.getPname());
                holder.tvQuantity.setText("Quantity= "+model.getQuantity());
                holder.tvPrice.setText("Price= $"+model.getPrice());

                int subTotal=(Integer.valueOf(model.getPrice()) * Integer.valueOf(model.getQuantity()));
                totalPrice=totalPrice+subTotal;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[]=new CharSequence[]{
                                "Edit",
                                "Remove"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Title");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, final int i) {
                                if(i==0){
                                    Intent intent=new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if(i==1){
                                    cartReference.child("User View").child(Prevalent.currentOnlineUser.getContact())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this, "Item removed successfully...", Toast.LENGTH_SHORT).show();
                                                        Intent intent=new Intent(CartActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };
        rvCart.setAdapter(adapter);
        adapter.startListening();
        tvTotalPrice.setText("Total Price ="+totalPrice);
    }
}

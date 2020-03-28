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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saahil.ssshopping.Model.AdminOrders;
import com.saahil.ssshopping.ViewHolder.AdminNewOrdersViewHolder;

public class AdminNewOrdersActivity extends AppCompatActivity {
    RecyclerView rvOrders;
    DatabaseReference ordersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        ordersReference= FirebaseDatabase.getInstance().getReference().child("Orders");

        rvOrders=findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options=new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersReference, AdminOrders.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrders, AdminNewOrdersViewHolder> adapter=new FirebaseRecyclerAdapter<AdminOrders, AdminNewOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminNewOrdersViewHolder holder, final int position, @NonNull AdminOrders model) {
                holder.tvName.setText("Name :"+model.getName());
                holder.tvContact.setText("Contact: "+model.getContact());
                holder.tvtotalPrice.setText("Total Amount: "+model.getTotalAmount());
                holder.tvAddressCity.setText("Shipping Address: "+model.getAddress()+", "+model.getCity());
                holder.tvDateTime.setText("Ordered on "+model.getDate()+" "+model.getTime());

                holder.btnShowproducts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uId=getRef(position).getKey();
                        Intent intent=new Intent(AdminNewOrdersActivity.this, AdminOrderedProductsActivity.class);
                        intent.putExtra("uid", uId);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[]=new CharSequence[]
                                {
                                        "Yes",
                                        "No"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(AdminNewOrdersActivity.this);
                        builder.setTitle("Have the products been shipped?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    String uid=getRef(position).getKey();
                                    removeOrder(uid);
                                }
                                if(i==1){
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AdminNewOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_items_layout, parent, false);
                AdminNewOrdersViewHolder holder=new AdminNewOrdersViewHolder(view);
                return holder;
            }
        };

        rvOrders.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeOrder(String uid) {
        ordersReference.child(uid).removeValue();
    }
}

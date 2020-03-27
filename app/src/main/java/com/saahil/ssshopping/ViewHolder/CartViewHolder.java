package com.saahil.ssshopping.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.saahil.ssshopping.R;

public class CartViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName, tvQuantity, tvPrice;

    public CartViewHolder(View itemView){
        super(itemView);
        tvName=itemView.findViewById(R.id.tvName);
        tvPrice=itemView.findViewById(R.id.tvPrice);
        tvQuantity=itemView.findViewById(R.id.tvQuantity);
    }
}

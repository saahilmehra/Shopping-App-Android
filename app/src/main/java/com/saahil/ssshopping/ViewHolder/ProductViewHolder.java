package com.saahil.ssshopping.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saahil.ssshopping.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName, tvPrice, tvDescription;
    public ImageView ivImage;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        ivImage=(ImageView)itemView.findViewById(R.id.ivImage);
        tvName=itemView.findViewById(R.id.tvName);
        tvPrice=itemView.findViewById(R.id.tvPrice);
        tvDescription=itemView.findViewById(R.id.tvDescription);
    }
}

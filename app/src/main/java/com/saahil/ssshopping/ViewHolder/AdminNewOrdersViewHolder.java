package com.saahil.ssshopping.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.saahil.ssshopping.R;

import org.w3c.dom.Text;

public class AdminNewOrdersViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName, tvContact, tvtotalPrice, tvAddressCity, tvDateTime;
    public Button btnShowproducts;

    public AdminNewOrdersViewHolder(View itemView){
        super(itemView);
        tvName=itemView.findViewById(R.id.tvName);
        tvContact=itemView.findViewById(R.id.tvContact);
        tvtotalPrice=itemView.findViewById(R.id.tvTotalPrice);
        tvAddressCity=itemView.findViewById(R.id.tvAddressCity);
        tvDateTime=itemView.findViewById(R.id.tvDateTime);
        btnShowproducts=itemView.findViewById(R.id.btnShowProducts);
    }
}

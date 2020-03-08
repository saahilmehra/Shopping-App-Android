package com.saahil.ssshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AdminAddNewProductActivity extends AppCompatActivity {
    ImageView ivUploadImage;
    EditText etName, etDescription, etPrice;
    Button btnAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        ivUploadImage=findViewById(R.id.ivUploadImage);
        etName=findViewById(R.id.etName);
        etDescription=findViewById(R.id.etDescription);
        etPrice=findViewById(R.id.etPrice);
        btnAddProduct=findViewById(R.id.btnAddProduct);

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }
}

package com.saahil.ssshopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
    ImageView ivUploadImage;
    EditText etName, etDescription, etPrice;
    Button btnAddProduct;
    String categoryName;
    String name, description, price, currentDate, currentTime;
    public static final int GALLERY_PIN=1;
    public Uri imageUri;
    String productRandomKey, downloadImageUrl;
    private StorageReference productImageReference;
    private DatabaseReference productReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        progressDialog=new ProgressDialog(this);

        categoryName=getIntent().getExtras().get("category").toString();
        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();

        productImageReference= FirebaseStorage.getInstance().getReference().child("Product Images");
        productReference= FirebaseDatabase.getInstance().getReference().child("Products");

        ivUploadImage=findViewById(R.id.ivUploadImage);
        etName=findViewById(R.id.etName);
        etDescription=findViewById(R.id.etDescription);
        etPrice=findViewById(R.id.etPrice);
        btnAddProduct=findViewById(R.id.btnAddProduct);
        
        ivUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();
            }
        });
    }

    private void checkValidations() {
        name=etName.getText().toString().trim();
        description=etDescription.getText().toString().trim();
        price=etPrice.getText().toString().trim();

        if(imageUri == null){
            Toast.makeText(this, "Product image is mandatory!!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name)){
            etName.setError("Required Field!");
        }
        else if(TextUtils.isEmpty(description)){
            etDescription.setError("Required Field!");
        }
        else if(TextUtils.isEmpty(price)){
            etPrice.setError("Required Field!");
        }
        else{
            storeProducInformation();
        }
    }

    private void storeProducInformation() {
        progressDialog.setTitle("Adding New Product");
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM dd,yyyy");
        currentDate=simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat simpleTimeFormat=new SimpleDateFormat("HH:mm:ss a");
        currentTime=simpleTimeFormat.format(calendar.getTime());

        productRandomKey = currentDate + currentTime;

        final StorageReference filePath=productImageReference.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask=filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage=e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: "+errorMessage, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl=task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Got the image url successfully!", Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase() {
        final HashMap<String, Object> productMap=new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", currentDate);
        productMap.put("time", currentTime);
        productMap.put("description", description);
        productMap.put("image_url", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", price);
        productMap.put("pname", name);

        productReference.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);

                    progressDialog.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PIN && resultCode == RESULT_OK && data != null){
            imageUri=data.getData();
            ivUploadImage.setImageURI(imageUri);
        }
    }
}

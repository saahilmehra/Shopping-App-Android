package com.saahil.ssshopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saahil.ssshopping.Admin.AdminChangeProductDetailsActivity;
import com.saahil.ssshopping.Model.Products;
import com.saahil.ssshopping.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference productReference;
    TextView tvUsername;
    ImageView ivProfileImage;
    FloatingActionButton fab;
    String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
            type=getIntent().getExtras().get("Admin").toString();
        }

        productReference= FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView=findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        fab=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!type.equals("Admin")){
                    Intent intent=new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);
        tvUsername=headerView.findViewById(R.id.tvUsername);
        ivProfileImage=headerView.findViewById(R.id.ivProfileImage);

        if(!type.equals("Admin")){
            setUsernameImage();
        }
    }

    private void setUsernameImage() {
        DatabaseReference userReference=FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getContact());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name=dataSnapshot.child("name").getValue().toString();

                    if(dataSnapshot.child("image").exists()){
                        String imageUrl=dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(imageUrl).placeholder(R.drawable.profile).into(ivProfileImage);
                    }

                    tvUsername.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options=new FirebaseRecyclerOptions.Builder<Products>().setQuery(productReference, Products.class).build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                holder.tvName.setText(model.getPname());
                holder.tvPrice.setText(model.getPrice());
                holder.tvDescription.setText(model.getDescription());

                Picasso.get().load(model.getImage_url()).into(holder.ivImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(type.equals("Admin")){
                            Intent intent=new Intent(HomeActivity.this, AdminChangeProductDetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                        else{
                            Intent intent=new Intent(HomeActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
                ProductViewHolder holder=new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id=menuItem.getItemId();
        if(!type.equals("Admin")){
            if(id==R.id.navCart){
                Intent intent=new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
            else if(id==R.id.navSearch){
                Intent intent=new Intent(HomeActivity.this, SearchProductsActivity.class);
                startActivity(intent);
            }
            else if(id==R.id.navCategories){
                Toast.makeText(this, "Categories", Toast.LENGTH_SHORT).show();
            }
            else if(id==R.id.navSettings){
                Intent intent=new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        }

        if(id==R.id.navLogout){
            Paper.book().destroy();
            Intent intent=new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}

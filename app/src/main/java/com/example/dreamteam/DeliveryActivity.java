package com.example.dreamteam;

import android.app.Activity;
import android.content.Intent;
//import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
//import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DeliveryActivity extends AppCompatActivity {

    private RecyclerView deliveryRecyclerView;
    private Button changeOrAddNewAddressBtn;
    private Button continueButton;
    private FirebaseAuth firebaseAuth;

    private AddressesModel currentAddress;

    private ActivityResultLauncher<Intent> activityResultLauncher;
    public static final int SELECT_ADDRESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");
        firebaseAuth = FirebaseAuth.getInstance();

        continueButton = findViewById(R.id.delivery_continue_btn);
        deliveryRecyclerView = findViewById(R.id.delivery_recyclerview);
        changeOrAddNewAddressBtn = findViewById(R.id.change_or_add_address_btn);
        View detailsLayout = findViewById(R.id.shipping_details);
        TextView fullName = detailsLayout.findViewById(R.id.full_name);
        TextView shippingAddress = detailsLayout.findViewById(R.id.address);
        TextView postalCode = detailsLayout.findViewById(R.id.pincode);

        if (currentAddress != null) {
            fullName.setText(currentAddress.getName());
            shippingAddress.setText(currentAddress.getAddress());
            postalCode.setText(currentAddress.getPincode());
        }else {
            fullName.setVisibility(View.INVISIBLE);
            shippingAddress.setVisibility(View.INVISIBLE);
            postalCode.setVisibility(View.INVISIBLE);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(linearLayoutManager);
        List<CartItemModel> deliveryCartItemModelList = new ArrayList<>();

        CartAdapter cartAdapter = new CartAdapter(deliveryCartItemModelList,
                CartAdapter.AdapterMode.DELIVERY);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            MyCartFragment.cartItemModelList.forEach(cartItemModel -> {
                if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                    deliveryCartItemModelList.add(cartItemModel);
                }
            });
        }


        changeOrAddNewAddressBtn.setVisibility(View.VISIBLE);
        changeOrAddNewAddressBtn.setOnClickListener(v -> {
            Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressesActivity.class);
            myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
            activityResultLauncher.launch(myAddressesIntent);
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    assert intent != null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        AddressesModel data = intent.getSerializableExtra(
                                "address",
                                AddressesModel.class
                        );
                        if (data != null) {
                            currentAddress = data;
                            fullName.setVisibility(View.VISIBLE);
                            shippingAddress.setVisibility(View.VISIBLE);
                            postalCode.setVisibility(View.VISIBLE);
                            fullName.setText(currentAddress.getName());
                            shippingAddress.setText(currentAddress.getAddress());
                            postalCode.setText(currentAddress.getPincode());
                        }
                    }
                    // Handle the Intent
                }
            }
        });

        continueButton.setOnClickListener(v -> {
            if (currentAddress == null) {
                Toast.makeText(this, "Please choose a shipping address", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}

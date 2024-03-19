package com.example.dreamteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.dreamteam.DeliveryActivity.SELECT_ADDRESS;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyAddressesActivity extends AppCompatActivity {

    private RecyclerView myaddressesRecyclerView;
    private static AddressesAdapter addressesAdapter;
    private Button deliverHereBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView numOfAddress;

    private ViewGroup addNewAddressBtn;

    private final List<AddressesModel> addressesModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myaddressesRecyclerView = findViewById(R.id.addresses_recyclerview);
        addNewAddressBtn = findViewById(R.id.add_new_address_btn);
        deliverHereBtn = findViewById(R.id.deliver_here_btn);
        numOfAddress = findViewById(R.id.address_saved);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myaddressesRecyclerView.setLayoutManager(linearLayoutManager);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        int mode = getIntent().getIntExtra("MODE", -1);
        if (mode == SELECT_ADDRESS) {
            deliverHereBtn.setVisibility(View.VISIBLE);
        } else {
            deliverHereBtn.setVisibility(View.GONE);
        }
        addressesAdapter = new AddressesAdapter(addressesModelList, mode);
        addressesAdapter.setListener(model -> {
            Intent intent = new Intent();
            intent.putExtra("address", model);
            setResult(RESULT_OK,intent);
            finish();

        });
        myaddressesRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator) myaddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        firebaseFirestore.collection("USERS")
                .document(user.getUid())
                .collection("ADDRESSES")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (queryDocumentSnapshots == null) {
                        return;
                    }
                    addressesModelList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        addressesModelList.add(new AddressesModel(
                                user.getUid(),
                                doc.getId(),
                                (doc.get("name") != null) ? doc.get("name").toString() : "",
                                (doc.get("phone") != null) ? doc.get("phone").toString() : "",
                                (doc.get("line1") != null) ? doc.get("line1").toString() : "",
                                (doc.get("line2") != null) ? doc.get("line2").toString() : "",
                                (doc.get("line3") != null) ? doc.get("line3").toString() : "",
                                (doc.get("line4") != null) ? doc.get("line4").toString() : "",
                                String.valueOf(doc.get("postal_code")),
                                doc.get("isDefault") != null && (boolean) doc.get("isDefault")
                        ));
                        onNotifyChange(addressesAdapter);
                    }
                    onNotifyChange(addressesAdapter);
                });


        addressesModelList.add(new AddressesModel("1", "123", "name", "phone", "123 Main Street", "Apartment 101", "", "Springfield", "12345", true));
        addressesModelList.add(new AddressesModel("1", "123", "name", "phone", "456 Elm Avenue", "", "", "Rivertown", "54321", false));
        addressesModelList.add(new AddressesModel("1", "123", "name", "phone", "789 Oak Lane", "Suite B", "", "Lakeside", "67890", true));
        addressesModelList.add(new AddressesModel("1", "123", "name", "phone", "101 Pine Street", "", "", "Mountainview", "45678", true));
        addressesModelList.add(new AddressesModel("1", "123", "name", "phone", "555 Cedar Drive", "", "", "Oceanview", "98765", false));
        addressesModelList.add(new AddressesModel("1", "123", "name", "phone", "321 Maple Road", "", "Floor 3", "Hillside", "24680", true));
        addressesModelList.add(new AddressesModel("1", "123", "name", "phone", "777 Willow Lane", "Unit 202", "", "Meadowville", "13579", false));
        addressesModelList.add(new AddressesModel("1", "123", "name", "phone", "888 Birch Street", "", "", "Woodland", "36912", true));
        addressesModelList.add(new AddressesModel("1", "123", "name", "phone", "444 Oak Avenue", "", "Suite A", "Grovewood", "97531", false));


        addNewAddressBtn.setOnClickListener(v -> {
            Intent deliveryIntent = new Intent(MyAddressesActivity.this, AddAddressActivity.class);
            startActivity(deliveryIntent);
        });

        addressesAdapter.notifyDataSetChanged();
    }

    public static void refreshItem(int deSelect, int select) {
        addressesAdapter.notifyItemChanged(deSelect);
        addressesAdapter.notifyItemChanged(select);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onNotifyChange(AddressesAdapter adapter) {
        adapter.notifyDataSetChanged();
        numOfAddress.setText(addressesModelList.size() + " addresses saved");
    }
}

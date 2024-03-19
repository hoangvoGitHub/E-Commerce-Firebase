package com.example.dreamteam;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddAddressActivity extends AppCompatActivity {


    private Button saveBtn;

    private EditText line1;
    private EditText line2;
    private EditText line3;
    private EditText line4;
    private EditText postalCode;

    private EditText name;

    private EditText mobileNumber;

    private ProgressBar loading;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add a new Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        line4 = findViewById(R.id.line4);
        loading = findViewById(R.id.add_new_address_progress_bar);
        postalCode = findViewById(R.id.pincode);
        name = findViewById(R.id.name);
        mobileNumber = findViewById(R.id.mobile_no);




        saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(v -> {
            checkFormThenSave();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkFormThenSave(){
        boolean isFormValid = true;
        if (line1.getText().toString().isBlank() || line1.getText().toString().isEmpty()){
            line1.setError("This line cannot be empty");
            isFormValid = false;
        }
         if (line2.getText().toString().isBlank() || line2.getText().toString().isEmpty()){
             line2.setError("This line cannot be empty");
            isFormValid = false;
        }
         if (line3.getText().toString().isBlank() || line3.getText().toString().isEmpty()){
             line3.setError("This line cannot be empty");
            isFormValid = false;
        }
         if (line4.getText().toString().isBlank() || line4.getText().toString().isEmpty()){
             line4.setError("This line cannot be empty");
            isFormValid = false;
        }
         if (postalCode.getText().toString().isBlank() || postalCode.getText().toString().isEmpty()){
             postalCode.setError("Postal code cannot be empty");
            isFormValid = false;
        }
         if (name.getText().toString().isBlank() || name.getText().toString().isEmpty()){
             name.setError("Name code cannot be empty");
            isFormValid = false;
        }

        if (mobileNumber.getText().toString().isBlank() || mobileNumber.getText().toString().isEmpty()){
            mobileNumber.setError("Mobile Number cannot be empty");
            isFormValid = false;
        }

        if (mobileNumber.getText().toString().length() < 9){
            mobileNumber.setError("Mobile Number must at least 8 numbers");
            isFormValid = false;
        }
        if (isFormValid){
            loading.setVisibility(View.VISIBLE);
            Map<String, Object> addressObject = new HashMap<>();
            addressObject.put("name", name.getText().toString());
            addressObject.put("phone", mobileNumber.getText().toString());
            addressObject.put("line1", line1.getText().toString());
            addressObject.put("line2", line2.getText().toString());
            addressObject.put("line3", line3.getText().toString());
            addressObject.put("line4", line4.getText().toString());
            addressObject.put("postal_code", postalCode.getText().toString());
            addressObject.put("is_default", postalCode.getText().toString());

            firebaseFirestore.collection("USERS")
                    .document(user.getUid())
                    .collection("ADDRESSES")
                    .add(addressObject)
                    .addOnCompleteListener(task -> {
                        loading.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Add address successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Some Errors Occur", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }






}

package com.example.dreamteam;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyCartFragment extends Fragment {


    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartItemsRecyclerView;
    private Button continueBtn;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressBar loading;
    FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private TextView totalCartAmount;

    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);
        cartItemsRecyclerView = view.findViewById(R.id.cart_items_recyclerview);
        continueBtn = view.findViewById(R.id.cart_continue_btn);
        loading = view.findViewById(R.id.cart_loading);
        loading.setVisibility(View.VISIBLE);
        swipeRefreshLayout = view.findViewById(R.id.cart_refresh_layout);
        totalCartAmount = view.findViewById(R.id.total_cart_amount);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecyclerView.setLayoutManager(linearLayoutManager);

         CartAdapter cartAdapter = new CartAdapter(cartItemModelList);
        cartAdapter.setListener(new CartAdapter.CartItemActionListener() {
            @Override
            public void onQuantityChange(String productId, int quantity) {
                loading.setVisibility(View.VISIBLE);
                firebaseFirestore.collection("USERS/" + user.getUid() + "/CART").document(productId)
                        .update("quantity", quantity).addOnCompleteListener(task -> {
                            loading.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Change quantity success", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Some Errors Occur", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCartItemClick(String productId) {

            }

            @Override
            public void onRemoveItem(String productId, View view) {

                view.setEnabled(false);
                AlertDialog alert = new AlertDialog.Builder(requireContext())
                        .setTitle("Remove item")
                        .setMessage("Are you sure to remove this item?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            loading.setVisibility(View.VISIBLE);
                            firebaseFirestore.collection("USERS/" + user.getUid() + "/CART").document(productId)
                                    .delete().addOnCompleteListener(task -> {

                                        loading.setVisibility(View.GONE);
                                        onNotifyChange(cartAdapter);
                                        if (task.isSuccessful()) {
                                            Toast.makeText(requireContext(), "Remove product successful success", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(requireContext(), "Some Errors Occur", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        })
                        .setOnDismissListener(dialog -> {
                            view.setEnabled(true);
                        })
                        .setNegativeButton("No", null)
                        .create();

                alert.setOnShowListener(arg0 -> {
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                });
                alert.show();

            }
        });

        cartItemsRecyclerView.setAdapter(cartAdapter);

        firebaseFirestore.collection("USERS/" + user.getUid() + "/CART").
                addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (queryDocumentSnapshots == null) {
                        return;
                    }
                    cartItemModelList.clear();
                    loading.setVisibility(View.GONE);
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        int quantity = Integer.parseInt(documentSnapshot.get("quantity").toString());
                        String productId = documentSnapshot.getId();
                        firebaseFirestore.collection("PRODUCT_TEST")
                                .document(productId).get()
                                .addOnCompleteListener(productTask -> {
                                    if (productTask.isSuccessful()) {

                                        DocumentSnapshot doc = productTask.getResult();
                                        long amount = (long) doc.get("price");
                                        double cutAmount = amount / 0.8;

                                        CartItemModel itemModel = new CartItemModel(
                                                CartItemModel.CART_ITEM,
                                                productId,
                                                ((List<String>) doc.get("images")).get(0),
                                                doc.get("name").toString(),
                                                2,
                                                Double.parseDouble(String.valueOf(amount)),
                                                Double.parseDouble(String.valueOf(cutAmount)), quantity,
                                                2, 2

                                        );
                                        cartItemModelList.add(itemModel);
                                        onNotifyChange(cartAdapter);
                                    }
                                });
                        onNotifyChange(cartAdapter);
                    }

                });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            firebaseFirestore.collection("USERS/" + user.getUid() + "/CART").get()
                    .addOnCompleteListener(cartTask -> {
                        swipeRefreshLayout.setRefreshing(false);
                        if (cartTask.isSuccessful()) {
                            cartItemModelList.clear();
                            QuerySnapshot queryDocumentSnapshots = cartTask.getResult();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                int quantity = Integer.parseInt(documentSnapshot.get("quantity").toString());
                                String productId = documentSnapshot.getId();
                                firebaseFirestore.collection("PRODUCT_TEST")
                                        .document(productId).get()
                                        .addOnCompleteListener(productTask -> {
                                            if (productTask.isSuccessful()) {

                                                DocumentSnapshot doc = productTask.getResult();
                                                long amount = (long) doc.get("price");
                                                double cutAmount = amount / 0.8;

                                                CartItemModel itemModel = new CartItemModel(
                                                        CartItemModel.CART_ITEM,
                                                        productId,
                                                        ((List<String>) doc.get("images")).get(0),
                                                        doc.get("name").toString(),
                                                        2,
                                                        Double.parseDouble(String.valueOf(amount)),
                                                        Double.parseDouble(String.valueOf(cutAmount)), quantity,
                                                        2, 2

                                                );
                                                cartItemModelList.add(itemModel);
                                                onNotifyChange(cartAdapter);
                                            }
                                        });
                            }
                        }
                    });
        });


        continueBtn.setOnClickListener(v -> {
            checkQuantity();
        });
        return view;
    }

    private void onNotifyChange(CartAdapter adapter) {
        double sum = 0;
        int itemCount = 0;
        for (CartItemModel model : cartItemModelList) {
            if (model.getType() == CartItemModel.CART_ITEM) {
                sum += (model.getRealPrice() * model.getProductQuantity());
                itemCount++;
            }
        }
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        String price = currencyFormatter.format(sum);
        totalCartAmount.setText(price);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cartItemModelList.removeIf(cartItemModel ->
                    cartItemModel.getType() == CartItemModel.TOTAL_AMOUNT
            );
        }
        cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT, "Price (" + itemCount + ") items", price, "Free", price, "230/-"));


        adapter.notifyDataSetChanged();
    }

    private final ArrayList<QuantityError> errorArrayList = new ArrayList<>();

    private void checkQuantity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            loading.setVisibility(View.VISIBLE);
            continueBtn.setEnabled(false);
            AtomicInteger checkedItem = new AtomicInteger();
            cartItemModelList.forEach(cartItemModel -> {
                if (cartItemModel.getType() ==CartItemModel.CART_ITEM){
                    firebaseFirestore.collection("PRODUCT_TEST")
                            .document(cartItemModel.getProductId())
                            .get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    checkedItem.getAndIncrement();
                                    var doc = task.getResult();
                                    int quantityInInventory = Integer.parseInt(doc.get("quantity").toString());
                                    if (cartItemModel.getProductQuantity() > quantityInInventory) {
                                        errorArrayList.add(new QuantityError(
                                                cartItemModel.getProductId(),
                                                cartItemModel.getProductTitle(),
                                                cartItemModel.getProductQuantity(),
                                                quantityInInventory
                                        ));
                                    }
                                }
                                if (checkedItem.get() == cartItemModelList.size()-1) {
                                    loading.setVisibility(View.GONE);
                                    continueBtn.setEnabled(true);
                                    // loading = false
                                    // show error if needed
                                    if (!errorArrayList.isEmpty()) {
                                        String productList = "";

                                        for (int i = 0; i < errorArrayList.size(); i++) {
                                            productList = productList + errorArrayList.get(i).getProductName()
                                                    + "\n ";
                                            if (i == errorArrayList.size() - 1) {
                                                productList += errorArrayList.get(i).getProductName();
                                            }
                                        }

                                        new AlertDialog.Builder(requireContext())
                                                .setTitle("Items below are exceeding inventory quantity")
                                                .setMessage(productList)
                                                .create()
                                                .show();
                                    } else {
                                        // go to next screen

                                        Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                                        requireActivity().startActivity(deliveryIntent);
                                    }
                                }
                            });
                }



            });
        }


    }

    private static class QuantityError {
        private String productId;
        private String productName;

        private int quantityInCart;
        private int quantityInInventory;

        public QuantityError(String productId, String productName, int quantityInCart, int quantityInInventory) {
            this.productId = productId;
            this.productName = productName;
            this.quantityInCart = quantityInCart;
            this.quantityInInventory = quantityInInventory;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantityInCart() {
            return quantityInCart;
        }

        public void setQuantityInCart(int quantityInCart) {
            this.quantityInCart = quantityInCart;
        }

        public int getQuantityInInventory() {
            return quantityInInventory;
        }

        public void setQuantityInInventory(int quantityInInventory) {
            this.quantityInInventory = quantityInInventory;
        }
    }

}


package com.example.dreamteam;

import static com.example.dreamteam.MainActivity.showCart;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ProductDetailsActivity extends AppCompatActivity {

    private ViewPager productImagesViewPager;
    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView totalRatingMiniView;
    private TextView productPrice;
    private TextView cuttedPrice;
    private ImageView codIndicator;
    private TextView tvCodIndicator;
    private TabLayout viewPagerIndicator;
    private TextView averageRating;
    private ProgressBar loading;

    private static Boolean ALREADY_ADDED_TO_WISHLIST = false;
    private FloatingActionButton addToWishListBtn;


    private TextView rewardTitle;
    private TextView rewardBody;

    /*********** RATINGS LAYOUT ******/

    private LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsNoContainer;
    private LinearLayout ratingsProgressBarContainer;
    private TextView totalRatingsFig;
    /*********** RATINGS LAYOUT ******/


    /*********** PRODUCT DESCRIPTION ******/
    private ConstraintLayout productDetailsOnlyContainer;
    private ConstraintLayout productDetailsTabsContainer;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;
    private TextView productOnlyDescriptionBody;

    public List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    public String productDescription;
    public String productOtherDetails;
    /*********** PRODUCT DESCRIPTION ******/

    private Button couponRedeemBtn;

    FirebaseFirestore firebaseFirestore;
    private Button buyNowBtn;
    private ViewGroup addToCartBtn;

    /*********** COUPON DIALOG ******/
    public static TextView couponTitle;
    public static TextView couponExpiryDate;
    public static TextView couponTBody;
    private static RecyclerView opencouponsRecyclerView;
    private static LinearLayout selectedCoupon;
    private int quantity = 0;


    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    /*********** COUPON DIALOG ******/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String productId = getIntent().getStringExtra("product_id");
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        addToWishListBtn = findViewById(R.id.add_to_wishlist_btn);
        productDetailsViewPager = findViewById(R.id.product_details_viewpager);
        productDetailsTabLayout = findViewById(R.id.product_details_tablayout);
        buyNowBtn = findViewById(R.id.buy_now_btn);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        couponRedeemBtn = findViewById(R.id.coupon_redemption_btn);
        productTitle = findViewById(R.id.product_title);
        averageRatingMiniView = findViewById(R.id.tv_product_rating_miniview);
        totalRatingMiniView = findViewById(R.id.total_ratings_miniview);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        codIndicator = findViewById(R.id.cod_indicator_imageview);
        tvCodIndicator = findViewById(R.id.tv_cod_indicator);
        rewardTitle = findViewById(R.id.reward_title);
        rewardBody = findViewById(R.id.reward_body);
        productDetailsTabsContainer = findViewById(R.id.product_details_tabs_container);
        productDetailsOnlyContainer = findViewById(R.id.product_details_container);
        productOnlyDescriptionBody = findViewById(R.id.product_details_body);
        totalRatings = findViewById(R.id.total_ratings);
        ratingsNoContainer = findViewById(R.id.ratings_numbers_container);
        totalRatingsFig = findViewById(R.id.total_ratings__figure);
        ratingsProgressBarContainer = findViewById(R.id.ratings_progressbar_container);
        averageRating = findViewById(R.id.average_rating);
        firebaseFirestore = FirebaseFirestore.getInstance();

        final List<String> productImages = new ArrayList<>();
        firebaseFirestore.collection("PRODUCT_TEST").document(productId)
                .get().addOnCompleteListener(task -> {
                    loading.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        productImages.addAll((List<String>) documentSnapshot.get("images"));
                        quantity = Integer.parseInt(documentSnapshot.get("quantity").toString());
                        ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                        productImagesViewPager.setAdapter(productImagesAdapter);
                        productTitle.setText(documentSnapshot.get("name").toString());
                        averageRatingMiniView.setText("4");
                        totalRatingMiniView.setText("(450) ratings");

                        long amount = (long) documentSnapshot.get("price");
                        double cutAmount = amount * 0.8;
                        Locale locale = new Locale("vi", "VN");
                        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                        String price = currencyFormatter.format(amount);
                        String cutPrice = currencyFormatter.format(cutAmount);

                        productPrice.setText(price + "/-");
                        cuttedPrice.setText(cutPrice + "/-");
                        boolean isCod = new Random().nextBoolean();
                        if (isCod) {
                            codIndicator.setVisibility(View.VISIBLE);
                            tvCodIndicator.setVisibility(View.VISIBLE);
                        } else {
                            codIndicator.setVisibility(View.INVISIBLE);
                            tvCodIndicator.setVisibility(View.INVISIBLE);
                        }

//                            rewardTitle.setText((long) documentSnapshot.get("free_coupons") + documentSnapshot.get("free_coupon_title").toString());
                        rewardTitle.setText("Coupous Title");
                        rewardBody.setText("Coupou body");
                        List<ProductSpecification> specifications = new ArrayList<>();
                        ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) documentSnapshot.get("specifications");
                        for (Map<String, Object> map : list) {
                            ProductSpecification productSpecification = new ProductSpecification();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if (entry.getKey().contentEquals("title")) {
                                    productSpecification.setTitle(entry.getValue().toString());
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        ArrayList<Map<String, String>> valueMapList = (ArrayList<Map<String, String>>) entry.getValue();
                                        List<ProductSpecification.SpecificationValue> values = new ArrayList<>();
                                        for (Map<String, String> mapList : valueMapList) {
                                            values.add(new ProductSpecification.SpecificationValue(
                                                    mapList.get("feature"),
                                                    mapList.get("feature_value")
                                            ));
                                        }
                                        productSpecification.setValue(values);

                                    }
                                }
                            }
                            specifications.add(productSpecification);
                        }
                        if (!specifications.isEmpty()) {


                            productDetailsTabsContainer.setVisibility(View.VISIBLE);
                            productDetailsOnlyContainer.setVisibility(View.GONE);
                            productDescription = documentSnapshot.get("description").toString();

                            productOtherDetails = documentSnapshot.get("other_details").toString();

                            productSpecificationModelList = ProductSpecificationMapper
                                    .toProductSpecificationModelList(specifications);

                        } else {
                            productDetailsTabsContainer.setVisibility(View.GONE);
                            productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                            productOnlyDescriptionBody.setText(documentSnapshot.get("description").toString());
                        }
                        totalRatings.setText("450 ratings");
                        List<Integer> ratingPoints = List.of(250, 100, 50, 50, 0);
                        for (int x = 0; x < 5; x++) {
                            TextView ratings = (TextView) ratingsNoContainer.getChildAt(x);
                            ratings.setText(ratingPoints.get(x).toString());
                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                            int maxProgress = 450;
                            progressBar.setMax(maxProgress);
                            progressBar.setProgress(ratingPoints.get(x));
                        }
                        totalRatingsFig.setText("450");

                        averageRating.setText("4.1");
                        productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList));
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_LONG).show();


                    }
                });

        viewPagerIndicator.setupWithViewPager(productImagesViewPager, true);
        addToWishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ALREADY_ADDED_TO_WISHLIST) {
                    ALREADY_ADDED_TO_WISHLIST = false;
                    addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                } else {
                    ALREADY_ADDED_TO_WISHLIST = true;
                    addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.btnRed));
                }
            }
        });


        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /*********** RATINGS LAYOUT ******/
        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int i = 0; i < rateNowContainer.getChildCount(); i++) {
            final int starPosition = i;
            rateNowContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRating(starPosition);
                }
            });
        }
        /*********** RATINGS LAYOUT ******/

        buyNowBtn.setOnClickListener(v -> {
            Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
            startActivity(deliveryIntent);
        });

        addToCartBtn.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                loading.setVisibility(View.GONE);
                firebaseAuth.signOut();
                Intent intent = new Intent(this, RegisterActivity.class);
                RegisterActivity.setSignUpFragment = false;
                startActivity(intent);
                finish();
            }

            showPickQuantity(new DialogListener() {
                @Override
                public void onDismiss() {
                    addToCartBtn.setEnabled(true);
                }

                @Override
                public void onPickNumber(int number) {
                    loading.setVisibility(View.VISIBLE);
                    addToCartBtn.setEnabled(false);
                    firebaseFirestore.collection("USERS/" + user.getUid() + "/CART").document(productId)
                            .get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    documentSnapshot.getReference().update("quantity", FieldValue.increment(number))
                                            .addOnCompleteListener(task -> {
                                                loading.setVisibility(View.GONE);
                                                addToCartBtn.setEnabled(true);
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProductDetailsActivity.this, "Add product to cart successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ProductDetailsActivity.this, "Some Errors Occur", Toast.LENGTH_SHORT).show();
                                                }

                                            });
                                } else {
                                    firebaseFirestore.collection("USERS/" + user.getUid() + "/CART").document(productId)
                                            .set(new CartItemToAdd(
                                                    productId,
                                                    number
                                            )).addOnCompleteListener(task -> {
                                                loading.setVisibility(View.GONE);
                                                addToCartBtn.setEnabled(true);
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProductDetailsActivity.this, "Add product to cart successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ProductDetailsActivity.this, "Some Errors Occur", Toast.LENGTH_SHORT).show();

                                                }

//                                                            addToCartBtn.setEnabled(true);
                                            });

                                }
                            });
                }
            });
        });


        /* ********* COUPON DIALOG********* */
        final Dialog checkCouponPriceDialog = new Dialog(ProductDetailsActivity.this);
        checkCouponPriceDialog.setContentView(R.layout.coupon_redeem_dialog);
        checkCouponPriceDialog.setCancelable(true);
        checkCouponPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView toggleRecyclerView = checkCouponPriceDialog.findViewById(R.id.toggle_recyclerview);
        opencouponsRecyclerView = checkCouponPriceDialog.findViewById(R.id.coupons_recyclerview);
        selectedCoupon = checkCouponPriceDialog.findViewById(R.id.selected_coupon);
        couponTitle = checkCouponPriceDialog.findViewById(R.id.coupon_title);
        couponExpiryDate = checkCouponPriceDialog.findViewById(R.id.coupon_validity);
        couponTBody = checkCouponPriceDialog.findViewById(R.id.coupon_body);


        TextView originalPrice = checkCouponPriceDialog.findViewById(R.id.original_price);
        TextView discountedPrice = checkCouponPriceDialog.findViewById(R.id.discounted_price);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        opencouponsRecyclerView.setLayoutManager(layoutManager);
        List<CouponModel> rewardModelList = new ArrayList<>();
        rewardModelList.add(new CouponModel("CashBack", "till, 2nd,June 2019", "GET 20% CashBack on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new CouponModel("Discount", "till, 2nd,June 2019", "GET 20% CashBack on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new CouponModel("Buy 1 get 1 Free", "till, 2nd,June 2019", "GET 20% CashBack on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new CouponModel("CashBack", "till, 2nd,June 2019", "GET 20% CashBack on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new CouponModel("Discount", "till, 2nd,June 2019", "GET 20% CashBack on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new CouponModel("Buy 1 get 1 Free", "till, 2nd,June 2019", "GET 20% CashBack on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new CouponModel("CashBack", "till, 2nd,June 2019", "GET 20% CashBack on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new CouponModel("Discount", "till, 2nd,June 2019", "GET 20% CashBack on any product above Rs. 200/- and below Rs. 3000/-"));
        rewardModelList.add(new CouponModel("Buy 1 get 1 Free", "till, 2nd,June 2019", "GET 20% CashBack on any product above Rs. 200/- and below Rs. 3000/-"));

        MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(rewardModelList, true);
        opencouponsRecyclerView.setAdapter(myRewardsAdapter);
        myRewardsAdapter.notifyDataSetChanged();

        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRecyclerView();
            }
        });


        couponRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCouponPriceDialog.show();
            }
        });
        /* ********* COUPON DIALOG********* */
    }

    private interface DialogListener {
        public void onDismiss();

        public void onPickNumber(int number);
    }

    public void showPickQuantity(DialogListener listener) {

        final Dialog d = new Dialog(ProductDetailsActivity.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(quantity);
        np.setMinValue(1);
        np.setMaxValue(100);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener((picker, oldVal, newVal) -> {

        });
        b1.setOnClickListener(v -> {
            listener.onPickNumber(np.getValue());
            d.dismiss();
        });
        b2.setOnClickListener(v -> {
            listener.onDismiss();
            d.dismiss();
        });
        d.show();


    }

    public static void showDialogRecyclerView() {
        if (opencouponsRecyclerView.getVisibility() == View.GONE) {
            opencouponsRecyclerView.setVisibility(View.VISIBLE);
            selectedCoupon.setVisibility(View.GONE);

        } else {
            opencouponsRecyclerView.setVisibility(View.GONE);
            selectedCoupon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            finish();
            return true;
        } else if (id == R.id.main_search_icon) {
            //todo:search
            return true;
        } else if (id == R.id.main_cart_icon) {
            Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
            showCart = true;
            startActivity(cartIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*********** RATINGS LAYOUT ******/
    private void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }

    /*********** RATINGS LAYOUT ******/


    private static class CartItemToAdd {
        private String productId;
        private int quantity;

        public CartItemToAdd(String productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public CartItemToAdd() {
            productId = "";
            quantity = 0;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}


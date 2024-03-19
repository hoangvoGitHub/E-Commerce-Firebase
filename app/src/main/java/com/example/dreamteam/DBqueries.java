package com.example.dreamteam;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class DBqueries {

    private static final String TAG = "DBqueries";
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();

    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadCategoryIds = new ArrayList<>();


    public static void loadCategories(final RecyclerView categoryRecyclerview, final Context context) {


        firebaseFirestore.collection("CATEGORIES").orderBy("index").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    categoryModelList.add(new CategoryModel(documentSnapshot.getId(), documentSnapshot.get("categoryIconLink").toString(), documentSnapshot.get("categoryName").toString()));
                }
                CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                categoryRecyclerview.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();
            } else {
                String error = task.getException().getMessage();
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
        });
    }


    public static void loadFragmentData(final RecyclerView recyclerView,
                                        final Context context, final int index,
                                        String categoryId) {


        firebaseFirestore.collection("CATEGORIES")
                .document(categoryId)
                .collection("TOP_DEALS")
                .orderBy("index").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        final long viewType = (long) snapshot.get("view_type");
                        if (viewType == 0) {
                            List<SliderModel> sliderModelList = new ArrayList<>();
                            for (String banner : (List<String>) snapshot.get("banners")) {
                                sliderModelList.add(new SliderModel(banner, "white"));
                            }
                            lists.get(index).add(new HomePageModel(0, sliderModelList));
                        } else if (viewType == 1) {
                            lists.get(index).add(new HomePageModel(1, snapshot.get("strip_ad_banner").toString(), "white"));

                        } else if (viewType == 2) {
                            List<WishlistModel> viewAllProductList = new ArrayList<>();

                            List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
                            AtomicInteger documentFetched = new AtomicInteger();
                            List<DocumentReference> documentReferences = (List<DocumentReference>) snapshot.get("products");
                            for (int i = 0; i < documentReferences.size(); i++) {
                                DocumentReference docRef = documentReferences.get(i);
                                docRef.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                    }
                                });
                                docRef.get().addOnSuccessListener(documentSnapshot -> {
                                    horizontalProductScrollModelList.add(
                                            new HorizontalProductScrollModel
                                                    (documentSnapshot.getId(),
                                                            ((List<String>) documentSnapshot.get("images")).get(0),
                                                            documentSnapshot.get("name").toString(),
                                                            documentSnapshot.get("description").toString(),
                                                            documentSnapshot.get("price").toString()));
                                    documentFetched.getAndIncrement();
                                    if (documentFetched.get() == documentReferences.size()) {
                                        lists.get(index).add(new HomePageModel(2, "Category name", "white", horizontalProductScrollModelList));
                                        HomePageAdapter adapter = new HomePageAdapter(lists.get(index));
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                });


                            }


                        } else if (viewType == 3) {
                            //Grid view
                            List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                            Log.d(TAG, "loadFragmentData: " + snapshot.getData());
                            AtomicInteger documentFetched = new AtomicInteger();
                            AtomicBoolean isCompleted = new AtomicBoolean(false);
                            List<DocumentReference> documentReferences = (List<DocumentReference>) snapshot.get("products");
                            if (documentReferences.isEmpty()) {
                                isCompleted.set(true);
                            }
                            for (int i = 0; i < documentReferences.size(); i++) {
                                DocumentReference docRef = documentReferences.get(i);
                                final int finalI = i;
                                docRef.get().addOnSuccessListener(documentSnapshot -> {
                                    gridLayoutModelList.add(
                                            new HorizontalProductScrollModel
                                                    (documentSnapshot.getId(),
                                                            ((List<String>) documentSnapshot.get("images")).get(0),
                                                            documentSnapshot.get("name").toString(),
                                                            documentSnapshot.get("description").toString(),
                                                            documentSnapshot.get("price").toString()));


                                    documentFetched.getAndIncrement();
                                    if (documentFetched.get() == documentReferences.size()) {
                                        lists.get(index).add(new HomePageModel(2, "Category name", "white", gridLayoutModelList));
                                        HomePageAdapter adapter = new HomePageAdapter(lists.get(index));
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }

                                });

                            }
                        }
                        HomePageAdapter adapter = new HomePageAdapter(lists.get(index));
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(e -> {
                    String error = e.getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    HomeFragment.swipeRefreshLayout.setRefreshing(false);

                });


//
//
//        firebaseFirestore.collection("CATEGORIES")
//                .document(categoryId.toUpperCase())
//                .collection("TOP_DEALS")
//                .orderBy("index").get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                            if ((long) documentSnapshot.get("view_type") == 0) {
//                                List<SliderModel> sliderModelList = new ArrayList<>();
//                                long no_of_banners = (long) documentSnapshot.get("no_of_banners");
//                                for (long x = 1; x < no_of_banners + 1; x++) {
//                                    sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString(), documentSnapshot.get("banner_" + x + "_background").toString()));
//                                }
//                                lists.get(index).add(new HomePageModel(0, sliderModelList));
//
//                            } else if ((long) documentSnapshot.get("view_type") == 1) {
//                                lists.get(index).add(new HomePageModel(1, documentSnapshot.get("strip_ad_banner").toString(), documentSnapshot.get("background").toString()));
//                            } else if ((long) documentSnapshot.get("view_type") == 2) {
//
//                                List<WishlistModel> viewAllProductList = new ArrayList<>();
//
//                                List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
//                                long no_of_products = (long) documentSnapshot.get("no_of_products");
//                                for (long x = 1; x < no_of_products + 1; x++) {
//                                    horizontalProductScrollModelList.add(
//                                            new HorizontalProductScrollModel
//                                                    (documentSnapshot.get("product_ID_" + x).toString(),
//                                                            documentSnapshot.get("product_image_" + x).toString(),
//                                                            documentSnapshot.get("product_title_" + x).toString(),
//                                                            documentSnapshot.get("product_subtitle_" + x).toString(),
//                                                            documentSnapshot.get("product_price_" + x).toString()));
//                                    viewAllProductList.add(
//                                            new WishlistModel(documentSnapshot.get("product_image_" + x).toString()
//                                                    , documentSnapshot.get("product_full_title_" + x).toString()
//                                                    , (long) documentSnapshot.get("free_coupons_" + x)
//                                                    , documentSnapshot.get("average_rating_" + x).toString()
//                                                    , (long) documentSnapshot.get("total_ratings_" + x)
//                                                    , documentSnapshot.get("product_price_" + x).toString()
//                                                    , documentSnapshot.get("cutted_price_" + x).toString()
//                                                    , (boolean) documentSnapshot.get("COD_" + x)));
//                                }
//                                lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList, viewAllProductList));
//
//                            } else if ((long) documentSnapshot.get("view_type") == 3) {
//                                List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
//                                long no_of_products = (long) documentSnapshot.get("no_of_products");
//                                for (long x = 1; x < no_of_products + 1; x++) {
//                                    gridLayoutModelList.add(
//                                            new HorizontalProductScrollModel
//                                                    (documentSnapshot.get("product_ID_" + x).toString(),
//                                                            documentSnapshot.get("product_image_" + x).toString(),
//                                                            documentSnapshot.get("product_title_" + x).toString(),
//                                                            documentSnapshot.get("product_subtitle_" + x).toString(),
//                                                            documentSnapshot.get("product_price_" + x).toString()));
//                                }
//                                lists.get(index).add(new HomePageModel(3, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), gridLayoutModelList));
//
//                            }
//
//                        }
//                        HomePageAdapter adapter = new HomePageAdapter(lists.get(index));
//                        recyclerView.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//                        HomeFragment.swipeRefreshLayout.setRefreshing(false);
//
//                    } else {
//                        String error = task.getException().getMessage();
//                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
//                    }
//                });


    }

    private void addData(DocumentReference productRef, List<HorizontalProductScrollModel> models) {
        productRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                var documentSnapshot = task.getResult();
                models.add(
                        new HorizontalProductScrollModel
                                (documentSnapshot.getId(),
                                        ((List<String>) documentSnapshot.get("images")).get(0),
                                        documentSnapshot.get("name").toString(),
                                        documentSnapshot.get("description").toString(),
                                        documentSnapshot.get("price").toString()));
            }
        });
    }

    private void runTask(Queue<Task> tasks) {

    }

}






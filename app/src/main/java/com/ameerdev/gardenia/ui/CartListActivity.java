package com.ameerdev.gardenia.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ameerdev.gardenia.MainActivity;
import com.ameerdev.gardenia.R;
import com.ameerdev.gardenia.RegisterActivity;
import com.ameerdev.gardenia.adapter.CartListAdapter;
import com.ameerdev.gardenia.fragments.GardenFragment;
import com.ameerdev.gardenia.models.Plant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.GardeniaApi;

public class CartListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CartListAdapter mAdapter;
    TextView tv_no_cart_item_found;
    GardeniaApi gardeniaApi = GardeniaApi.getInstance();
    Button btn_checkout;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;

   TextView tv_subtotal,tv_shipping_charge,tv_total ;
     int  subtotal = 0;
     int shipping = 10;
     int total = 0;

    static ArrayList<Plant>cartList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);


        tv_no_cart_item_found = findViewById(R.id.tv_no_cart_item_found);
        btn_checkout = findViewById(R.id.btn_checkout);
        tv_subtotal = findViewById(R.id.tv_sub_total);
        tv_shipping_charge = findViewById(R.id.tv_shipping_charge);
        tv_total = findViewById(R.id.tv_total_amount);

        if (gardeniaApi.getCartList()!=null){
            cartList = gardeniaApi.getCartList();
            try{
                for (int i=0 ; i<cartList.size();i++){
                    int price =  Integer.parseInt(cartList.get(i).getPrice2());
                    subtotal+=price;

                }
            }
            catch (NumberFormatException ex){
                ex.printStackTrace();
            }
        }


        total = subtotal+shipping;

        String s1= "$"+subtotal+".00";
        String s2= "$"+shipping+".00";
        String s3= "$"+total+".00";

        tv_subtotal.setText(s1);
        tv_shipping_charge.setText(s2);
        tv_total.setText(s3);

        /**
         * Checkout Buttonnnnnnnnnn
         */
        btn_checkout.setOnClickListener(view -> {

            if(gardeniaApi.getUserId()!=null && gardeniaApi.getCartList()!=null){
                collectionReference =
                        db.collection("UserPlants");

                Map<String, String> userPlantsObj = new HashMap<>();
                userPlantsObj.put("userId", gardeniaApi.getUserId());

                for (int i=0 ; i<cartList.size();i++) {

                    userPlantsObj.put("name" ,cartList.get(i).getName());
                    userPlantsObj.put("plant_sun" ,cartList.get(i).getPlant_sun());
                    userPlantsObj.put("plant_water" ,cartList.get(i).getPlant_water());
                    userPlantsObj.put("plant_profile_img" ,cartList.get(i).getPlant_profile_img());

                    //save to our Firestore database
                    collectionReference.add(userPlantsObj)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    documentReference.get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                }
                                            });
                                }
                            });
                }
                Intent intent = new Intent(CartListActivity.this,
                        MainActivity.class);
                startActivity(intent);
                GardenFragment.setCount(0);
                Toast.makeText(CartListActivity.this,
                        "Payment Successful, Thank You",Toast.LENGTH_SHORT)
                        .show();
                gardeniaApi.emptyCart();

            }
        });

        mRecyclerView = findViewById(R.id.rv_cart_items_list);

        /**
         * Show Cart Items if exist
         */
        if (gardeniaApi.getCartList()!=null){

            mAdapter = new CartListAdapter(gardeniaApi.getCartList(), this);

            tv_no_cart_item_found.setVisibility(View.GONE);

            mRecyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setHasFixedSize(true);

        }else{
            tv_no_cart_item_found.setVisibility(View.VISIBLE);
        }
    }




}
package com.ameerdev.gardenia.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.ameerdev.gardenia.R;
import com.ameerdev.gardenia.adapter.PlantRecyclerViewAdapter;
import com.ameerdev.gardenia.models.Plant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IndoorActivity extends AppCompatActivity {

    private static final ArrayList<Plant> plantList= new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PlantRecyclerViewAdapter mAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    static int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor);
        getPlantList();
        mRecyclerView = findViewById(R.id.rv_indoor);
        mAdapter = new PlantRecyclerViewAdapter(plantList,this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);


    }
    void getPlantList(){

        if (count == 0){
            count++;
            db.collection("Indoor")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot plants : task.getResult()) {
                                    //Log.d("suuu", document.getId() + " => " + document.getData());

                                    Plant plant = plants.toObject(Plant.class);

                                    plantList.add(plant);

                                }
                            } else {
                                Log.d("suuu", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
}

}
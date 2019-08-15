package com.schwifty.bitseats_admin;


import android.os.health.UidHealthStats;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemsAvailability extends AppCompatActivity {

    private String mess;

    DatabaseReference reference;

    List<Items> items;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_availability);
        mess = getIntent().getStringExtra("mess");

        reference = FirebaseDatabase.getInstance().getReference()
                .child("Menu")
                .child("Mess")
                .child(mess)
                .child("0");
        items = new ArrayList<>();
        inflater = LayoutInflater.from(this);

        LoadData();

    }

    private void LoadData()
    {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("hundred_uo",dataSnapshot.getValue().toString());

                items.clear();

                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Items h = new Items(
                            dataSnapshot1.getKey().toString(),
                            dataSnapshot1.child("Name").getValue().toString(),
                            Double.parseDouble(dataSnapshot1.child("Price").getValue().toString()),
                            dataSnapshot1.child("isAvailable").getValue().toString(),
                            dataSnapshot1.child("isPackagable").getValue().toString(),
                            Double.parseDouble(dataSnapshot1.child("packingPrice").getValue().toString())
                            );

                    items.add(h);

                    DisplayViews();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayViews()
    {
        final LinearLayout root = (LinearLayout)findViewById(R.id.ItemsAval_root);
        root.removeAllViews();

        Iterator<Items> iterator =items.iterator();
        while(iterator.hasNext())
        {
            final Items item = iterator.next();

            //Inflate here
            LinearLayout view_inItem = (LinearLayout) inflater.inflate(R.layout.template_items, null, false);
            TextView name = view_inItem.findViewById(R.id.template_ItmName);
            TextView qty = view_inItem.findViewById(R.id.template_Qty);
            ImageView vImg1 = view_inItem.findViewById(R.id.template_More1);
            ImageView vImg2 = view_inItem.findViewById(R.id.template_More2);


            name.setText(item.Name);
            qty.setVisibility(View.GONE);

            if(item.isAvailable.contains("true"))
            {
                vImg1.setVisibility(View.VISIBLE);
                vImg2.setVisibility(View.GONE);
            }
            else
            {
                vImg1.setVisibility(View.GONE);
                vImg2.setVisibility(View.VISIBLE);
            }

            vImg1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    reference.child(item.UID).child("isAvailable").setValue(false);
                    LoadData();
                }
            });

            vImg2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reference.child(item.UID).child("isAvailable").setValue(true);
                    LoadData();
                }
            });

            root.addView(view_inItem);

        }
    }


    class Items
    {
        String UID;
        String Name;
        Double Price;
        String isAvailable;
        String isPackagable;
        Double packingPrice;

        public Items(String UID, String name, Double price, String isAvailable, String isPackagable, Double packingPrice) {
            this.UID = UID;
            Name = name;
            Price = price;
            this.isAvailable = isAvailable;
            this.isPackagable = isPackagable;
            this.packingPrice = packingPrice;
        }
    }
}
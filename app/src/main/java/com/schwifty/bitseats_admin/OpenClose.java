package com.schwifty.bitseats_admin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OpenClose extends AppCompatActivity {

    TextView vOpenShop, vOpenTakeAway, vOpenEatHere, vOpenDelivery;
    TextView vCloseShop, vCloseTakeAway, vCloseEatHere, vCloseDelivery;
    String mess;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_close);
        vOpenShop = findViewById(R.id.OpenShop);
        vOpenTakeAway = findViewById(R.id.OpenTakeAway);
        vOpenEatHere = findViewById(R.id.OpenEatHere);
        vOpenDelivery = findViewById(R.id.OpenDelivery);

        vCloseShop = findViewById(R.id.CloseShop);
        vCloseTakeAway = findViewById(R.id.CloseTakeAway);
        vCloseEatHere = findViewById(R.id.CloseEatHere);
        vCloseDelivery = findViewById(R.id.CloseDelivery);

        mess = getIntent().getStringExtra("mess");

    }

    @Override
    protected void onStart() {
        super.onStart();

        CloseOpenDetection();

        CloseOpenListners();

    }

    private void CloseOpenListners()
    {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Variables");

        vOpenShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Status").child(mess).setValue("opened");
            }
        });
        vCloseShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Status").child(mess).setValue("closed");
            }
        });

        vOpenTakeAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Allowance").child(mess+"TakeAway").setValue("opened");
            }
        });

        vCloseTakeAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Allowance").child(mess+"TakeAway").setValue("closed");
            }
        });

        vOpenEatHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Allowance").child(mess+"EatHere").setValue("opened");
            }
        });

        vCloseEatHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Allowance").child(mess+"EatHere").setValue("closed");
            }
        });

        vOpenDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Allowance").child(mess+"Delivery").setValue("opened");
            }
        });

        vCloseDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Allowance").child(mess+"Delivery").setValue("closed");
            }
        });


    }

    private void CloseOpenDetection() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Variables").child("Allowance").child(mess+"Delivery").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String _opt = dataSnapshot.getValue().toString();

                if(_opt.contains("opened"))
                {
                    vOpenDelivery.setVisibility(View.GONE);
                    vCloseDelivery.setVisibility(View.VISIBLE);
                }
                else
                {
                    vOpenDelivery.setVisibility(View.VISIBLE);
                    vCloseDelivery.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("Variables").child("Allowance").child(mess+"EatHere").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String _opt = dataSnapshot.getValue().toString();

                if(_opt.contains("opened"))
                {
                    vOpenEatHere.setVisibility(View.GONE);
                    vCloseEatHere.setVisibility(View.VISIBLE);
                }
                else
                {
                    vOpenEatHere.setVisibility(View.VISIBLE);
                    vCloseEatHere.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("Variables").child("Allowance").child(mess+"TakeAway").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String _opt = dataSnapshot.getValue().toString();

                if(_opt.contains("opened"))
                {
                    vOpenTakeAway.setVisibility(View.GONE);
                    vCloseTakeAway.setVisibility(View.VISIBLE);
                }
                else
                {
                    vOpenTakeAway.setVisibility(View.VISIBLE);
                    vCloseTakeAway.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("Variables").child("Status").child(mess).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String _opt = dataSnapshot.getValue().toString();

                if(_opt.contains("opened"))
                {
                    vOpenShop.setVisibility(View.GONE);
                    vCloseShop.setVisibility(View.VISIBLE);
                }
                else
                {
                    vOpenShop.setVisibility(View.VISIBLE);
                    vCloseShop.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

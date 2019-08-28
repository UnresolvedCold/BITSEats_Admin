package com.schwifty.bitseats_admin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class MainActivity extends AppCompatActivity {

    String DEBUGTAG = "Hundred_TAG";

    String mess = "INS";

    String MallId,RestId;

    DatabaseReference RefToOrders;

    LinearLayout vNewOrders;
    LinearLayout vNewOrdersList;
    LinearLayout vProcessingOrders;
    LinearLayout vProcessingOrdersList;
    LinearLayout vCompletedOrders;
    LinearLayout vCompletedOrdersList;

    LayoutInflater inflater;

    List<OrderRef> orders;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.activeOrders:
                    vNewOrders.setVisibility(View.VISIBLE);
                    vProcessingOrders.setVisibility(View.GONE);
                    vCompletedOrders.setVisibility(View.GONE);


                    return true;

                case R.id.processingOrders:
                    vNewOrders.setVisibility(View.GONE);
                    vProcessingOrders.setVisibility(View.VISIBLE);
                    vCompletedOrders.setVisibility(View.GONE);
                    return true;

                case R.id.finishedOrders:
                    vNewOrders.setVisibility(View.GONE);
                    vProcessingOrders.setVisibility(View.GONE);
                    vCompletedOrders.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //MallId="MallUID";
        RestId="BITS1";

        //RestId = "resid1";
        vNewOrders = findViewById(R.id.NewOrders);
        vNewOrdersList=findViewById(R.id.NewOrdersList);
        vProcessingOrders = findViewById(R.id.ProcessingOrders);
        vProcessingOrdersList=findViewById(R.id.ProcessingOrdersList);
        vCompletedOrders=findViewById(R.id.CompletedOrders);
        vCompletedOrdersList=findViewById(R.id.CompletedOrdersList);

        vNewOrders.setVisibility(View.VISIBLE);
        vProcessingOrders.setVisibility(View.GONE);
        vCompletedOrders.setVisibility(View.GONE);

       /* RefToOrders= FirebaseDatabase.getInstance().getReference()
                .child("Mall").child(MallId).child("Order")
                .child(RestId);*/

        RefToOrders= FirebaseDatabase.getInstance().getReference()
                .child("TrackNonDeliveryOrders");

        orders = new ArrayList<>();

        inflater = LayoutInflater.from(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        RestId="BITS1";
        // RestId = "resid1";

        AttachClickListnerForMenuBtn();

        RefToOrders.orderByChild("Mess").startAt(mess).endAt(mess).addValueEventListener(order_listner);

    }


    private void AttachClickListnerForMenuBtn()
    {
        final ImageView vMore = findViewById(R.id.more);
        final View vTab = findViewById(R.id.maintab);
        final ImageView vClose = findViewById(R.id.close);
        final TextView vOption1 = findViewById(R.id.Menu_Option1);
        final TextView vOption2 = findViewById(R.id.Menu_Option2);
        final TextView vOption3 = findViewById(R.id.Menu_Option3);

        vOption1.setText("History");
        vOption2.setText("Items Availability");
        vOption3.setVisibility(View.GONE);
        vOption1.setVisibility(View.GONE);

        vMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenCloseTab(vTab);
            }
        });

        vClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenCloseTab(vTab);
            }
        });

       /* vOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,AllOrders.class);
                intent.putExtra("mess",mess);
                startActivity(intent);
            }
        });*/

        vOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,ItemsAvailability.class);
                intent.putExtra("mess",mess);
                startActivity(intent);
            }
        });

       /* vOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });*/

    }

    private void OpenCloseTab(View view)
    {
        if(view.getVisibility()==View.VISIBLE)
        {
            view.setVisibility(View.GONE);
        }
        else
        {
            view.setVisibility(View.VISIBLE);
        }
    }


    ValueEventListener order_listner = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
            orders.clear();
            Log.d(DEBUGTAG,"order_listner : Start of onDataChange()");
            for(DataSnapshot snapshot:dataSnapshot.getChildren())
            {
                if (snapshot.hasChild("OrderUID")) {
                    final String orderUID = snapshot.child("OrderUID").getValue().toString();
                    final String Token = snapshot.child("token").getValue().toString();
                    final String isPaid = snapshot.child("isPaid").getValue().toString();
                    //if already present in the list ignore
                   // if (!isAlreadyPresent(orderUID))
                    if(!isPaid.contains("false"))
                    {

                        Log.d(DEBUGTAG,"order_listner : isAlready... "+isAlreadyPresent(orderUID)+ " UID - "+snapshot.child("OrderUID").getValue().toString()+" Token - "+snapshot.child("token").getValue().toString() );
                        final OrderRef order = new OrderRef(
                                snapshot.getKey().toString(),
                                Math.round(Double.parseDouble(snapshot.child("Cost").getValue().toString())*100.0)*100.0,
                                snapshot.child("Mess").getValue().toString(),
                                snapshot.child("Mob").getValue().toString(),
                                snapshot.child("Name").getValue().toString(),
                                snapshot.child("OrderUID").getValue().toString(),
                                snapshot.child("deviceId").getValue().toString(),
                                snapshot.child("device_token_id").getValue().toString(),
                                Boolean.parseBoolean(snapshot.child("isPaid").getValue().toString()),
                                snapshot.child("mode").getValue().toString(),
                                snapshot.child("status").getValue().toString(),
                                snapshot.child("token").getValue().toString()

                        );


                        FirebaseDatabase.getInstance().getReference().child("Order").child(orderUID)
                                .child("Ordered Items")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for(DataSnapshot s : dataSnapshot.getChildren())
                                        {
                                            Items i = new Items(s.getKey().toString(),
                                                    Integer.parseInt(s.getValue().toString()));

                                           // Log.d(DEBUGTAG,"1"+i.name+" "+i.qty);
                                            order.items.add(i);
                                        }

                                      //  Log.d(DEBUGTAG,"3 "+orders.size()+ " "+ order.items.size());
                                        if(!isAlreadyPresent(orderUID)) {
                                            orders.add(order);
                                            Log.d(DEBUGTAG, "order_listner : size of orders " + orders.size());
                                            DisplayOrders();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    }

                }
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }


        private Boolean isAlreadyPresent(String orderUID)
        {
            Iterator<OrderRef> itr = orders.iterator();
            Log.d(DEBUGTAG,""+orders.size());
            while(itr.hasNext())
            {
                OrderRef o =itr.next();
                Log.d(DEBUGTAG,o.orderUID+" "+orderUID);
                if(o.orderUID.contains(orderUID))
                {
                    Log.d(DEBUGTAG,"true");
                    return true;
                }

            }
            Log.d(DEBUGTAG,"false");
            return false;
        }
    };


    private void DisplayOrders()
    {
        vNewOrdersList.removeAllViews();
        vCompletedOrdersList.removeAllViews();
        vProcessingOrdersList.removeAllViews();

        Log.d(DEBUGTAG,"DisplayOrders called");

        Iterator<OrderRef> iterator = orders.iterator();

        while(iterator.hasNext())
        {
            final OrderRef o = iterator.next();
           // if(o.isPaid)
            {
                LinearLayout view_inComp = (LinearLayout) inflater.inflate(R.layout.template_order, null, false);
                LinearLayout view_inComp_list = view_inComp.findViewById(R.id.itemList);
                TextView vToken = view_inComp.findViewById(R.id.Token);
                TextView vExtraRequest = view_inComp.findViewById(R.id.extrarequest);

                vToken.setText(o.token);
                vExtraRequest.setText(o.mode);

                Log.d(DEBUGTAG, "OrderUID :" + o.orderUID);

                Iterator<Items> _iterator = o.items.iterator();

                while (_iterator.hasNext()) {

                    Items items = _iterator.next();
                    LinearLayout view_inItem = (LinearLayout) inflater.inflate(R.layout.template_items, null, false);
                    TextView name = view_inItem.findViewById(R.id.template_ItmName);
                    TextView qty = view_inItem.findViewById(R.id.template_Qty);

                    name.setText(items.name);
                    qty.setText(items.qty + "");

                    view_inComp_list.addView(view_inItem);

                }

                if (o.status.equals("end")) {
                    vCompletedOrdersList.addView(view_inComp);
                } else if (o.status.equals("pro")) {
                    vProcessingOrdersList.addView(view_inComp);

                    view_inComp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog = new Dialog(MainActivity.this);
                            dialog.setContentView(R.layout.dialog_confirm);
                            dialog.setTitle("Confirm");
                            // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            // dialog.setCanceledOnTouchOutside(false);

                            View vOK = dialog.findViewById(R.id.OK);
                            View vCancel = dialog.findViewById(R.id.Cancel);
                            TextView vconfirmation = dialog.findViewById(R.id.confirmation);
                            vconfirmation.setText("Are you sure the order is ready?");

                            vOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d("_Hundred_1", "Status changed to E");
                                    o.status = "end";
                                    RefToOrders.child(o.UID).child("status").setValue("end");
                                    DisplayOrders();
                                    dialog.dismiss();
                                }
                            });

                            vCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();

                        }
                    });


                } else if (o.status.equals("new")) {
                    vNewOrdersList.addView(view_inComp);
                    view_inComp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final Dialog dialog = new Dialog(MainActivity.this);
                            dialog.setContentView(R.layout.dialog_confirm);
                            dialog.setTitle("Confirm");
                            // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            // dialog.setCanceledOnTouchOutside(false);

                            View vOK = dialog.findViewById(R.id.OK);
                            View vCancel = dialog.findViewById(R.id.Cancel);
                            TextView vconfirmation = dialog.findViewById(R.id.confirmation);
                            vconfirmation.setText("Are you sure you want to send the order for processing?");

                            vOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d("_Hundred_1", "Status changed to P");
                                    o.status = "pro";
                                    RefToOrders.child(o.UID).child("status").setValue("pro");
                                    DisplayOrders();
                                    dialog.dismiss();
                                }
                            });

                            vCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();


                        }
                    });
                }
            }

        }

    }


    //Classes
    class OrderRef
    {
        String UID;
        double cost;
        String mess;
        String mob;
        String name;
        String orderUID;
        String deviceId;
        String device_token_id;
        Boolean isPaid;
        String mode;
        String status;
        String token;
        List<Items> items;

        public OrderRef(String UID,double cost, String mess, String mob, String name, String orderUID, String deviceId, String device_token_id, Boolean isPaid, String mode, String status, String token) {
            this.UID = UID;
            this.cost = cost;
            this.mess = mess;
            this.mob = mob;
            this.name = name;
            this.orderUID = orderUID;
            this.deviceId = deviceId;
            this.device_token_id = device_token_id;
            this.isPaid = isPaid;
            this.mode = mode;
            this.status = status;
            this.token = token;
            items = new ArrayList<>();
        }
    }

    class Items
    {
        String name;
        int qty;

        public Items(String name, int qty) {
            this.name = name;
            this.qty = qty;
        }
    }
}

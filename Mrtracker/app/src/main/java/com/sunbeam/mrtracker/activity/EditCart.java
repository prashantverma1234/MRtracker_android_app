package com.sunbeam.mrtracker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sunbeam.mrtracker.R;
import com.sunbeam.mrtracker.model.Mycart;
import com.sunbeam.mrtracker.utils.urls;

public class EditCart extends AppCompatActivity {

    int count;
    int amount;
    int temp;
    int price;

    ImageView imageView;
    TextView textName;
    TextView textPrice1;
    TextView textDiscount,textDtext,textDescription;
    TextView textWithDiscount,quantity,textAmount;
    LinearLayout tdescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cart);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("blue")));
        bar.setTitle("Edit Products");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);


        imageView = findViewById(R.id.Image);
        textName = findViewById(R.id.name);
        textPrice1 = findViewById(R.id.textPrice);
        textDiscount = findViewById(R.id.textdiscount);
        textWithDiscount = findViewById(R.id.priceWithDiscount);
        textAmount = findViewById(R.id.discountAmount);

        textDtext = findViewById(R.id.dName);
        // tdescription = findViewById(R.id.ddname);

        textDescription = findViewById(R.id.description);


        Intent intent = getIntent();

        String url = urls.images();
        url = url + intent.getStringExtra("image");
        Ion.with(this).load(url).withBitmap().intoImageView(imageView);

        textName.setText(""+intent.getStringExtra("name"));
        textPrice1.setText("MRP ₹"+intent.getIntExtra("price",0));
        textDiscount.setText("  "+intent.getIntExtra("discount",0)+"% off");
        textWithDiscount.setText("₹"+intent.getIntExtra("priceWithDiscount",0));

        amount = intent.getIntExtra("priceWithDiscount",0);
        textAmount.setText("₹"+amount);

        temp = amount;
        price = intent.getIntExtra("price",0);

        textDtext.setText("Information about "+intent.getStringExtra("name")+":-");
        textDescription.setText(""+intent.getStringExtra("description"));

        // textDescription.setMovementMethod(new ScrollingMovementMethod());



    }


    @Override
    protected void onResume() {

        Intent intent1 = getIntent();
        int q = intent1.getIntExtra("quantity",0);
        count = q;

        quantity = findViewById(R.id.countAmount);
        quantity.setText(""+count);


        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onIncrement(View view) {


        quantity = findViewById(R.id.countAmount);
        count++;

        quantity.setText(""+count);

        amount = temp * count;
        textAmount.setText("₹"+amount);


    }

    public void ondecrement(View view) {

        quantity = findViewById(R.id.countAmount);
        if(count == 0){
            Toast.makeText(this, "Can not be decremented", Toast.LENGTH_SHORT).show();
        }
        else{
            count--;
            quantity.setText(""+count);

            amount = temp * count;
            textAmount.setText("₹"+amount);
        }

    }


    public void onAddtoCart(View view) {


        //shared prefrence
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int status = preferences.getInt("login_status",0);


        if(status != 1){
            Toast.makeText(this,"You are not logged in first login yourself",Toast.LENGTH_LONG).show();
        }
        else{


            // logic to add product in orderdetails table
            final Intent intent = getIntent();

            int productId = intent.getIntExtra("id",0);
            int MRid = preferences.getInt("id",0);
            int orderDetailsTableID = intent.getIntExtra("orderDetailsTableID",0);


            int totalAmount = count * price;
            int totalDiscount = count * temp;
            totalDiscount = totalAmount - totalDiscount;

            if(count != 0){
                String url = urls.EditInCart();

                JsonObject body = new JsonObject();
                body.addProperty("Quantity",count);
                body.addProperty("totalAmount",amount);
                body.addProperty("totalDiscount",totalDiscount);
                body.addProperty("MRid",MRid);
                body.addProperty("productID",productId);
                body.addProperty("orderDetailsTableID",orderDetailsTableID);

                Log.e("EditCart",""+orderDetailsTableID);

                Ion.with(this).load("POST",url).setJsonObjectBody(body).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        String status = result.get("status").getAsString();

                        if(status.equals("success")){
                            Toast.makeText(getApplicationContext(),"product updated",Toast.LENGTH_SHORT).show();


                            Intent intent2 = new Intent(getApplicationContext(), Cart.class);
                            startActivity(intent2);
                            finish();


                        }
                        else{
                            String error = result.get("error").getAsString();
                            Log.e("ProductDetails",error);

                            Toast.makeText(getApplicationContext(),"Somthing went wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //  finish(); .. this finish mathod can call before success data
            }
            else{
                String url = urls.cartDelete();

                JsonObject body = new JsonObject();
                body.addProperty("id",orderDetailsTableID);

                Ion.with(this).load("POST",url).setJsonObjectBody(body).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        String status = result.get("status").getAsString();

                        if(status.equals("success")){
                            Toast.makeText(getApplicationContext(),"product updated",Toast.LENGTH_SHORT).show();

                            Intent intent2 = new Intent(getApplicationContext(), Cart.class);
                            startActivity(intent2);
                            finish();

                        }
                        else{
                            String error = result.get("error").getAsString();
                            //Log.e("ProductDetails",error);

                            Toast.makeText(getApplicationContext(),"Somthing went wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //  finish(); .. this finish mathod can call before success data
            }

        }
    }








}

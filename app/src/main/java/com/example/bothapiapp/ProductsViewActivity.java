package com.example.bothapiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bothapiapp.recyclerview.Product;
import com.example.bothapiapp.recyclerview.ProductAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsViewActivity extends AppCompatActivity {

    RecyclerView rv; List<Product> cart;
    ProductAdapter adapter; LinearLayoutManager layoutManager;

    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        rv = findViewById(R.id.product_recycler_view);
        layoutManager = new LinearLayoutManager(ProductsViewActivity.this );
        cart = new ArrayList<>();

        Bundle extras = getIntent().getExtras();

        code = extras.getString("code");

        productAPIRequest(code);
    }

    private void productAPIRequest( String code ) {
        String url = "https://tmiapi-dev.mitraindogrosir.co.id/api/get_data_member";
        RequestQueue queue = Volley.newRequestQueue(ProductsViewActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject memberList = new JSONObject(response);
                        JSONArray memberData  = memberList
                                .getJSONObject("message")
                                .getJSONArray("data_product");

                        Log.e("Status Value", memberList.getString("status"));
                        // Proves that the API Responded
                        Log.e("Size", "products size is " + memberData.length() );

                        JSONObject apple;

                        for (int i = 0; i < memberData.length(); i++) {
                            try {
                                apple = memberData.getJSONObject(i);

                                cart.add( new Product(
                                        apple.getString("product_name"),
                                        apple.getString("product_code"),
                                        apple.getString("price")
                                ) );
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }, error -> {
                Utils.showToast(ProductsViewActivity.this, "Login Failed: " + error);
                Log.e("Error POST VOLLEY", error.toString() );
            }) {
            @Override
            public Map<String, String> getHeaders( ) {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + code); return headers;
            }
        };

        rv.setAdapter(adapter); rv.setLayoutManager(layoutManager);
        queue.add(request);
    }
}
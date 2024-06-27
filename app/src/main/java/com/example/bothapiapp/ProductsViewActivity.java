package com.example.bothapiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
import java.util.Locale;
import java.util.Map;

public class ProductsViewActivity extends AppCompatActivity {

    RecyclerView rv; List<Product> cart;
    ProductAdapter adapter; LinearLayoutManager layoutManager;

    SearchView rvSearchView;

    String code;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.rv_menu, menu);

        rvSearchView = (SearchView) menu.findItem(R.id.rv_search_bar).getActionView();
        rvSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // IGNORE
            }

            @Override
            public boolean onQueryTextChange( String filter ) {
                // IF RV data is not empty
                if ( !cart.isEmpty() ){
                    // IF text input
                    if (filter.length() != 0 ){
                        filterData( filter );
                    } else originalData();
                    return true;
                } else {
                    Utils.showToast(ProductsViewActivity.this,
                            "Please wait until data loads");
                    return false;
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // Set data after filtered
    private void filterData(String filter) {
        List<Product> filteredList = new ArrayList<>();
        for ( Product filteredProductPosition : cart ) {
            if ( filteredProductPosition.getProduct_name( )
                    .toLowerCase( ).contains( filter.toLowerCase( Locale.ROOT ) ) ) {
                filteredList.add( filteredProductPosition );
            }
        } adapter.setDataShown( filteredList );
    }

    // return original data when Search is empty
    private void originalData() {
        adapter.setDataShown(cart);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        rv = findViewById(R.id.product_recycler_view);
        layoutManager = new LinearLayoutManager(ProductsViewActivity.this );
        rv.setLayoutManager(layoutManager);

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

                        adapter = new ProductAdapter(ProductsViewActivity.this, cart);
                        rv.setAdapter(adapter);
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

        queue.add(request);
    }
}
package com.example.bothapiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bothapiapp.databasehandler.DBHandler;
import com.example.bothapiapp.recyclerview.Product;
import com.example.bothapiapp.recyclerview.ProductAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductsViewActivity extends AppCompatActivity {

    RecyclerView rv;
    ProductAdapter adapter; LinearLayoutManager layoutManager;

    List<Product> cart = new ArrayList<>();

    DBHandler dbHandler;

    TextView waiting;
    SearchView rvSearchView;

    ProgressBar loadingAnimation;

    String LOGIN_INSTANCE = "Preference Login";

    String LOGIN_PREFERENCE = "LOGIN PREFERENCES";
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
        }); return super.onCreateOptionsMenu(menu);
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
        if ( item.getTitle( ).equals( getString( R.string.LogOut ) ) ) {
            SharedPreferences preferences = getSharedPreferences(LOGIN_PREFERENCE, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean(LOGIN_INSTANCE, false);
            editor.apply();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        loadingAnimation = findViewById(R.id.loadingAnimation);
        loadingAnimation.setVisibility(View.INVISIBLE);

        dbHandler = new DBHandler(ProductsViewActivity.this );

        waiting = findViewById( R.id.waits );

        rv = findViewById( R.id.product_recycler_view );
        layoutManager = new LinearLayoutManager(ProductsViewActivity.this );
        rv.setLayoutManager( layoutManager );

        // CHECK SQLITE IF IT HAS VALUE ALREADY. IF NOT, CALL API
        checkSQLite();

    }

    private void checkSQLite() {
        Cursor cursor = dbHandler.readAllProducts();
        cart.clear();

        if ( cursor.moveToFirst() && cursor.getCount() < 0 ){
            // SQLITE IS NOT NULL
        } else {
            // INSERT ELSE YG DI BAWAH
        }

        if ( !cart.isEmpty( ) ) {
            // KALAU SQLITE SDH ADA ISI NYA
            adapter = new ProductAdapter(ProductsViewActivity.this, cart);
        } else {
            // KALAU SQLITE MASIH KOSONG
            Bundle extras = getIntent().getExtras();
            code = extras.getString("code");

            productAPIRequest(code);
        }
    }

    private void productAPIRequest( String code ) {
        String url = "https://tmiapi-dev.mitraindogrosir.co.id/api/get_data_member";
        RequestQueue queue = Volley.newRequestQueue(ProductsViewActivity.this);

        loadingAnimation.setVisibility(View.VISIBLE);

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
                        JSONArray barcode;

                        Date currentTime = Calendar.getInstance().getTime();

                        for (int i = 0; i < memberData.length(); i++) {
                            try {
                                apple = memberData.getJSONObject(i);
                                barcode = apple.getJSONArray("barcode");

                                dbHandler.addProduct(
                                        apple.getInt("product_id"),
                                        apple.getString("product_name"),
                                        apple.getString("product_code"),
                                        apple.getString("price")
                                );

                                // Re-roll foreach barcode yang ada
                                for (int j = 0; j < barcode.length(); j++) {
                                    dbHandler.addBarcode(
                                            apple.getInt("product_id"),
                                            apple.getString("product_code"),
                                            barcode.getString(j),
                                            currentTime,
                                            currentTime
                                    );
                                }

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
                        waiting.setVisibility(View.INVISIBLE);
                        loadingAnimation.setVisibility(View.INVISIBLE);
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
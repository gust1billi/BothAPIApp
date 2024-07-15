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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bothapiapp.databasehandler.DBHandler;
import com.example.bothapiapp.recyclerview.Product;
import com.example.bothapiapp.recyclerview.ProductAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        } else if ( item.getTitle().equals( getString( R.string.size_of_db ) ) ){
            Cursor cursor = dbHandler.readAllProducts();
            Cursor bCursor = dbHandler.readAllBarcodes();
            cursor.moveToFirst();

            Log.e("Count Product", "" + cursor.getCount( ) );
            Log.e("Count Barcode", "" + bCursor.getCount( ) );
            Log.e("Count RV", "" + cart.size() );

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

    // Check if DB already has data. Take from API if not
    private void checkSQLite() {
        Cursor cursor = dbHandler.readAllProducts( );
        Cursor barcodeCursor;
        cart.clear();

//        dbHandler.onUpgrade( dbHandler.getReadableDatabase(), 0, 0 );

        Log.e("DB Products", "" + cursor.getCount( ) );
//        Log.e("DB Barcodes", "" + barcodeCursor.getCount( ) );

        // Dengan asumsi data tidak perlu di update setelah di simpan
        // Error statement dari stack overflow. Cursor.getCount > 0 aja nanti
        if ( cursor.getCount() > 0 ){
            // SQLITE IS NOT NULL
            List<String> barcodeList;
            Log.e("path", "yeet");
            loadingAnimation.setVisibility(View.VISIBLE);

            while ( cursor.moveToNext() ){
                barcodeList = new ArrayList<>();
                barcodeCursor = dbHandler.readProductBarcodes( cursor.getString(2) );

                while (barcodeCursor.moveToNext( ) ){
                    barcodeList.add( barcodeCursor.getString(0 ) );
                } // Log.e("Barcode by SELECT WHERE", barcodeList.toString( ) );
                cart.add( new Product(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        barcodeList
                ) );
            }

            adapter = new ProductAdapter(ProductsViewActivity.this, cart );
            waiting.setVisibility(View.INVISIBLE);
            loadingAnimation.setVisibility(View.INVISIBLE);
            rv.setAdapter(adapter);
        } else {
            Bundle extras = getIntent().getExtras();
            String code = extras.getString("code");

            productAPIRequest( code ); // To give data to the Adapter

//            dbAssignAPIRequest( code ); // 3rd try: Assign to DB, only runs for loop twice

//            ExecutorService executorService = Executors.newFixedThreadPool(256);
              // 1st Try & 2nd Try
//            executorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    dbAssignAPIRequest( code );
//                }
//            });
        }
    }

    private void productAPIRequest( String token ) {
        String url = "https://tmiapi-dev.mitraindogrosir.co.id/api/get_data_member";
        RequestQueue queue = Volley.newRequestQueue(ProductsViewActivity.this);

        loadingAnimation.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject memberList = new JSONObject(response);
                        JSONArray memberData = memberList
                                .getJSONObject("message")
                                .getJSONArray("data_product");

                        Log.e("Status Value", memberList.getString("status"));
                        // Proves that the API Responded
                        Log.e("Size", "products size is " + memberData.length());

                        String date = getDate();
                        List<String> barcodeList;

                        // 4th Try: Gets 475 iterations
                        // 8th Try:
                        ExecutorService service = Executors.newFixedThreadPool(256);
                        // 7th Try: AsyncTask, only makes the whole loop called once
//                        AsyncTask<String, Void, Void> asyncTask = new assignDBAsyncTask();

                        for (int i = 0; i < memberData.length(); i++) {
                            try {
                                JSONObject apple = memberData.getJSONObject(i);
                                JSONArray barcode = apple.getJSONArray("barcode");

                                int position = i+1;

                                service.execute( () -> {
                                    try {
                                        dbHandler.addProduct(
                                                apple.getString("product_name"),
                                                apple.getString("product_code"),
                                                apple.getString("price")
                                        );
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });

                                barcodeList = new ArrayList<>();

                                for (int j = 0 ; j < barcode.length() ; j++) {
                                    barcodeList.add( barcode.getString( j ) );
                                    int barcode_position = j;

                                    service.execute( () -> {
                                        try {
                                            dbHandler.addBarcode(
                                                    position,
                                                    apple.getString("product_code"),
                                                    barcode.getString(barcode_position),
                                                    date, date
                                            );
                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    });
                                }

                                cart.add(new Product(
                                        apple.getString("product_name"),
                                        apple.getString("product_code"),
                                        apple.getString("price"),
                                        barcodeList
                                ));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        adapter = new ProductAdapter(ProductsViewActivity.this, cart);
                        waiting.setVisibility(View.INVISIBLE);
                        loadingAnimation.setVisibility(View.INVISIBLE);
                        rv.setAdapter(adapter);

//                        Product post; // 5th Try: Kenapa hanya bisa bikin 475 dari 789???
//                        for (int i = 0; i < cart.size() ; i++) {
//                            post = cart.get(i);
//                            dbHandler.addProduct(
//                                    post.getProduct_id(),
//                                    post.getProduct_name(),
//                                    post.getProduct_code(),
//                                    post.getPrice()
//                            );
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Utils.showToast(ProductsViewActivity.this, "API Failed: " + error);
                    Log.e("Error POST VOLLEY", error.toString());
                }) {
                @Override
                public Map<String, String> getHeaders( ) {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token); return headers;
                }
            };

        // EXTENDS TIMEOUT TIMER. TO AVOID RTO ERROR
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000; // time in milliseconds
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000; // time in milliseconds
            }

            @Override
            public void retry(VolleyError error) { /* IGNORE */ }
        });

        queue.add(request);
    }

    // 2nd Try, do a new API call. Technically works; F

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
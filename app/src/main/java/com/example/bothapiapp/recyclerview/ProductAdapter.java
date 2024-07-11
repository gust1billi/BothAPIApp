package com.example.bothapiapp.recyclerview;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bothapiapp.R;
import com.example.bothapiapp.databasehandler.DBHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.productViewHolder> {
    Context ctx;

    List<Product> products;
    DBHandler dbHandler;

    public ProductAdapter(Context ctx, List<Product> products) {
        this.ctx = ctx;
        this.products = products;

        dbHandler = new DBHandler(ctx);
    }

    @NonNull
    @Override
    public productViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_recycler_view, parent, false);

        return new ProductAdapter.productViewHolder(view);
    }

    public static class productViewHolder extends RecyclerView.ViewHolder {
        // TEXT VIEW & IMAGE VIEW
        TextView name, code, price;

        public productViewHolder(@NonNull View itemView) {
            super(itemView);

            // ASSIGNING VIEWS TO R.ID
            name = itemView.findViewById(R.id.product_name_rv);
            code = itemView.findViewById(R.id.product_code_rv);
            price = itemView.findViewById(R.id.product_price_rv);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void onBindViewHolder(@NonNull productViewHolder holder, int position) {
        // MAIN LOGIC
        Product product = products.get(position);

        holder.name.setText( product.getProduct_name( ) );
        holder.code.setText( product.getProduct_code( ) );
        holder.price.setText( product.getPrice( ) );

        holder.itemView.setOnClickListener(view -> {
            Log.e("path", "boop");

            Cursor cursor = dbHandler.readProductBarcodes( holder.code.getText().toString() );
            while ( cursor.moveToNext() ) {
                Log.e( "Product Barcode", cursor.getString( 0 ) );
            }

        });
    }

    public void setDataShown(List<Product> basket){
        products = basket; notifyDataSetChanged();
    }
}

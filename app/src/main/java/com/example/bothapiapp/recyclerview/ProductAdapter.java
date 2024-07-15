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

    StringBuilder shownBarcodes = new StringBuilder();

    boolean barcodeClick = false;

    public ProductAdapter(Context ctx, List<Product> products) {
        this.ctx = ctx;
        this.products = products;

        dbHandler = new DBHandler(ctx);
    } // END OF CONSTRUCTOR

    @NonNull
    @Override
    public productViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_recycler_view, parent, false);

        return new ProductAdapter.productViewHolder(view);
    }

    public static class productViewHolder extends RecyclerView.ViewHolder {
        TextView name, code, price, barcodes;

        public productViewHolder(@NonNull View itemView) {
            super(itemView);

            // ASSIGNING VIEWS TO R.ID
            name = itemView.findViewById(R.id.product_name_rv);
            code = itemView.findViewById(R.id.product_code_rv);
            price = itemView.findViewById(R.id.product_price_rv);
            barcodes = itemView.findViewById(R.id.barcodes_shown);

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

        shownBarcodes = new StringBuilder("");
        List<String> barcodeList = product.getBarcodes();

        // PREPARES THE BARCODE FROM LIST TO SHOW
        for (int i = 0 ; i < product.getBarcodes().size() ; i++) {

            if (i != 0) shownBarcodes.append(", ");

            shownBarcodes.append( barcodeList.get(i) );
        } // Log.e("shown barcodes append", String.valueOf(shownBarcodes) );

        // PRE LOADS THE BARCODES AND MAKES THEM INVISIBLE
        holder.barcodes.setText( shownBarcodes );
        holder.barcodes.setVisibility(View.INVISIBLE);

        if ( barcodeClick ){
            holder.barcodes.setVisibility(View.VISIBLE);

            ExecutorService waitService = Executors.newSingleThreadExecutor();
            waitService.execute( () -> {
                try {
                    Thread.sleep(500);
                    barcodeClick = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        holder.itemView.setOnClickListener(view -> {
            barcodeClick = true;
            notifyItemChanged( position );

//            holder.barcodes.setVisibility(View.VISIBLE);
//            Log.e("product barcode", "" + product.getBarcodes() );
//            Cursor cursor = dbHandler.readProductBarcodes( product.getProduct_code( ) );

//            shownBarcodes = new StringBuilder();
//
//            while ( cursor.moveToNext() ) {
//                Log.e( "Product Barcode", cursor.getString( 0 ) );
//                this.shownBarcodes.append( cursor.getString(0 ) ).append(" ");
//            }
//
//            Log.e("Text Concat", shownBarcodes.toString() );


        });
    }

    public void setDataShown(List<Product> basket){
        products = basket; notifyDataSetChanged();
    }
}

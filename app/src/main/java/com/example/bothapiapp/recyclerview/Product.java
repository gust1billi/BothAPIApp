package com.example.bothapiapp.recyclerview;

import java.util.List;

public class Product {
    String product_name, product_code, price;

    List<String> barcodes;

    public Product(String product_name, String product_code, String price, List<String> barcodes) {
        this.product_name = product_name;
        this.product_code = product_code;
        this.price = price;
        this.barcodes = barcodes;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_code() {
        return product_code;
    }

    public String getPrice() {
        return price;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }

}

package com.example.bothapiapp.recyclerview;

public class Product {
    String product_name, product_code, price;

    public Product(String product_name, String product_code, String price) {
        this.product_name = product_name;
        this.product_code = product_code;
        this.price = price;
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

}

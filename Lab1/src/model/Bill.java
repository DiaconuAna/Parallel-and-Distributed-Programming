package model;

import java.util.HashMap;
import java.util.Map;

public class Bill {
    // Pairs of products and the sold quantities
    private final Map<Product, Integer> products;
    private int finalPrice;

    public Bill() {
        this.products = new HashMap<>();
        this.finalPrice = 0;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int newPrice) {
        this.finalPrice = newPrice;
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder("Bill:\n");

        for (Map.Entry<Product, Integer> billProd : products.entrySet()) {
            toReturn.append("\t").append(billProd.getKey().getName()).append(" -- ").append(billProd.getValue()).append("\n");
        }

        return toReturn.toString();
    }
}

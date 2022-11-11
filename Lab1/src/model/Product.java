package model;

import java.util.concurrent.locks.ReentrantLock;

public class Product {
    private final String name;
    private final int price;
    private int quantity;
    public final ReentrantLock productMutex = new ReentrantLock();

    public Product(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int q) {
        this.quantity = q;
    }

    @Override
    public String toString() {
        return name + ": " + price + " price units, " + quantity + " pieces";
    }
}

package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Deposit {
    private List<Product> products = new ArrayList<>();
    private int totalQuantity = 0;

    public Deposit(int numberOfProducts, List<Product> prods) {
        for (int i = 0; i < numberOfProducts; i++) {
            products.add(prods.get(i));
            this.totalQuantity += prods.get(i).getQuantity();
        }
    }

    public boolean sellProduct(String name, int quantity) {
        Product soldProduct = this.products.stream().filter(t -> Objects.equals(t.getName(), name)).collect(Collectors.toList()).get(0);
        //soldProduct.productMutex.lock();

        if (soldProduct.getQuantity() - quantity >= 0) {
            System.out.println(String.format("%s - %d - %d\n", soldProduct.getName(), soldProduct.getQuantity(), quantity ));
            soldProduct.setQuantity(soldProduct.getQuantity() - quantity);
            //soldProduct.productMutex.unlock();
            return true;
        }
        //soldProduct.productMutex.unlock();
        return false;
    }

    public List<Product> getProducts() {
        return this.products;
    }

    public int getTotalQuantity() {
        return this.totalQuantity;
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder("Deposit:\n");

        for (Product p: products) {
            toReturn.append("\t").append(p.getName()).append(" -- ").append(p.getQuantity()).append("\n");
        }

        return toReturn.toString();
    }
}

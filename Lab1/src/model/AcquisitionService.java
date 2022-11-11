package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class AcquisitionService implements Runnable {
    private final Deposit myDeposit;
    private int profit;
    private final List<Bill> bills;
    public static final ReentrantLock priceCheckerLock = new ReentrantLock();

    public AcquisitionService(Deposit d) {
        this.bills = new ArrayList<>();
        this.profit = 0;
        this.myDeposit = d;
    }

    // Two sales involving distinct products must be able to update their
    // quantities independently (without having to wait for the same mutex).
    @Override
    public void run() {
        priceCheckerLock.lock();

        Bill currentBill = new Bill();

        while (currentBill.getProducts().size() < 3) {
            int productIndex = 0;
            int productQuantity = 0;

            while (productQuantity == 0 || findProductInBill(currentBill, myDeposit.getProducts().get(productIndex))) {
                productIndex = (int) (Math.random() * myDeposit.getProducts().size());
                productQuantity = (int) (Math.random() * myDeposit.getProducts().get(productIndex).getQuantity());
            }

            Product soldProduct = myDeposit.getProducts().get(productIndex);

          soldProduct.productMutex.lock();
            if (myDeposit.sellProduct(soldProduct.getName(), productQuantity)) {
                currentBill.getProducts().put(soldProduct, productQuantity);
                currentBill.setFinalPrice(currentBill.getFinalPrice() + soldProduct.getPrice() * productQuantity);
            }
            soldProduct.productMutex.unlock();
        }

        System.out.println(currentBill);
        writeOutputFile(myDeposit);
        bills.add(currentBill);
        profit += currentBill.getFinalPrice();
        priceCheckerLock.unlock();
    }

    public int getProfit() {
        return this.profit;
    }

    public synchronized int getBillsProfit() {
        return this.bills.stream().map(Bill::getFinalPrice).reduce(0, Integer::sum);
    }

    public boolean findProductInBill(Bill b, Product p) {
        for (Map.Entry<Product, Integer> billProd : b.getProducts().entrySet()) {
            if (p == billProd.getKey())
                return true;
        }
        return false;
    }

    public synchronized int getSoldQuantity() {
        int soldQuantity = 0;
        for (Bill b : bills) {
            for (Map.Entry<Product, Integer> billProd : b.getProducts().entrySet()) {
                soldQuantity += billProd.getValue();
            }
        }
        return soldQuantity;
    }

    public synchronized int getRemainingQuantity() {
        return myDeposit.getProducts().stream().map(Product::getQuantity).reduce(0, Integer::sum);
    }

    public int getTotalQuantity() {
        return myDeposit.getTotalQuantity();
    }

    private static void writeOutputFile(Deposit myDeposit) {
        try {
            FileWriter myWriter = new FileWriter("deposit.txt", true);
            myWriter.write(String.valueOf(myDeposit));
            myWriter.close();
            //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}

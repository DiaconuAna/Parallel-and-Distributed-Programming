import model.AcquisitionService;
import model.Deposit;
import model.Product;
import model.ProductChecker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class Main {
    private static final int PRODUCT_COUNT = 1000;
    private static final int THREAD_COUNT = 400;

    public static void main(String[] args) {
        createOutputFile();
        List<Product> myProducts = generateProducts();

        Deposit myDeposit = new Deposit(generateProducts().size(), myProducts);
        writeOutputFile(myDeposit);
        AcquisitionService myAcquisition = new AcquisitionService(myDeposit);

        ProductChecker productChecker = new ProductChecker(myAcquisition);
        Timer timer = new Timer();
        timer.schedule(productChecker, 1, 1);

        List<Thread> myThreads = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            myThreads.add(new Thread(myAcquisition));
        }

        float start = System.nanoTime() / 1000000;

        for (Thread t : myThreads) {
            t.start();
        }

        for (Thread thread : myThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        timer.cancel();

        productChecker.run();

        float end = System.nanoTime() / 1000000;
        System.out.printf("\n%f seconds elapsed%n", (end - start) / 1000);
    }

    private static List<Product> generateProducts() {
        List<Product> myProducts = new ArrayList<>();

        for (int i = 0; i < PRODUCT_COUNT; i++) {
            int productQuantity = (int) (Math.random() * 100);
            int productPrice = (int) (Math.random() * 50);
            String productName = "Product" + i;
            myProducts.add(new Product(productName, productPrice, productQuantity));
        }

        return myProducts;
    }

    private static void createOutputFile() {
        try {
            File myObj = new File("deposit.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void writeOutputFile(Deposit myDeposit) {
        try {
            FileWriter myWriter = new FileWriter("deposit.txt");
            myWriter.write(String.valueOf(myDeposit));
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

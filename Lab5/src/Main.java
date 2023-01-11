import domain.Operation;
import domain.Polynomial;

import java.util.concurrent.ExecutionException;

public class Main {

    private static Polynomial regularSequential(Polynomial a, Polynomial b) {
        long startTime = System.currentTimeMillis();
        Polynomial result1 = Operation.sequentialRegularMultiplication(a, b);
        long endTime = System.currentTimeMillis();
        System.out.println("Simple sequential multiplication");
        System.out.println("time: " + (endTime - startTime) + " ms");
        return result1;
    }

    private static Polynomial regularThreaded(Polynomial a, Polynomial b) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Polynomial result2 = Operation.threadedRegularMultiplication(a, b);
        long endTime = System.currentTimeMillis();
        System.out.println("Simple parallel multiplication");
        System.out.println("time: " + (endTime - startTime) + " ms");
        return result2;
    }

    private static Polynomial karatsubaSequential(Polynomial a, Polynomial b) {
        long startTime = System.currentTimeMillis();
        Polynomial result3 = Operation.karatsubaSequential(a, b);
        long endTime = System.currentTimeMillis();
        System.out.println("Karatsuba sequential multiplication");
        System.out.println("time: " + (endTime - startTime) + " ms");
        return result3;
    }

    private static Polynomial karatsubaThreaded(Polynomial a, Polynomial b) throws ExecutionException,
            InterruptedException {
        long startTime = System.currentTimeMillis();
        Polynomial result4 = Operation.karatsubaThreaded(a, b, 1);
        long endTime = System.currentTimeMillis();
        System.out.println("Karatsuba parallel multiplication: time: " + (endTime - startTime) + " ms");
//        System.out.println("time: " + (endTime - startTime) + " ms");
        return result4;
    }


    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Polynomial a = new Polynomial(10000);
        Polynomial b = new Polynomial(10000);

        System.out.println("First:" + a);
        System.out.println("Second:" + b);
        System.out.println("\n");

//        System.out.println(regularSequential(a, b));
//        System.out.println(regularThreaded(a, b));
//        System.out.println(karatsubaSequential(a, b));
//        System.out.println(karatsubaThreaded(a, b));
//
        regularSequential(a, b);
        regularThreaded(a, b);
        karatsubaSequential(a, b);
        karatsubaThreaded(a, b);

    }

}

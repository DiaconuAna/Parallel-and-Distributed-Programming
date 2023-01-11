package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Operation {
    public static int thread_count = 10;
    public static int max_depth = 4;

    public static Polynomial sequentialRegularMultiplication(Polynomial a, Polynomial b){
        int resultSize = a.polynomialDegree + b.polynomialDegree + 1;
        List<Integer> coefficients = new ArrayList<>();

        for(int i=0;i<resultSize;i++){
            coefficients.add(0);
        }

        int coeffA = a.getCoefficientCount();
        int coeffB = b.getCoefficientCount();

        /*
         * (9x^0 + 9x^1 + 3x^2) * (2x^0 + 2x^1 + 5x^2 + 3x^3)
         *
         * 9x^0*2x^0 + 9x^0*2x^1 + 9x^0*5x^2 + 9x^0*3x^3 + 9x^1*2x^0 + 9x^1*2x^1 + 9x^1*5x^2 + 9x^1*3x^3 + 3x^2*2x^0 + 3x^2*2x^1 + 3x^2*5x^2 + 3x^2*3x^3
         * 0    1      2        3     1      2        3      4       2     3       4        5
         * 18 + 18x + 45x^2 + 27x^3 + 18x + 18x^2 + 45x^3 + 27x^4 + 6x^2 + 6x^3 + 15x^4 + 9x^5
         *
         * Result:  18x^0 + 36x^1 + 69x^2 + 78x^3 + 42x^4 + 9x^5
         */

        for(int i=0;i<coeffA;i++){
            for(int j=0;j<coeffB;j++){
                int index = i + j;
                int value = a.polynomialCoefficients.get(i) * b.polynomialCoefficients.get(j);
                coefficients.set(index, coefficients.get(index) + value);
            }
        }
        return new Polynomial(coefficients);
    }

    public static Polynomial threadedRegularMultiplication(Polynomial a, Polynomial b) throws InterruptedException {
        int resultSize = a.polynomialDegree + b.polynomialDegree + 1;
        List<Integer> coefficients = new ArrayList<>();

        for(int i=0;i<resultSize;i++){
            coefficients.add(0);
        }

        Polynomial c = new Polynomial(coefficients);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(thread_count);
        int stepSize = c.getCoefficientCount()/thread_count; // number of computations per thread
        if(stepSize == 0){
            stepSize = 1;
        }

        for(int i=0;i<c.getCoefficientCount(); i = i + stepSize){
            int aux = i + stepSize;
            Task task = new Task(i, aux, a, b, c);
            executor.execute(task);
        }

        executor.shutdown();
        executor.awaitTermination(50, TimeUnit.SECONDS);

        return c;
    }

    public static Polynomial karatsubaSequential(Polynomial a, Polynomial b){
        if(a.polynomialDegree < 2  || b.polynomialDegree < 2){
            return sequentialRegularMultiplication(a, b);
        }

        int len = Math.max(a.polynomialDegree, b.polynomialDegree) / 2; // n/2
        Polynomial low1 = new Polynomial(a.polynomialCoefficients.subList(0, len));
        Polynomial high1 = new Polynomial(a.polynomialCoefficients.subList(len, a.getCoefficientCount()));
        Polynomial low2 = new Polynomial(b.polynomialCoefficients.subList(0, len));
        Polynomial high2 = new Polynomial(b.polynomialCoefficients.subList(len, b.getCoefficientCount()));

        // D1 -  high1   ; D0 - low1
        // E1 -  high2    ; E0 - low2
        // ( (E1*D1) * (x^n) + ((E1 + E0) * (D0 + D1) - (D1*E1) - (D0*E0)) * (x ^ n/2) + E0*D0)
        Polynomial z1 = karatsubaSequential(low1, low2); // E0*D0
        Polynomial z2 = karatsubaSequential(Polynomial.add(low1, high1), Polynomial.add(low2, high2)); // (E1+E0)*(D0+D1)
        Polynomial z3 = karatsubaSequential(high1, high2); // E1*D1

        Polynomial r1 = Polynomial.addZeros(z3, 2*len); // (E1*D1)*(x^n)
        Polynomial r2 = Polynomial.addZeros(Polynomial.subtract(Polynomial.subtract(z2, z3),z1), len); // ((E1 + E0) * (D0 + D1) - (D1*E1) - (D0*E0)) * (x ^ n/2)

        return Polynomial.add(Polynomial.add(r1, r2), z1);
    }

    public static Polynomial karatsubaThreaded(Polynomial a, Polynomial b, int currentDepth) throws ExecutionException, InterruptedException {
        if(currentDepth > max_depth){
            return karatsubaSequential(a, b);
        }

        if(a.polynomialDegree < 2  || b.polynomialDegree < 2){
            return sequentialRegularMultiplication(a, b);
        }

        int len = Math.max(a.polynomialDegree, b.polynomialDegree) / 2;
        Polynomial low1 = new Polynomial(a.polynomialCoefficients.subList(0, len));
        Polynomial high1 = new Polynomial(a.polynomialCoefficients.subList(len, a.getCoefficientCount()));
        Polynomial low2 = new Polynomial(b.polynomialCoefficients.subList(0, len));
        Polynomial high2 = new Polynomial(b.polynomialCoefficients.subList(len, b.getCoefficientCount()));

        ExecutorService executor = Executors.newFixedThreadPool(thread_count);
        Future<Polynomial> f1 = executor.submit(() -> karatsubaThreaded(low1, low2, currentDepth + 1));
        Future<Polynomial> f2 = executor.submit(() -> karatsubaThreaded(Polynomial.add(low1, high1), Polynomial.add(low2, high2), currentDepth + 1));
        Future<Polynomial> f3 = executor.submit(() -> karatsubaThreaded(high1, high2, currentDepth + 1));

        executor.shutdown();

        Polynomial z1 = f1.get();
        Polynomial z2 = f2.get();
        Polynomial z3 = f3.get();

        executor.awaitTermination(60, TimeUnit.SECONDS);

        Polynomial r1 = Polynomial.addZeros(z3, 2 * len);
        Polynomial r2 = Polynomial.addZeros(Polynomial.subtract(Polynomial.subtract(z2, z3), z1), len);
        return Polynomial.add(Polynomial.add(r1, r2), z1);

    }
}

package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Polynomial {
    int polynomialDegree;
    List<Integer> polynomialCoefficients;

    public Polynomial(int degree) {
        this.polynomialDegree = degree;
        // randomly generate the coefficients
        polynomialCoefficients = new ArrayList<>(degree + 1);
//        Random seed = new Random();
        for (int i = 0; i < degree; i++) {
//            polynomialCoefficients.add(seed.nextInt(10));
            polynomialCoefficients.add(1);
        }
        polynomialCoefficients.add( 1);
    }

    public Polynomial(List<Integer> polynomialCoefficients) {
        this.polynomialCoefficients = polynomialCoefficients;
        this.polynomialDegree = polynomialCoefficients.size() - 1;
    }

    public int getCoefficientCount() {
        return this.polynomialCoefficients.size();
    }

    public static Polynomial addZeros(Polynomial polynomial, int offset) {
//        List<Integer> coeff = new ArrayList<>();
//        for (int i = 0; i < offset; i++) {
//            coeff.add(0);
//        }
        List<Integer> coeff= IntStream.range(0, offset).mapToObj(i -> 0).collect(Collectors.toList());
        coeff.addAll(polynomial.polynomialCoefficients);
        return new Polynomial(coeff);
    }

    public static Polynomial add(Polynomial a, Polynomial b) {
        int minDegree = Math.min(a.polynomialDegree, b.polynomialDegree);
        int maxDegree = Math.max(a.polynomialDegree, b.polynomialDegree);
        Polynomial bigPolynomial;
        List<Integer> coefficients = new ArrayList<>(maxDegree + 1);

        if (a.polynomialDegree > b.polynomialDegree) {
            bigPolynomial = a;
        } else {
            bigPolynomial = b;
        }

        for (int i = 0; i <= minDegree; i++) {
            coefficients.add(a.polynomialCoefficients.get(i) + b.polynomialCoefficients.get(i));
        }

        if (minDegree != maxDegree) {
            for (int i = minDegree + 1; i <= maxDegree; i++) {
                coefficients.add(bigPolynomial.polynomialCoefficients.get(i));
            }
        }

        return new Polynomial(coefficients);
    }


    public static Polynomial subtract(Polynomial a, Polynomial b) {
        int minDegree = Math.min(a.polynomialDegree, b.polynomialDegree);
        int maxDegree = Math.max(a.polynomialDegree, b.polynomialDegree);
        Polynomial bigPolynomial;
        List<Integer> coefficients = new ArrayList<>(maxDegree + 1);

        if (a.polynomialDegree > b.polynomialDegree) {
            bigPolynomial = a;
        } else {
            bigPolynomial = b;
        }

        for (int i = 0; i <= minDegree; i++) {
            coefficients.add(a.polynomialCoefficients.get(i) - b.polynomialCoefficients.get(i));
        }

        if (minDegree != maxDegree) {
            for (int i = minDegree + 1; i <= maxDegree; i++) {
                coefficients.add(bigPolynomial.polynomialCoefficients.get(i));
            }
        }

        // remove consecutive zero coefficients starting from the biggest power
        int i = coefficients.size() - 1; // start from biggest power

        while (coefficients.get(i) == 0 && i > 0) {
            coefficients.remove(i);
            i--;
        }

        return new Polynomial(coefficients);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int power = 0;
        for (int i = 0; i <= this.polynomialDegree; i++) {
            if (polynomialCoefficients.get(i) == 0) {
                power++;
                continue;
            }
            str.append(" ").append(polynomialCoefficients.get(i)).append("x^").append(power).append(" +");
            power++;
        }
        str.deleteCharAt(str.length() - 1); //delete last +
        return str.toString();
    }
}

import java.util.ArrayList;
import java.util.List;

public class Operation {

    public static Polynomial sequentialRegularMultiplication(Object p, Object q, int begin, int end) {
        Polynomial p1 = (Polynomial) p;
        Polynomial p2 = (Polynomial) q;
        Polynomial result = Polynomial.buildEmptyPolynomial(p1.polynomialDegree * 2 + 1);

        for (int i = begin; i < end; i++) {
            for (int j = 0; j < p2.polynomialCoefficients.size(); j++) {
                int res = p1.polynomialCoefficients.get(i) * p2.polynomialCoefficients.get(j);
                result.polynomialCoefficients.set(i + j, result.polynomialCoefficients.get(i + j) + res);
            }
        }
        return result;
    }

    public static Polynomial sequentialRegularMultiplication(Polynomial a, Polynomial b){
        int resultSize = a.polynomialDegree + b.polynomialDegree + 1;
        List<Integer> coefficients = new ArrayList<>();

        for(int i=0;i<resultSize;i++){
            coefficients.add(0);
        }

        int coeffA = a.getCoefficientCount();
        int coeffB = b.getCoefficientCount();

        for(int i=0;i<coeffA;i++){
            for(int j=0;j<coeffB;j++){
                int index = i + j;
                int value = a.polynomialCoefficients.get(i) * b.polynomialCoefficients.get(j);
                coefficients.set(index, coefficients.get(index) + value);
            }
        }
        return new Polynomial(coefficients);
    }

    public static Polynomial karatsubaMultiplication(Polynomial a, Polynomial b){
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
        Polynomial z1 = karatsubaMultiplication(low1, low2); // E0*D0
        Polynomial z2 = karatsubaMultiplication(Polynomial.add(low1, high1), Polynomial.add(low2, high2)); // (E1+E0)*(D0+D1)
        Polynomial z3 = karatsubaMultiplication(high1, high2); // E1*D1

        Polynomial r1 = Polynomial.addZeros(z3, 2*len); // (E1*D1)*(x^n)
        Polynomial r2 = Polynomial.addZeros(Polynomial.subtract(Polynomial.subtract(z2, z3),z1), len); // ((E1 + E0) * (D0 + D1) - (D1*E1) - (D0*E0)) * (x ^ n/2)

        return Polynomial.add(Polynomial.add(r1, r2), z1);
    }

    public static Polynomial buildResult(ArrayList<Polynomial> polynomials) {
        int degree = polynomials.get(0).polynomialDegree;
        Polynomial result = Polynomial.buildEmptyPolynomial(degree + 1); // expected result

        for (int i = 0; i < result.polynomialCoefficients.size(); i++) {
            for (Object polynomial : polynomials) {
                result.polynomialCoefficients.set(i, result.polynomialCoefficients.get(i) + ((Polynomial) polynomial).polynomialCoefficients.get(i));
            }
        }

        return result;
    }

}

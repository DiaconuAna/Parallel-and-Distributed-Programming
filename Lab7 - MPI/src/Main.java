import mpi.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final int ORDER = 100;
    private static final Boolean KARATSUBA = true;

    private static List<Integer> createCoefficientList(Object[] objCoefficientsP, int offset) {
        String stringCoefficientsP = (String) objCoefficientsP[offset];
        ArrayList<String> stringsListCoefficientsB = new ArrayList<String>(Arrays.asList(stringCoefficientsP.split(",")));
        List<Integer> coefficientsP = new ArrayList<Integer>();
        for (String fav : stringsListCoefficientsB) {
            coefficientsP.add(Integer.parseInt(fav.trim()));
        }
        return coefficientsP;
    }


    private static void MultiplicationMaster(Polynomial p1, Polynomial p2, int processNumber) {
        long startTime = System.currentTimeMillis();
        int start, finish = 0;
        int len = p1.getCoefficientCount() / (processNumber - 1);

        for (int i = 1; i < processNumber; i++) { // skip the starting process
            // distribute the computations across several nodes by computing the start and finish index of the coefficients
            // a node will be responsible for
            start = finish;
            finish += len;

            if (i == processNumber - 1) {
                finish = p1.getCoefficientCount();
            }

            // send data to the current process (i) - P2P communication
            // tag value used to select between several incoming messages - the call will
            // wait until a message sent with the matching tag value arrives
            // In the actual arguments passed to these methods, buf must be an array or a run-time exception will occur
            // offset - element in the buf array where message start. Offset = 0 => message starts at the beginning of the array

            MPI.COMM_WORLD.Send(new Object[]{p1.polynomialCoefficients.toString().substring(1, p1.polynomialCoefficients.toString().length() - 1)}, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(new Object[]{p2.polynomialCoefficients.toString().substring(1, p2.polynomialCoefficients.toString().length() - 1)}, 0, 1, MPI.OBJECT, i, 0);

//            MPI.COMM_WORLD.Send(new Object[]{p1}, 0, 1, MPI.OBJECT, i, 0);
//            MPI.COMM_WORLD.Send(new Object[]{p2}, 0, 1, MPI.OBJECT, i, 0);

            MPI.COMM_WORLD.Send(new int[]{start}, 0, 1, MPI.INT, i, 0);
            MPI.COMM_WORLD.Send(new int[]{finish}, 0, 1, MPI.INT, i, 0);
        }

        Object[] objResults = new Object[processNumber - 1];

        for (int i = 1; i < processNumber; i++) {
            // receive the data computed
            MPI.COMM_WORLD.Recv(objResults, i - 1, 1, MPI.OBJECT, i, 0);
        }

        ArrayList<Polynomial> results = new ArrayList<>();

        for (int i = 0; i < processNumber - 1; i++) {
            results.add(new Polynomial(createCoefficientList(objResults, i)));
        }

        Polynomial result = Operation.buildResult(results);
        long endTime = System.currentTimeMillis();

        System.out.println("result:\n" + result.toString());
        System.out.println("time: " + (endTime - startTime) + " ms");
    }

    private static void SequentialWorker() {
        System.out.println("Sequential worker started");

        Object[] p1Coeff = new Object[2];
        Object[] p2Coeff = new Object[2];
        int[] begin = new int[1];
        int[] end = new int[1];

        // receive the data
        MPI.COMM_WORLD.Recv(p1Coeff, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(p2Coeff, 0, 1, MPI.OBJECT, 0, 0);

        MPI.COMM_WORLD.Recv(begin, 0, 1, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Recv(end, 0, 1, MPI.INT, 0, 0);

        Polynomial p1 = new Polynomial(createCoefficientList(p1Coeff, 0));
        Polynomial p2 = new Polynomial(createCoefficientList(p2Coeff, 0));

        Polynomial result = Operation.sequentialRegularMultiplication(p1, p2, begin[0], end[0]);

        // send the data
        MPI.COMM_WORLD.Send(new Object[]{result.polynomialCoefficients.toString().substring(1, result.polynomialCoefficients.toString().length() - 1)}, 0, 1, MPI.OBJECT, 0, 0);
    }

    private static void KaratsubaWorker() {
        System.out.println("Sequential worker started");

        Object[] p1Coeff = new Object[2];
        Object[] p2Coeff = new Object[2];
        int[] begin = new int[1];
        int[] end = new int[1];

        // receive the data
        MPI.COMM_WORLD.Recv(p1Coeff, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(p2Coeff, 0, 1, MPI.OBJECT, 0, 0);

        MPI.COMM_WORLD.Recv(begin, 0, 1, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Recv(end, 0, 1, MPI.INT, 0, 0);

        Polynomial p1 = new Polynomial(createCoefficientList(p1Coeff, 0));
        Polynomial p2 = new Polynomial(createCoefficientList(p2Coeff, 0));

        // for karatsuba, mark the coefficients that are not in the range of the node with 0
        // to make sure they are not used
        for (int i = 0; i < begin[0]; i++) {
            p1.polynomialCoefficients.set(i, 0);
        }
        for (int j = end[0]; j < p1.polynomialCoefficients.size(); j++) {
            p1.polynomialCoefficients.set(j, 0);
        }

        Polynomial result = Operation.karatsubaMultiplication(p1, p2);

        // send the data
        MPI.COMM_WORLD.Send(new Object[]{result.polynomialCoefficients.toString().substring(1, result.polynomialCoefficients.toString().length() - 1)}, 0, 1, MPI.OBJECT, 0, 0);

    }

    public static void main(String[] args) {

        MPI.Init(args);

        int me = MPI.COMM_WORLD.Rank(); // index of the local process
        int size = MPI.COMM_WORLD.Size(); // total number of processes
        System.out.println("Hi from < " + me + " > with size: " + size);

        if (me == 0) { // the first process
            Polynomial p1 = new Polynomial(ORDER);
            System.out.println("First polynomial >> " + p1);

            Polynomial p2 = new Polynomial(ORDER);
            System.out.println("Second polynomial >> " + p2);

            MultiplicationMaster(p1, p2, size);
        } else {
            if (KARATSUBA) {
                KaratsubaWorker();
            } else {
                SequentialWorker();
            }
        }

        MPI.Finalize();
    }
}
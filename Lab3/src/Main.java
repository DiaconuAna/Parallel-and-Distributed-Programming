import domain.Matrix;
import threads.ColumnThread;
import threads.KThread;
import threads.MatrixUtils;
import threads.RowThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {
    private static int rowCountMatrixA = 1000;
    private static int columnCountMatrixA = 900;
    private static int rowCountMatrixB = 800;
    private static int columnCountMatrixB = 700;
    private static int threadCount = 5;

    /****************************************************
     * ROW : Each task computes consecutive elements, going row after row.
     ****************************************************/

    public static RowThread initializeRow(int index, Matrix a, Matrix b, Matrix c, int threadCount) {
        int finalMatrixSize = c.rowCount * c.columnCount;
        int elementCount = finalMatrixSize / threadCount; // number of elements each thread deals with

        int startRow = elementCount * index / c.columnCount;
        int startColumn = elementCount * index % c.columnCount;

        if (index == threadCount - 1) {
            elementCount += finalMatrixSize % threadCount;
            // the last thread takes the remaining elements that did not fit in previous operations
        }

        return new RowThread(startRow, startColumn, elementCount, a, b, c);
    }

    public static void rowMultiplication(Matrix a, Matrix b, Matrix c) {
        float start = System.nanoTime();

        runRowThreadPerTask(a, b, c);
        runRowThreadPool(a, b, c);

        System.out.println("Time elapsed: " + (System.nanoTime() - start) / 1_000_000_000.0 + " seconds");
    }

    public static void runRowThreadPerTask(Matrix a, Matrix b, Matrix c) {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            threads.add(initializeRow(i, a, b, c, threadCount));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Row:\n" + c);
    }

    public static void runRowThreadPool(Matrix a, Matrix b, Matrix c) {
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            service.submit(initializeRow(i, a, b, c, threadCount));
        }

        service.shutdown();
        try {
            if (!service.awaitTermination(300, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
            System.out.println("Row:\n" + c.toString());
        } catch (InterruptedException ex) {
            service.shutdownNow();
            ex.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    /*************************************************
     * COLUMN: Each task computes consecutive elements, going column after column.
     */

    public static ColumnThread initializeColumn(int index, Matrix a, Matrix b, Matrix c, int threadCount) {
        int finalMatrixSize = c.rowCount * c.columnCount;
        int elementCount = finalMatrixSize / threadCount;

        // inverse of rows initializer
        int startRow = elementCount * index % c.rowCount;
        int startColumn = elementCount * index / c.rowCount;

        if (index == threadCount - 1) {
            elementCount += finalMatrixSize % threadCount;
        }

        return new ColumnThread(startRow, startColumn, elementCount, a, b, c);
    }

    public static void columnMultiplication(Matrix a, Matrix b, Matrix c) {
        float start = System.nanoTime();

        runColumnThreadPerTask(a, b, c);
        runColumnThreadPool(a, b, c);

        System.out.println("Time elapsed: " + (System.nanoTime() - start) / 1_000_000_000.0 + " seconds");
    }

    public static void runColumnThreadPerTask(Matrix a, Matrix b, Matrix c) {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            threads.add(initializeColumn(i, a, b, c, threadCount));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Column: \n" + c);
    }

    public static void runColumnThreadPool(Matrix a, Matrix b, Matrix c) {
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            service.submit(initializeColumn(i, a, b, c, threadCount));
        }

        service.shutdown();
        try {
            if (!service.awaitTermination(300, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
            System.out.println("Column :\n" + c.toString());
        } catch (InterruptedException ex) {
            service.shutdownNow();
            ex.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    /****************************************************
     * K tasks: Each task takes every k-th element (where k is the number of tasks), going row by row
     ****************************************************/

    public static KThread initializeKThread(int index, Matrix a, Matrix b, Matrix c, int threadCount) {
        int finalMatrixSize = c.rowCount * c.columnCount;
        int elementCount = finalMatrixSize / threadCount;

        if (index < finalMatrixSize % threadCount) {
            elementCount++;
        }

        int startRow = index / c.columnCount;
        int startColumn = index % c.columnCount;

        return new KThread(startRow, startColumn, elementCount, threadCount, a, b, c);
    }

    public static void kTask(Matrix a, Matrix b, Matrix c) {
        float start = System.nanoTime();

//        runKhreadPerTask(a, b, c);
        runKThreadPool(a, b, c);

        System.out.println("Time elapsed: " + (System.nanoTime() - start) / 1_000_000_000.0 + " seconds");
    }

    public static void runKhreadPerTask(Matrix a, Matrix b, Matrix c) {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            threads.add(initializeKThread(i, a, b, c, threadCount));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("K-th element: \n" + c);
    }

    public static void runKThreadPool(Matrix a, Matrix b, Matrix c) {
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            service.submit(initializeKThread(i, a, b, c, threadCount));
        }

        service.shutdown();
        try {
            if (!service.awaitTermination(300, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
            System.out.println("Thread Pool K-th element:\n" + c.toString());
        } catch (InterruptedException ex) {
            service.shutdownNow();
            ex.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    /***********************************
     * MAIN
     ***********************************/
    public static void main(String[] args) {
        Matrix a = new Matrix(rowCountMatrixA, columnCountMatrixA, 1);
        Matrix b = new Matrix(rowCountMatrixB, columnCountMatrixB, 0);

        System.out.println(a);
        System.out.println(b);

        if (columnCountMatrixA == rowCountMatrixB) {

            Matrix c = new Matrix(rowCountMatrixA, columnCountMatrixB, 1);

//            rowMultiplication(a, b, c);
//            columnMultiplication(a, b, c);
            kTask(a, b, c);

        } else {
            System.err.println("Impossible to multiply matrices");
        }


    }
}

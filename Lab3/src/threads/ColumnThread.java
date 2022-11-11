package threads;

import domain.Matrix;
import domain.Pair;

import java.util.ArrayList;
import java.util.List;

public class ColumnThread extends Thread {

    public Matrix a, b, c;
    public int startRow, startColumn;
    public int elementCount;
    public List<Pair<Integer, Integer>> pairs;

    public ColumnThread(int startRow, int startColumn, int elementCount, Matrix a, Matrix b, Matrix c) {
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.elementCount = elementCount;
        this.a = a;
        this.b = b;
        this.c = c;
        this.pairs = new ArrayList<>();
        computeElements();
    }

    public void computeElements() {
        int i = startRow;
        int j = startColumn;
        int size = elementCount;

        while (i < c.rowCount && j < c.columnCount && size > 0) {
            pairs.add(new Pair<>(i, j)); // add new index pair
            i++;
            size--;

            if (c.columnCount == i) {
                i = 0;
                j++;
            }
        }
    }

    @Override
    public void run() {
        for (Pair<Integer, Integer> p : pairs) {
            int row = p.first;
            int column = p.second;
            try {
                c.setElement(row, column, MatrixUtils.computeElement(a, b, row, column));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

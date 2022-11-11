package domain;

import java.util.Random;
import java.util.stream.IntStream;

public class Matrix {
    public final int rowCount, columnCount;
    public int[][] elements;
    public int flag;

    public Matrix(int rc, int cc, int f) {
        this.rowCount = rc;
        this.columnCount = cc;
        this.flag = f;
        this.elements = new int[rowCount][columnCount];
        generateMatrix(flag);
    }

    /**
     * @param flag: if flag = 1 => populate matrix with 1
     */
    private void generateMatrix(int flag) {
        if(flag == 1){
            for(int i=0;i<rowCount;i++){
                for(int j=0;j<columnCount;j++)
                    elements[i][j] = 1;
            }
        }
        else{
            Random generator = new Random();
            for(int i=0;i<rowCount;i++){
                for(int j=0;j<columnCount;j++)
                    elements[i][j] = generator.nextInt(100)+1;
            }
        }
    }

    public int getElement(int row, int column) {
        return elements[row][column];
    }

    public void setElement(int row, int column, int value) {
        elements[row][column] = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, rowCount).forEach(i -> {
            IntStream.range(0, columnCount).forEach(j -> sb.append(elements[i][j]).append(" "));
            sb.append("\n");
        });
        return sb.toString();
    }

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import static java.lang.Math.sqrt;

public class HMM0 {
    public static double[] multiply(double[][] matrix, double[] vector) {
        int rows = matrix[0].length;
        int columns = matrix.length;

        double[] result = new double[rows];

        for (int row = 0; row < rows; row++) {
            double sum = 0;
            for (int column = 0; column < columns; column++) {
                sum += matrix[column][row]
                        * vector[column];
            }
            result[row] = sum;
        }
        return result;
    }

    // Stackoverflow
    public static double[][] multiplicar(double[][] A, double[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] C = new double[aRows][bColumns];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                C[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }

    public static double[][] multiplyByMatrix(double[][] m1, double[][] m2) {
        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if(m1ColLength != m2RowLength) return null; // matrix multiplication is not possible
        int mRRowLength = m1.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }

    public static double[][] fillMatrix(String[] stringList, double[][] matrix) {
        // Start at 2 since two first items in input rows are matrix sizes.
        int counter = 2;
        for (int i = 0; i < Integer.parseInt(stringList[0]); i++) {
            for (int j = 0; j < Integer.parseInt(stringList[1]); j++) {
                matrix[i][j] = Double.parseDouble(stringList[counter]);
                counter++;
            }
        }
        return matrix;
    }

    public static void main(String[] args) throws IOException{
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        //BufferedReader stdin = new BufferedReader(new FileReader("C:\\Users\\David\\IdeaProjects\\Labb2_AK\\src\\testfile.txt"));

        // First row is the A-matrix, second is the B-matrix and the third is pi
        String lineString;
        String[] stringList;
        int numStates = 0;
        int numObs;
        double[][] A;
        double[][] B;
        double[][] piVector;

        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        numStates = Integer.parseInt(stringList[0]);
        A = fillMatrix(stringList, new double[numStates][numStates]);

        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        numObs = Integer.parseInt(stringList[1]);
        B = fillMatrix(stringList, new double[numStates][numObs]);
        
        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        piVector = fillMatrix(stringList, new double[1][numStates]);
        /*int counter = 2;
        for (int i = 0; i < numStates; i++) {
            piVector[i] = Double.parseDouble(stringList[counter]);
            counter++;   b
        }*/

        double[][] piVectorTimesA = multiplyByMatrix(piVector, A);
        double[][] probDist = multiplyByMatrix(piVectorTimesA,B);
        StringBuilder sb = new StringBuilder();
        sb.append("1 " + probDist[0].length);
        for (double item: probDist[0]) {
              sb.append(" " + item);
        }


        System.out.println(sb.toString());
    }
}



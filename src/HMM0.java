import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import static java.lang.Math.sqrt;

public class HMM0 {
    public static double[] multiply(double[][] matrix, double[] vector) {
        int rows = matrix.length;
        int columns = matrix[0].length;

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
        double[] piVector;


        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        numStates = Integer.parseInt(stringList[0]);
        A = fillMatrix(stringList, new double[numStates][numStates]);

        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        numObs = Integer.parseInt(stringList[0]);
        B = fillMatrix(stringList, new double[numStates][numObs]);

        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        piVector = new double[numStates];
        int counter = 2;
        for (int i = 0; i < numStates; i++) {
            piVector[i] = Double.parseDouble(stringList[counter]);
            counter++;
        }

        double[] probDist = multiply(B, multiply(A, piVector));
        System.out.println(Arrays.toString(B[3]));
        System.out.println(Arrays.toString(probDist));
    }
}



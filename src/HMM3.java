import sun.font.TrueTypeFont;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HMM3 {
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
        A = MyFunctions2.fillMatrix(stringList, new double[numStates][numStates]);

        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        numObs = Integer.parseInt(stringList[1]);
        B = MyFunctions2.fillMatrix(stringList, new double[numStates][numObs]);

        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        piVector = MyFunctions2.fillMatrix(stringList, new double[1][numStates]);

        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        int iters = 0;
        int maxIters = 500;
        double oldLogProb = -10000000;

        int T = Integer.parseInt(stringList[0]);
        int[] obsList = new int[T];
        int counter = -1;
        for (String obs : stringList) {
            if (counter != -1) {
                obsList[counter] = Integer.parseInt(obs);
            }
            counter++;
        }

        Object[] objectList;
        objectList = MyFunctions2.alphaPass(piVector, numStates, T, B, obsList, A, true);
        double[][] alpha = (double[][]) objectList[0];
        double[] c = (double[]) objectList[1];
        double[][] beta = MyFunctions2.betaPass(numStates, T, B, obsList, A, c);
        objectList = MyFunctions2.gammaFunc(numStates, T, B, obsList, A, alpha, beta);
        double[][][] diGamma = (double[][][]) objectList[0];
        double[][] gamma = (double[][]) objectList[1];
        objectList = MyFunctions2.reestimateFunc(diGamma, gamma, obsList, numStates, T, numObs);
        A = (double[][]) objectList[0];
        B = (double[][]) objectList[1];
        piVector = (double[][]) objectList[2];
        double logProb = MyFunctions2.compLogProb(c, T);

        iters = iters + 1;
        while (true) {
            if (iters < maxIters && logProb > oldLogProb) {
                oldLogProb = logProb;
                objectList = MyFunctions2.alphaPass(piVector, numStates, T, B, obsList, A, true);
                alpha = (double[][]) objectList[0];
                c = (double[]) objectList[1];
                beta = MyFunctions2.betaPass(numStates, T, B, obsList, A, c);
                objectList = MyFunctions2.gammaFunc(numStates, T, B, obsList, A, alpha, beta);
                diGamma = (double[][][]) objectList[0];
                gamma = (double[][]) objectList[1];
                objectList = MyFunctions2.reestimateFunc(diGamma, gamma, obsList, numStates, T, numObs);
                A = (double[][]) objectList[0];
                B = (double[][]) objectList[1];
                piVector = (double[][]) objectList[2];
                logProb = MyFunctions2.compLogProb(c, T);
                iters = iters + 1;
            } else {
                break;
            }

        }

        StringBuilder sb = new StringBuilder();
        sb.append(A[0].length);
        sb.append(" ");
        sb.append(A.length);
        for (int i = 0; i < A[0].length; i++) {
            for (int j = 0; j < A.length; j++) {
                sb.append(" ");
                sb.append(A[i][j]);
            }
        }
        System.out.println(sb.toString());
        sb.setLength(0);
        sb.append(B.length);
        sb.append(" ");
        sb.append(B[0].length);
        for (int i = 0; i < B.length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                sb.append(" ");
                sb.append(B[i][j]);
            }
        }
        System.out.println(sb.toString());


    }



}

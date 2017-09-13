import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HMM1 {




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
        A = MyFunctions.fillMatrix(stringList, new double[numStates][numStates]);

        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        numObs = Integer.parseInt(stringList[1]);
        B = MyFunctions.fillMatrix(stringList, new double[numStates][numObs]);

        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        piVector = MyFunctions.fillMatrix(stringList, new double[1][numStates]);

        lineString = stdin.readLine();
        stringList = lineString.split(" ");

        int T = Integer.parseInt(stringList[0]);

        int[] obsList = new int[T];
        int counter = -1;
        for (String obs : stringList) {
            if (counter != -1) {
                obsList[counter] = Integer.parseInt(obs);
            }
            counter++;
        }

        double[][] alpha = MyFunctions.alphaPass(piVector, numStates, T, B, obsList, A, false);

        double P = 0;
        for (int i = 0; i < numStates; i++) {
            P += alpha[T-1][i];
        }


        System.out.println(P);
    }
}



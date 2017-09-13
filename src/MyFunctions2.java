// This class is used for the HMM3 exercise
public class MyFunctions2 {
    public static Object[] alphaPass(double[][] piVector, int N, int T, double[][] B, int[] obsList, double[][] A, boolean scaling) {
        // rows will be the time steps, columns will be states
        double[][] alpha = new double[T][N];
        double[] c = new double[T];
        if (scaling) {
            c[0] = 0;
        } else {
            c[0] = 1;
        }
        for (int i = 0; i < N; i++) {
            alpha[0][i] = piVector[0][i] * B[i][obsList[0]];
            if (scaling) {
                c[0] = c[0] + alpha[0][i];
            }
        }
        if (scaling) {
            c[0] = 1 / c[0];
            for (int i = 0; i < N; i++) {
                alpha[0][i] = c[0] * alpha[0][i];
            }
        }

        for (int t = 1; t < T; t++) {
            for (int i = 0; i < N; i++) {
                alpha[t][i] = 0;
                for (int j = 0; j < N; j++) {
                    alpha[t][i] = alpha[t][i] + alpha[t - 1][j] * A[j][i];
                }
                alpha[t][i] = alpha[t][i] * B[i][obsList[t]];
                if (scaling) {
                    c[t] = c[t] + alpha[t][i];
                }
            }
            if (scaling) {
                c[t] = 1 / c[t];
                for (int i = 0; i < N; i++) {
                    alpha[t][i] = c[t] * alpha[t][i];
                }
            }
        }
        return new Object[]{alpha, c};
    }

    public static double[][] betaPass(int N, int T, double[][] B, int[] obsList, double[][] A, double[] c) {
        double[][] beta = new double[T][N];
        for (int i = 0; i < N; i++) {
            beta[T - 1][i] = c[T - 1];
        }

        for (int t = T - 2; t > -1; t--) {
            for (int i = 0; i < N; i++) {
                beta[t][i] = 0;
                for (int j = 0; j < N; j++) {
                    beta[t][i] = beta[t][i] + A[i][j] * B[j][obsList[t + 1]] * beta[t + 1][j];
                }
                beta[t][i] = c[t] * beta[t][i];
            }
        }
        return beta;
    }

    public static Object[] gammaFunc(int N, int T, double[][] B, int[] obsList, double[][] A, double[][] alpha, double[][] beta) {
        double[][][] diGamma = new double[T][N][N];
        double[][] gamma = new double[T][N];

        for (int t = 0; t < T - 1; t++) {
            double denom = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    denom = denom + alpha[t][i] * A[i][j] * B[j][obsList[t + 1]] * beta[t + 1][j];
                }
            }
            for (int i = 0; i < N; i++) {
                gamma[t][i] = 0;
                for (int j = 0; j < N; j++) {
                    diGamma[t][i][j] = (alpha[t][i] * A[i][j] * B[j][obsList[t + 1]] * beta[t + 1][j]) / denom;
                    gamma[t][i] = gamma[t][i] + diGamma[t][i][j];
                }
            }
        }
        // calc gamma T-1:

        double denom = 0;
        for (int i = 0; i < N; i++) {
            denom = denom + alpha[T - 1][i];
        }
        for (int i = 0; i < N; i++) {
            gamma[T - 1][i] = alpha[T - 1][i] / denom;
        }
        return new Object[]{diGamma, gamma};
    }

    public static Object[] reestimateFunc(double[][][] diGamma, double[][] gamma, int[] obsList, int N, int T, int M) {
        double[][] piVector = new double[1][N];
        for (int i = 0; i < N; i++) {
            piVector[0][i] = gamma[0][i];
        }

        double[][] A = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double numer = 0;
                double denom = 0;
                for (int t = 0; t < T-1; t++) {
                    numer = numer + diGamma[t][i][j];
                    denom = denom + gamma[t][i];
                }
                A[i][j] = numer / denom;
            }
        }

        double[][] B = new double[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                double numer = 0;
                double denom = 0;
                for (int t = 0; t < T; t++) {
                    if (obsList[t] == j) {
                        numer = numer + gamma[t][i];
                    }
                    denom = denom + gamma[t][i];
                }
                B[i][j] = numer / denom;
            }
        }
        return new Object[]{A, B, piVector};
    }

    public static double compLogProb(double[] c, int T) {
        double logProb = 0;
        for (int t = 0; t < T; t++) {
            logProb = logProb + Math.log(c[t]);
        }
        logProb = -1 * logProb;
        return logProb;
    }

    // Returns array of most likely state sequence.
    public static int[] viterbi(double[][] piVector, int N, int T, double[][] B, int[] obsList, double[][] A) {
        double[][] delta = new double[T][N];
        int[][] delta_idx = new int[T][N];
        double delta_t_i_cand;
        double delta_t_max = -1;
        for (int i = 0; i < N; i++) {
            delta[0][i] = B[i][obsList[0]] * piVector[0][i];
        }
        for (int t = 1; t < T; t++) {
            for (int i = 0; i < N; i++) {
                delta[t][i] = -1;
                delta_t_max = -1;
                int current_arg_max = -1;
                for (int j = 0; j < N; j++) {
                    delta_t_i_cand = A[j][i] * delta[t - 1][j] * B[i][obsList[t]];
                    if (delta_t_i_cand > delta_t_max) {
                        //System.out.println(delta_t_i_cand);
                        //System.out.println(delta_t_max);
                        delta_t_max = delta_t_i_cand;

                        current_arg_max = j;
                    }
                }
                delta[t][i] = delta_t_max;
                delta_idx[t][i] = current_arg_max;
            }
        }
        // Calculating probability of most likely hidden state sequence.
        double P_mlh = 0;
        int mlh_T_state = -1;
        for (int j = 0; j < N; j++) {
            if (delta[T - 1][j] > P_mlh) {
                P_mlh = delta[T - 1][j];
                //System.out.println(P_mlh);
                mlh_T_state = j;
            }
        }

        //printMultiArray(delta);
        //System.out.println(" ");
        //printMultiArray(delta_idx);

        int[] Xhidden = new int[T];
        Xhidden[T - 1] = mlh_T_state;
        for (int t = T - 2; t > -1; t--) {
            Xhidden[t] = delta_idx[t + 1][Xhidden[t + 1]];
        }

        return Xhidden;

    }

    // Stackoverflow
    public static double[][] multiplyByMatrix(double[][] m1, double[][] m2) {
        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if (m1ColLength != m2RowLength) return null; // matrix multiplication is not possible
        int mRRowLength = m1.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        for (int i = 0; i < mRRowLength; i++) {         // rows from m1
            for (int j = 0; j < mRColLength; j++) {     // columns from m2
                for (int k = 0; k < m1ColLength; k++) { // columns from m1
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

    public static void printMultiArray(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix[0].length; i++) {
            sb.setLength(0);
            for (int j = 0; j < matrix.length; j++) {
                sb.append(matrix[j][i]);
                sb.append("            ");
            }
            System.out.println(sb.toString());
        }
    }

    public static void printMultiArray(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix[0].length; i++) {
            sb.setLength(0);
            for (int j = 0; j < matrix.length; j++) {
                sb.append(matrix[j][i]);
                sb.append("           ");
            }
            System.out.println(sb.toString());
        }
    }

}

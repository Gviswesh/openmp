import java.util.concurrent.*;

public class matrix {

    public static void main(String[] args) {
        int N = 1000; // Matrix size
        int[][] A = generateRandomMatrix(N, N);
        int[][] B = generateRandomMatrix(N, N);
        int[][] result = new int[N][N];

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < N; i++) {
            int row = i;
            executor.execute(() -> {
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < N; k++) {
                        result[row][j] += A[row][k] * B[k][j];
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Matrix multiplication took " + elapsedTime + " milliseconds.");
    }

    public static int[][] generateRandomMatrix(int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (int) (Math.random() * 10);
            }
        }
        return matrix;
    }
}

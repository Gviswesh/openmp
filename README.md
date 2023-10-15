# OpenMP implementation using Java  
In this folder, I will be storing all the Java codes that was required to execute any program related to parallel processing
## Vector-Vector Addition
In Java vector-vector addition is executed with the help of a framework called ForkJoinPool
### Initialization
- In this part, the number of elements in the vectors (n) is set to 1,000,000.
- Two input vectors (vectorA and vectorB) and an output vector (result) are initialized.
- The input vectors are filled with random double values using Math.random().
### Task Definition
- An instance of the VectorAddTask class is created, representing the main task of vector addition.
- The task is provided with input vectors, output vector, and the range of elements it needs to handle (0 to n).
### VectorAddTask Class
- The VectorAddTask class is defined as a subclass of RecursiveAction which represents tasks without return values.
- The THRESHOLD is a parameter that defines the maximum number of elements to be processed by a single task.
- In the compute method:

    - If the range of elements is below the threshold, the task performs vector addition sequentially for that range.
    - If the range exceeds the threshold, the task splits itself into two subtasks:
        - leftTask handles the first half of the range.
        - rightTask handles the second half of the range.
        - The invokeAll method is used to execute these subtasks in parallel.
## Matrix Matrix Multiplication
Aim is to achieve matrix matrix multiplication using Java and OpenMP
### Concurrent Package
`java.util.concurrent` is used for concurrent programming, allowing us to create parallel tasks.
### Main Method
- In the main method, we start by defining the matrix size N and then generate two random matrices A and B using the generateRandomMatrix method. The resulting matrix result will store the product of matrices A and B.
- We determine the number of available processor cores using Runtime.getRuntime().availableProcessors() and create an ExecutorService with a fixed number of threads. This service will manage the parallel execution of tasks.
- A loop iterates over each row of the matrices A and result. Inside this loop, a parallel task is executed for each row using the executor.execute() method. The task calculates the elements of the result matrix by performing the dot product of the row from matrix A with the column from matrix B.
- After all the tasks are submitted to the executor, we shut it down and wait for all tasks to complete using executor.awaitTermination(). This ensures that we don't proceed until all parallel tasks are finished.
- We calculate the elapsed time for the matrix multiplication and print it to the console.
- The generateRandomMatrix method creates a random matrix of the specified size with values between 0 and 9.
## Single Source Shortest Path
1. The provided code implements Dijkstra's algorithm for finding the shortest path from a single source vertex to all other vertices in a weighted graph.

2. The main method sets up an example graph represented as an adjacency matrix. You can replace this example with your own graph data.

3. The dijkstra method takes the adjacency matrix of the graph and the starting vertex as input and calculates the shortest path distances using Dijkstra's algorithm.

4. The minDistance method returns the vertex with the minimum distance value from the set of vertices not yet included in the shortest path tree.

5. The printSolution method prints the calculated shortest distances from the starting vertex to all other vertices.

6. The algorithm iterates V-1 times (where V is the number of vertices) to ensure that the shortest paths are calculated for all vertices.

7. The time complexity of the provided code is O(V^2), where V is the number of vertices. More efficient implementations using priority queues (min-heap) can achieve O(V + E*log(V)) complexity.





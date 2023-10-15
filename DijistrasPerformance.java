import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class DijistrasPerformance {

    public static void main(String[] args) {
        // Load the graph from the dataset file
        // Graph graph = loadGraph("trialgraph.txt"); // Replace with the correct file path
        Graph graph=loadGraph("com-youtube.ungraph.txt");
        // Define the number of threads to test
        int[] threadCounts = {1, 2, 4, 8};

        try (PrintWriter writer = new PrintWriter(new File("dijkstra_performance.csv"))) {
            StringBuilder sb = new StringBuilder();
            sb.append("Threads,Time (seconds)\n");

            for (int threadCount : threadCounts) {
                long startTime = System.nanoTime();
                int[] shortestDistances = dijkstraParallel(graph, 0, threadCount);
                long endTime = System.nanoTime();
                double duration = (endTime - startTime) / 1e9; // Convert nanoseconds to seconds

                sb.append(threadCount).append(",").append(duration).append("\n");
            }

            writer.write(sb.toString());
            System.out.println("CSV file 'dijkstra_performance.csv' created successfully.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Graph loadGraph(String filename) {
        Graph graph = new Graph();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\s+");
                if (parts.length == 3) {
                    int source = Integer.parseInt(parts[0]);
                    int destination = Integer.parseInt(parts[1]);
                    int weight = Integer.parseInt(parts[2]);
                    graph.addEdge(source, destination, weight);
                } else {
                    System.err.println("Invalid line in the graph file: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return graph;
    }

    public static void displayGraph(Graph graph) {
        for (int node : graph.getNodes()) {
            System.out.print("Node " + node + ": ");
            for (Edge edge : graph.getEdges(node)) {
                System.out.print("(" + edge.getDestination() + ", " + edge.getWeight() + ") ");
            }
            System.out.println();
        }
    }

    public static int[] dijkstraParallel(Graph graph, int source, int numThreads) {
        int[] distances = new int[graph.size()];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;

        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(source, 0));

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        while (!queue.isEmpty()) {
            Node node = queue.poll();

            if (node.distance == distances[node.id]) {
                for (Edge edge : graph.getEdges(node.id)) {
                    int newDistance = node.distance + edge.getWeight(); // Use the getter method

                    if (newDistance < distances[edge.getDestination()]) { // Use the getter method
                        distances[edge.getDestination()] = newDistance; // Use the getter method
                        queue.add(new Node(edge.getDestination(), newDistance)); // Use the getter method

                        // Use threads to update distances in parallel
                        executor.execute(() -> {
                            distances[edge.getDestination()] = newDistance; // Use the getter method
                        });
                    }
                }
            }
        }

        executor.shutdown();
        return distances;
    }
}

class Graph {
    private Map<Integer, List<Edge>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    public void addEdge(int source, int destination, int weight) {
        adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(new Edge(destination, weight));
        // For an undirected graph, you can add the reverse edge as well.
        // adjacencyList.computeIfAbsent(destination, k -> new ArrayList<>()).add(new Edge(source, weight));
    }

    public List<Integer> getNodes() {
        return new ArrayList<>(adjacencyList.keySet());
    }

    public List<Edge> getEdges(int node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public int size() {
        return adjacencyList.size();
    }
}

class Edge {
    private int destination;
    private int weight;

    public Edge(int destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public int getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }
}

class Node implements Comparable<Node> {
    public int id;
    public int distance;

    public Node(int id, int distance) {
        this.id = id;
        this.distance = distance;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.distance, other.distance);
    }
}

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
public class newmultinode {

    public static void main(String[] args) throws Exception {
        // Load the graph data from file
        String[] graphFiles = {"com-youtube.ungraph.txt"};
        for (String graphFile : graphFiles) {
            Graph graph = loadGraph(graphFile);
            int source = 0; // Set the source node
            int[] threadCounts = {1, 2, 4, 8};

            // Create a CSV file writer
            String csvFileName = "Youtube.csv";
            PrintWriter csvWriter = new PrintWriter(new File(csvFileName));
            csvWriter.println("Graph, Threads, Time(secs)");
            // csvWriter.println("Threads");
            // csvWriter.println("Time (seconds)"); // CSV header

            for (int threadCount : threadCounts) {
                long startTime = System.nanoTime();
                int[] distances = dijkstra(graph, source, threadCount);
                long endTime = System.nanoTime();
                double duration = (endTime - startTime) / 1000000000.0;

                // Print the results to the console
                System.out.println("Graph: " + graphFile + ", Threads: " + threadCount + ", Time: " + duration + " seconds");

                // Write the results to the CSV file
                csvWriter.println(graphFile + "," + threadCount + "," + duration);
            }

            // Close the CSV file writer
            csvWriter.close();
            System.out.println("CSV file 'Youtube.csv' created successfully.");
        }
    }

    public static Graph loadGraph(String filename) throws Exception {
        // Load the graph data from file
        Graph graph = new Graph();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#"))
                continue;
            String[] parts = line.split("\\t"); // Split using tabs
            if (parts.length != 2) {
                System.err.println("Invalid input here: " + line);
                continue;
            }

            try {
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                int w = 1; // Assuming a weight of 1 for simplicity, you can modify this as needed.
                graph.addEdge(u, v, w);
            } catch (NumberFormatException e) {
                System.err.println("Invalid input" + line);
            }
        }
        reader.close();
        return graph;
    }

    public static int[] dijkstra(Graph graph, int source, int threadCount) throws Exception {
        int[] distances = new int[graph.size()];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(source, 0));

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        class DijkstraCallable implements Callable<Void> {
            private final int from;
            private final int to;

            DijkstraCallable(int from, int to) {
                this.from = from;
                this.to = to;
            }

            @Override
            public Void call() throws Exception {
                for (int i = from; i < to; i++) {
                    Node node = queue.poll();
                    if (node.distance == distances[node.id]) {
                        for (Edge edge : graph.getEdges(node.id)) {
                            int newDistance = node.distance + edge.weight;
                            if (newDistance < distances[edge.to]) {
                                distances[edge.to] = newDistance;
                                queue.add(new Node(edge.to, newDistance));
                            }
                        }
                    }
                }
                return null;
            }
        }

        List<Callable<Void>> tasks = new ArrayList<>();
        int batchSize = graph.size() / threadCount;
        for (int i = 0; i < threadCount; i++) {
            int from = i * batchSize;
            int to = (i == threadCount - 1) ? graph.size() : (i + 1) * batchSize;
            tasks.add(new DijkstraCallable(from, to));
        }

        List<Future<Void>> futures = executorService.invokeAll(tasks);
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        return distances;
    }
    static class Graph {
        private Map<Integer, List<Edge>> edges;

        public Graph() {
            edges = new HashMap<>();
        }

        public void addEdge(int from, int to, int weight) {
            if (!edges.containsKey(from)) edges.put(from, new ArrayList<>());
            edges.get(from).add(new Edge(to, weight));
        }

        public List<Edge> getEdges(int node) {
            return edges.getOrDefault(node, new ArrayList<>());
        }

        public int size() {
            return edges.size();
        }
    }

    static class Edge {
        public int to;
        public int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    static class Node implements Comparable<Node> {
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
}

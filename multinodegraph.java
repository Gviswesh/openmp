import java.util.*;
import java.io.*;
import java.util.concurrent.*;

public class multinodegraph {
    public static void main(String[] args) throws Exception {
        // Load the graph data from file
        String[] graphFiles = {"com-youtube.ungraph.txt"};
        // String[] graphFiles = {"ca-AstroPh.txt", "ca-HepTh.txt", "com-amazon.ungraph.txt", "com-lj.ungraph.txt", "com-youtube.ungraph.txt"};
        for (String graphFile : graphFiles) {
            Graph graph = loadGraph(graphFile);
            int source = 0; // Set the source node
            int[] threadCounts = {1, 2, 4, 8};
            for (int threadCount : threadCounts) {
                long startTime = System.nanoTime();
                int[] distances = dijkstra(graph, source, threadCount);
                long endTime = System.nanoTime();
                double duration = (endTime - startTime) / 1000000000.0;
                System.out.println("Graph: " + graphFile + ", Threads: " + threadCount + ", Time: " + duration + " seconds");
            }
        }
    }

    public static Graph loadGraph(String filename) throws Exception {
        // Load the graph data from file
        Graph graph = new Graph();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            if(line.startsWith("#"))
                continue;
            String[] parts = line.split("\\s+");
            if(parts.length!=3){
                System.err.println("Invalid input here: "+line);
                continue;
            }

            try{
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                int w = Integer.parseInt(parts[2]);
                graph.addEdge(u, v, w);
            }
            catch(NumberFormatException e){
                System.err.println("Invalid input"+line);
            }
        }
        reader.close();
        return graph;

    }

    public static int[] dijkstra(Graph graph, int source, int threadCount) throws Exception {
        // Dijkstra's algorithm is implemented using the specified number of threads
        int[] distances = new int[graph.size()];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(source, 0));
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.distance == distances[node.id]) {
                for (Edge edge : graph.getEdges(node.id)) {
                    if (distances[edge.to] > node.distance + edge.weight) {
                        distances[edge.to] = node.distance + edge.weight;
                        queue.add(new Node(edge.to, distances[edge.to]));
                        final Object lock=new Object();
                        executor.execute(new Runnable() {
                            @Override
                            public void run(){
                                synchronized(lock){
                                    // Update the distance of the neighboring node in a separate thread
                                    distances[edge.to] = node.distance + edge.weight;
                                }
                            }
                        });
                    }
                }
            }
        }
        executor.shutdown();
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

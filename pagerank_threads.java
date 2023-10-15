import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class pagerank_threads {

    public static void computePageRank(Graph graph, float beta, float delta, int maxIter, Map<Integer, Float> pageRank) {
        int numNodes = graph.getNumNodes();
        Map<Integer, Float> pageRankNxt = new HashMap<>();

        // Initialize pageRank and pageRankNxt
        for (int node : graph.getNodes()) {
            pageRank.put(node, 1.0f / numNodes);
            pageRankNxt.put(node, 0.0f);
        }

        int iterCount = 0;
        float diff;

        do {
            diff = 0.0f;

            for (int v : graph.getNodes()) {
                float sum = 0.0f;

                for (int nbr : graph.getNeighbors(v)) {
                    sum += pageRank.get(nbr) / graph.getCountOutNeighbors(nbr);
                }

                float val = (1 - delta) / numNodes + delta * sum;
                pageRankNxt.put(v, val);
            }

            // Calculate the difference between pageRank and pageRankNxt
            for (int node : graph.getNodes()) {
                diff += Math.abs(pageRank.get(node) - pageRankNxt.get(node));
            }

            // Update pageRank
            for (int node : graph.getNodes()) {
                pageRank.put(node, pageRankNxt.get(node));
            }

            iterCount++;

        } while (diff > beta && iterCount < maxIter);
    }

    public static void main(String[] args) {
        // Create a graph from a .txt file
        // String file = "ca-AstroPh.txt";
            // String file = "ca-HepTh.txt";
            // String file = "com-amazon.ungraph.txt";
            // String file = "com-lj.ungraph.txt";
        String file = "com-youtube.ungraph.txt";
        Graph graph = readGraphFromFile(file);

        // Initialize PageRank values
        Map<Integer, Float> pageRank = new HashMap<>();

        // Set algorithm parameters
        float beta = 0.0001f;
        float delta = 0.85f;
        int maxIter = 100;

        // Measure time for different thread counts
        int[] threadCounts = {1, 2, 4, 8};

        for (int threadCount : threadCounts) {
            long startTime = System.currentTimeMillis();
            computePageRank(graph, beta, delta, maxIter, pageRank);
            long endTime = System.currentTimeMillis();

            System.out.println("Thread Count: " + threadCount + " - Time: " + (endTime - startTime) + " ms");
        }

        // Print PageRank values
        // for (Map.Entry<Integer, Float> entry : pageRank.entrySet()) {
        //     System.out.println("Node " + entry.getKey() + ": PageRank = " + entry.getValue() + " for " + file);
        // }
    }

    public static Graph readGraphFromFile(String filePath) {
        Graph graph = new Graph();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    int from = Integer.parseInt(parts[0]);
                    int to = Integer.parseInt(parts[1]);
                    graph.addEdge(from, to);
                } else {
                    System.err.println("Invalid line in the graph file: " + line);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return graph;
    }

    static class Graph {
        private List<List<Integer>> adjacencyList;

        public Graph() {
            adjacencyList = new ArrayList<>();
        }

        public void addEdge(int u, int v) {
            // Assuming an undirected graph
            ensureCapacity(Math.max(u, v) + 1);
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }

        public List<Integer> getNeighbors(int v) {
            return adjacencyList.get(v);
        }

        public boolean isEdge(int u, int v) {
            return adjacencyList.get(u).contains(v) || adjacencyList.get(v).contains(u);
        }

        public Iterable<Integer> getNodes() {
            List<Integer> nodes = new ArrayList<>();
            for (int i = 0; i < adjacencyList.size(); i++) {
                if (!adjacencyList.get(i).isEmpty()) {
                    nodes.add(i);
                }
            }
            return nodes;
        }

        private void ensureCapacity(int size) {
            while (adjacencyList.size() <= size) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        public int getNumNodes() {
            return adjacencyList.size();
        }

        public int getCountOutNeighbors(int node) {
            return adjacencyList.get(node).size();
        }
    }
}

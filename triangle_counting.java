import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class triangle_counting{

    public static long computeTriangleCount(Graph graph) {
        long triangleCount = 0;

        for (int v : graph.getNodes()) {
            List<Integer> neighborsV = graph.getNeighbors(v);

            for (int i = 0; i < neighborsV.size(); i++) {
                int u = neighborsV.get(i);

                for (int j = i + 1; j < neighborsV.size(); j++) {
                    int w = neighborsV.get(j);

                    if (graph.isEdge(u, w)) {
                        triangleCount += 1;
                    }
                }
            }
        }

        return triangleCount;
    }

    public static Graph loadGraphFromFile(String filePath) throws IOException {
        Graph graph = new Graph();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    int u = Integer.parseInt(parts[0]);
                    int v = Integer.parseInt(parts[1]);
                    graph.addEdge(u, v);
                }
            }
        }

        return graph;
    }

    public static void main(String[] args) {
        try {
            // Specify the path to your graph file
            // String file = "ca-AstroPh.txt";
            // String file = "ca-HepTh.txt";
            // String file = "com-amazon.ungraph.txt";
            // String file = "com-lj.ungraph.txt";
            String file = "com-youtube.ungraph.txt";
            Graph graph = loadGraphFromFile(file);

            long triangles = computeTriangleCount(graph);
            System.out.println("Triangle Count for "+file+" is "+triangles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Graph {
        private List<List<Integer>> adjacencyList;

        public Graph() {
            adjacencyList = new ArrayList<>();
        }

        public void addEdge(int u, int v) {
            // Assuming an undirected
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
    }
}

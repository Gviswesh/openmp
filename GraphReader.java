import java.io.*;
import java.util.*;

public class GraphReader {

    public static void main(String[] args) {
        // Provide the path to your input file
        // String filePath = "trialgraph.txt";
        String filePath= "trailgraph.txt";
        Graph graph = readGraphFromFile(filePath);

        // Display the loaded graph (optional)
        displayGraph(graph);
    }

    public static Graph readGraphFromFile(String filePath) {
        Graph graph = new Graph();

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\s+");

                if (parts.length == 2) {
                    int source = Integer.parseInt(parts[0]);
                    int destination = Integer.parseInt(parts[1]);
                    graph.addEdge(source, destination);
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
            for (int neighbor : graph.getNeighbors(node)) {
                System.out.print(neighbor + " ");
            }
            System.out.println();
        }
    }
}

class Graph {
    private Map<Integer, List<Integer>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    public void addEdge(int source, int destination) {
        adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(destination);
    }

    public List<Integer> getNodes() {
        return new ArrayList<>(adjacencyList.keySet());
    }

    public List<Integer> getNeighbors(int node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }
}

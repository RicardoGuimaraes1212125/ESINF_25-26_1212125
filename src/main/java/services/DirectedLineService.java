package services;

import graph.Edge;
import graph.Graph;
import dto.DirectedLineResultDTO;

import java.util.*;

/*
* Service class for computing the Directed Line Upgrade Plan.
* It performs cycle detection and topological sorting on the directed graph.
*/  
public class DirectedLineService {

    private enum Color { WHITE, GRAY, BLACK }

    public DirectedLineResultDTO computeUpgradePlan(Graph<String, Double> graph) {

        // Initialize data structures
        Map<String, Color> color = new HashMap<>();
        Deque<String> recursionStack = new ArrayDeque<>();
        List<String> topoOrder = new ArrayList<>();
        Set<String> cycleStations = new LinkedHashSet<>();

        // Initialize all vertices to WHITE
        for (String v : graph.vertices()) {
            color.put(v, Color.WHITE);
        }

        // Perform DFS for cycle detection and topological sorting
        for (String v : graph.vertices()) {
            if (color.get(v) == Color.WHITE) {
                if (dfs(v, graph, color, recursionStack, topoOrder, cycleStations)) {
                    return new DirectedLineResultDTO(
                            true,
                            null,
                            cycleStations,
                            "O(V + E)"
                    );
                }
            }
        }

        // Reverse topoOrder to get correct order
        Collections.reverse(topoOrder);

        // No cycles detected
        // Return result DTO
        return new DirectedLineResultDTO(
                false,
                topoOrder,
                Collections.emptySet(),
                "O(V + E)"
        );
    }

    // Depth-First Search to detect cycles and perform topological sorting
    private boolean dfs(String u, Graph<String, Double> graph, Map<String, Color> color, Deque<String> stack, List<String> topoOrder, Set<String> cycleStations) {

        // Mark the current node as being visited (GRAY)
        color.put(u, Color.GRAY);
        stack.push(u);

        // Recur for all the vertices adjacent to this vertex
        for (Edge<String, Double> e : graph.outgoingEdges(u)) {
            String v = e.getVDest();

            // If the adjacent vertex is in the recursion stack, found a cycle
            if (color.get(v) == Color.GRAY) {
                extractCycle(v, stack, cycleStations);
                return true;
            }

            // If the adjacent vertex is not visited, recurse on it
            if (color.get(v) == Color.WHITE) {
                if (dfs(v, graph, color, stack, topoOrder, cycleStations)) {
                    return true;
                }
            }
        }

        // Remove the vertex from recursion stack and mark it as fully visited (BLACK)
        stack.pop();
        color.put(u, Color.BLACK);
        topoOrder.add(u);
        return false;
    }

    // Extract the cycle stations from the recursion stack
    private void extractCycle(String start, Deque<String> stack, Set<String> cycleStations) {

        for (String v : stack) {
            cycleStations.add(v);
            if (v.equals(start)) break;
        }
    }
}

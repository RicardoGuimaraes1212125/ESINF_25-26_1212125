package services;

import graph.Edge;
import graph.Graph;
import dto.DirectedLineResultDTO;

import java.util.*;
/*
* Service class for computing the Directed Line Upgrade Plan.
* It performs a topological sort to determine the upgrade order of stations,
* while detecting cycles in the directed graph.
*/
public class DirectedLineService {

    private enum Color { WHITE, GRAY, BLACK }

    public DirectedLineResultDTO computeUpgradePlan(Graph<String, Double> graph) {
        // Initialize data structures for DFS
        Map<String, Color> color = new HashMap<>();
        Deque<String> stack = new ArrayDeque<>();
        List<String> topoOrder = new ArrayList<>();
        Set<String> cycleStations = new LinkedHashSet<>();

        // Initialize all vertices to WHITE
        for (String v : graph.vertices()) {
            color.put(v, Color.WHITE);
        }

        // Perform DFS for each vertex
        for (String v : graph.vertices()) {
            if (color.get(v) == Color.WHITE) {
                if (dfs(v, graph, color, stack, topoOrder, cycleStations)) {
                    return new DirectedLineResultDTO(
                            true,
                            null,
                            cycleStations,
                            "O(V + E)"
                    );
                }
            }
        }

        // Reverse the topological order to get the correct sequence
        Collections.reverse(topoOrder);

        // Return result DTO
        return new DirectedLineResultDTO(
                false,
                topoOrder,
                Collections.emptySet(),
                "O(V + E)"
        );
    }

    private boolean dfs(
            String u,
            Graph<String, Double> graph,
            Map<String, Color> color,
            Deque<String> stack,
            List<String> topoOrder,
            Set<String> cycleStations) {

        // Mark the current node as being visited (GRAY)        
        color.put(u, Color.GRAY);
        stack.push(u);

        // Explore all adjacent vertices
        for (Edge<String, Double> e : graph.outgoingEdges(u)) {
            String v = e.getVDest();

            // Cycle detected
            if (color.get(v) == Color.GRAY) {
                extractCycle(v, stack, cycleStations);
                return true;
            }

            // Continue DFS if the vertex is unvisited (WHITE)
            if (color.get(v) == Color.WHITE) {
                if (dfs(v, graph, color, stack, topoOrder, cycleStations)) {
                    return true;
                }
            }
        }

        // Mark the current node as fully processed (BLACK)
        stack.pop();
        color.put(u, Color.BLACK);
        topoOrder.add(u);
        return false;
    }

    // Extracts the cycle stations from the DFS stack
    private void extractCycle(String start, Deque<String> stack, Set<String> cycleStations) {
        for (String v : stack) {
            cycleStations.add(v);
            if (v.equals(start)) break;
        }
    }
}

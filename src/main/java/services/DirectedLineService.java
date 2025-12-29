package services;

import dto.DirectedLineResultDTO;
import graph.Edge;
import graph.Graph;

import java.util.*;

public class DirectedLineService {

    private enum Color { WHITE, GRAY, BLACK }

    public DirectedLineResultDTO computeUpgradePlan(Graph<String, Double> graph) {

        int n = graph.numVertices();
        Color[] color = new Color[n];
        Deque<String> stack = new ArrayDeque<>();
        List<String> topoOrder = new ArrayList<>();
        Set<String> cycleStations = new LinkedHashSet<>();

        // inicialização (padrão Algorithms)
        for (int i = 0; i < n; i++) {
            color[i] = Color.WHITE;
        }

        // DFS a partir de cada vértice
        for (String v : graph.vertices()) {
            int k = graph.key(v);
            if (k >= 0 && color[k] == Color.WHITE) {
                if (dfs(graph, v, color, stack, topoOrder, cycleStations)) {
                    return new DirectedLineResultDTO(
                            true,
                            null,
                            cycleStations,
                            "O(V + E)"
                    );
                }
            }
        }

        Collections.reverse(topoOrder);

        return new DirectedLineResultDTO(
                false,
                topoOrder,
                Collections.emptySet(),
                "O(V + E)"
        );
    }

    private boolean dfs(Graph<String, Double> graph,
                        String u,
                        Color[] color,
                        Deque<String> stack,
                        List<String> topoOrder,
                        Set<String> cycleStations) {

        int uKey = graph.key(u);
        color[uKey] = Color.GRAY;
        stack.push(u);

        // reutiliza iteração padrão do grafo
        for (Edge<String, Double> e : graph.outgoingEdges(u)) {
            String v = e.getVDest();
            int vKey = graph.key(v);

            if (color[vKey] == Color.GRAY) {
                extractCycle(v, stack, cycleStations);
                return true;
            }

            if (color[vKey] == Color.WHITE) {
                if (dfs(graph, v, color, stack, topoOrder, cycleStations)) {
                    return true;
                }
            }
        }

        stack.pop();
        color[uKey] = Color.BLACK;
        topoOrder.add(u);
        return false;
    }

    private void extractCycle(String start,
                              Deque<String> stack,
                              Set<String> cycleStations) {

        for (String v : stack) {
            cycleStations.add(v);
            if (v.equals(start)) break;
        }
    }
}

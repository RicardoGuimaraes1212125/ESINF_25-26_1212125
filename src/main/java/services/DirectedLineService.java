package services;

import dto.DirectedLineResultDTO;
import domain.RailNode;
import domain.RailLine;
import graph.Edge;
import graph.Graph;

import java.util.*;

public class DirectedLineService {

    private enum Color { WHITE, GRAY, BLACK }

    public DirectedLineResultDTO computeUpgradePlan(Graph<RailNode, RailLine> graph) {

        int n = graph.numVertices();
        Color[] color = new Color[n];

        Deque<RailNode> stack = new ArrayDeque<>();
        List<RailNode> topoOrder = new ArrayList<>();
        Set<RailNode> cycleStations = new LinkedHashSet<>();

        Arrays.fill(color, Color.WHITE);

        for (RailNode v : graph.vertices()) {
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

    private boolean dfs(Graph<RailNode, RailLine> graph,
                        RailNode u,
                        Color[] color,
                        Deque<RailNode> stack,
                        List<RailNode> topoOrder,
                        Set<RailNode> cycleStations) {

        int uKey = graph.key(u);
        color[uKey] = Color.GRAY;
        stack.push(u);

        for (Edge<RailNode, RailLine> e : graph.outgoingEdges(u)) {
            RailNode v = e.getVDest();
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

    private void extractCycle(RailNode start,
                              Deque<RailNode> stack,
                              Set<RailNode> cycleStations) {

        for (RailNode v : stack) {
            cycleStations.add(v);
            if (v.equals(start)) break;
        }
    }
}

package services;

import dto.DirectedLineResultDTO;
import domain.RailNode;
import domain.RailLine;
import graph.Edge;
import graph.Graph;

import java.util.*;

public class DirectedLineService {

    //statement enum for DFS
    private enum Color { WHITE, GRAY, BLACK }

    public DirectedLineResultDTO computeUpgradePlan(Graph<RailNode, RailLine> graph) {
 
        int n = graph.numVertices();
        Color[] color = new Color[n];

        Deque<RailNode> stack = new ArrayDeque<>(); // stack to keep track of the current path in DFS
        List<RailNode> topoOrder = new ArrayList<>(); // list to store topological order
        Set<RailNode> cycleStations = new LinkedHashSet<>(); // to store stations involved in cycles
        Set<RailLine> cycleLinks = new LinkedHashSet<>(); // to store links involved in cycles
        int[] cycleCount = {0}; // to count number of cycles found

        Arrays.fill(color, Color.WHITE);

        for (RailNode v : graph.vertices()) { // for each vertex
            int key = graph.key(v); 

            if (key >= 0 && color[key] == Color.WHITE) { // if not visited
                dfs(
                        graph,
                        v,
                        color,
                        stack,
                        topoOrder,
                        cycleStations,
                        cycleLinks,
                        cycleCount,
                        null
                );
            }
        }

        if (!cycleStations.isEmpty()) { // cycles detected
            return new DirectedLineResultDTO(
                    true,
                    null,
                    cycleStations,
                    cycleLinks,
                    cycleCount[0]
            );
        }

        Collections.reverse(topoOrder);

        return new DirectedLineResultDTO(
                false,
                topoOrder,
                Collections.emptySet(),
                Collections.emptySet(),
                0
        );
    }

    private void dfs(Graph<RailNode, RailLine> graph,
                     RailNode u,
                     Color[] color,
                     Deque<RailNode> stack,
                     List<RailNode> topoOrder,
                     Set<RailNode> cycleStations,
                     Set<RailLine> cycleLinks,
                     int[] cycleCount,
                     RailLine incomingEdge) {

        int uKey = graph.key(u);
        color[uKey] = Color.GRAY;
        stack.push(u);

        for (Edge<RailNode, RailLine> e : graph.outgoingEdges(u)) {

            RailNode v = e.getVDest();
            int vKey = graph.key(v);

            // Ciclo detectado (back-edge)
            if (color[vKey] == Color.GRAY && !cycleStations.contains(v)) {
                extractCycle(v, stack, cycleStations, cycleLinks, graph, e.getWeight());
                cycleCount[0]++;
            }

            // Exploração normal
            if (color[vKey] == Color.WHITE) {
                dfs(graph, v, color, stack, topoOrder, cycleStations, cycleLinks, cycleCount, e.getWeight());
            }
        }

        stack.pop();
        color[uKey] = Color.BLACK;
        
        if (cycleStations.isEmpty()) {
            topoOrder.add(u);
        }
    }

    private void extractCycle(RailNode start,
                              Deque<RailNode> stack,
                              Set<RailNode> cycleStations,
                              Set<RailLine> cycleLinks,
                              Graph<RailNode, RailLine> graph,
                              RailLine closingEdge) {

        List<RailNode> path = new ArrayList<>(stack);
        int startIdx = -1;

        // Encontra o índice de 'start' na pilha
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).equals(start)) {
                startIdx = i;
                break;
            }
        }

        if (startIdx >= 0) {
            // Adiciona todos os nós do ciclo
            for (int j = 0; j <= startIdx; j++) {
                cycleStations.add(path.get(j));
            }

            // Adiciona todas as arestas do ciclo
            for (int j = 0; j < startIdx; j++) {
                RailNode from = path.get(j);
                RailNode to = path.get(j + 1);

                // Encontra a aresta entre from e to
                for (Edge<RailNode, RailLine> e : graph.outgoingEdges(from)) {
                    if (e.getVDest().equals(to)) {
                        cycleLinks.add(e.getWeight());
                        break;
                    }
                }
            }

            // Adiciona a aresta de fechamento do ciclo
            cycleLinks.add(closingEdge);
        }
    }
}


package services;

import domain.RailLine;
import domain.RailNode;
import graph.Edge;
import graph.Graph;
import graph.map.MapGraph;

import java.util.ArrayList;
import java.util.List;

public class MinimalBackboneService {

    public Graph<RailNode, RailLine> computeMinimalBackbone(Graph<RailNode, RailLine> graph) {

        Graph<RailNode, RailLine> mst = new MapGraph<>(false);
        List<RailNode> isolatedVertices = new ArrayList<>();

        // Garante cobertura total do grafo (MSF)
        for (RailNode start : graph.vertices()) {

            if (mst.validVertex(start)) {
                continue;
            }

            mst.addVertex(start);
            boolean hasEdges = false;

            boolean expanded;
            do {
                expanded = false;
                Edge<RailNode, RailLine> minEdge = null;

                for (RailNode v : mst.vertices()) {
                    for (Edge<RailNode, RailLine> e : graph.outgoingEdges(v)) {

                        RailNode dest = e.getVDest();

                        if (!mst.validVertex(dest)) {
                            if (minEdge == null ||
                                e.getWeight().getDistance()
                                        < minEdge.getWeight().getDistance()) {
                                minEdge = e;
                            }
                        }
                    }
                }

                if (minEdge != null) {
                    mst.addVertex(minEdge.getVDest());
                    mst.addEdge(
                            minEdge.getVOrig(),
                            minEdge.getVDest(),
                            minEdge.getWeight()
                    );
                    expanded = true;
                    hasEdges = true;
                }

            } while (expanded);

            // Se nenhuma aresta foi encontrada, marca como isolada
            if (!hasEdges) {
                isolatedVertices.add(start);
            }
        }

        // Remove vértices isolados (não reachable)
        for (RailNode isolated : isolatedVertices) {
            mst.removeVertex(isolated);
        }

        return mst;
    }
}

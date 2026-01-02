package services;

import domain.RailLine;
import domain.RailNode;
import graph.Edge;
import graph.Graph;
import graph.map.MapGraph;

public class MinimalBackboneService {

    public Graph<RailNode, RailLine> computeMinimalBackbone(Graph<RailNode, RailLine> graph) {

        Graph<RailNode, RailLine> mst = new MapGraph<>(false);

        RailNode start = graph.vertices().get(0);
        mst.addVertex(start);

        while (mst.numVertices() < graph.numVertices()) {

            Edge<RailNode, RailLine> minEdge = null;

            for (RailNode v : mst.vertices()) {
                for (Edge<RailNode, RailLine> e : graph.outgoingEdges(v)) {

                    RailNode dest = e.getVDest();

                    if (!mst.validVertex(dest)) {
                        if (minEdge == null ||
                            e.getWeight().getDistance() < minEdge.getWeight().getDistance()) {
                            minEdge = e;
                        }
                    }
                }
            }

            if (minEdge == null) break;

            mst.addVertex(minEdge.getVDest());
            mst.addEdge(
                    minEdge.getVOrig(),
                    minEdge.getVDest(),
                    minEdge.getWeight()
            );
        }

        return mst;
    }
}

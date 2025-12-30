package services;

import graph.Edge;
import graph.Graph;
import graph.map.MapGraph;

public class MinimalBackboneService {

    public Graph<String, Double> computeMinimalBackbone(Graph<String, Double> graph) {

        Graph<String, Double> mst = new MapGraph<>(false);

        String start = graph.vertices().get(0);
        mst.addVertex(start);

        while (mst.numVertices() < graph.numVertices()) {

            Edge<String, Double> minEdge = null;

            for (String v : mst.vertices()) {
                for (Edge<String, Double> e : graph.outgoingEdges(v)) {

                    if (!mst.validVertex(e.getVDest())) {
                        if (minEdge == null ||
                                e.getWeight() < minEdge.getWeight()) {
                            minEdge = e;
                        }
                    }
                }
            }

            if (minEdge == null)
                break;

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

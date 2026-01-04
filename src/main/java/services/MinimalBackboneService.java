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

        // Prim's algorithm
        for (RailNode start : graph.vertices()) { 

            if (mst.validVertex(start)) { 
                continue;
            }

            mst.addVertex(start); // add starting vertex
            boolean hasEdges = false; // flag to check if any edges were added

            boolean expanded; // flag for expansion in each iteration
            do {
                expanded = false;
                Edge<RailNode, RailLine> minEdge = null;

                for (RailNode v : mst.vertices()) { // for each vertex in MST
                    for (Edge<RailNode, RailLine> e : graph.outgoingEdges(v)) { // explore outgoing edges

                        RailNode dest = e.getVDest(); // destination vertex

                        if (!mst.validVertex(dest)) { // if destination not in MST
                            if (minEdge == null || // find minimum edge
                                e.getWeight().getDistance() 
                                        < minEdge.getWeight().getDistance()) { // compare distances
                                minEdge = e; // update minimum edge
                            }
                        }
                    }
                }

                if (minEdge != null) { // found a minimum edge to add
                    mst.addVertex(minEdge.getVDest()); // add destination vertex
                    mst.addEdge( // add the edge to the MST
                            minEdge.getVOrig(), // origin vertex
                            minEdge.getVDest(), // destination vertex
                            minEdge.getWeight() // edge weight
                    );
                    expanded = true;
                    hasEdges = true;
                }

            } while (expanded);

            // Check for isolated vertex
            if (!hasEdges) {
                isolatedVertices.add(start);
            }
        }

        // Remove isolated vertices from MST
        for (RailNode isolated : isolatedVertices) {
            mst.removeVertex(isolated);
        }

        return mst;
    }
}

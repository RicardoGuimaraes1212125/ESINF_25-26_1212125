package services;

import domain.RailLine;
import domain.RailNode;
import graph.Edge;
import graph.Graph;
import graph.map.MapGraph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinimalBackboneServiceTest {

    private MinimalBackboneService service;

    @BeforeEach
    void setUp() {
        service = new MinimalBackboneService();
    }

    private RailNode n(String id) {
        return new RailNode(id, "Station " + id, 0, 0, 0, 0);
    }

    private RailLine l(String a, String b, double d) {
        return new RailLine(a, b, d, 100, 10);
    }

    private Graph<RailNode, RailLine> emptyGraphWithEightNodes() {
        Graph<RailNode, RailLine> g = new MapGraph<>(false);
        for (char c = 'A'; c <= 'H'; c++) {
            g.addVertex(n(String.valueOf(c)));
        }
        return g;
    }

    @Test
    void shouldProduceExactlyVMinusOneEdgesWhenConnected() {

        Graph<RailNode, RailLine> g = emptyGraphWithEightNodes();

        RailNode[] v = g.vertices().toArray(new RailNode[0]);

        for (int i = 0; i < v.length - 1; i++) {
            g.addEdge(v[i], v[i + 1], l(v[i].getId(), v[i + 1].getId(), i + 1));
        }

        Graph<RailNode, RailLine> mst =
                service.computeMinimalBackbone(g);

        assertEquals(8, mst.numVertices());
        assertEquals(14, mst.numEdges());
    }

    @Test
    void shouldAlwaysPickMinimalEdgeFromFrontier() {

        Graph<RailNode, RailLine> g = new MapGraph<>(false);

        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");
        RailNode d = n("D");
        RailNode e = n("E");
        RailNode f = n("F");
        RailNode g1 = n("G");
        RailNode h = n("H");

        g.addVertex(a); g.addVertex(b); g.addVertex(c); g.addVertex(d);
        g.addVertex(e); g.addVertex(f); g.addVertex(g1); g.addVertex(h);

        g.addEdge(a, b, l("A","B",10));
        g.addEdge(a, c, l("A","C",9));
        g.addEdge(c, e, l("C","E",1)); // menor
        g.addEdge(b, d, l("B","D",8));
        g.addEdge(e, f, l("E","F",7));
        g.addEdge(f, g1, l("F","G",6));
        g.addEdge(g1, h, l("G","H",5));

        Graph<RailNode, RailLine> mst =
                service.computeMinimalBackbone(g);

        assertNotNull(mst.edge(c, e));
        assertEquals(1, mst.edge(c, e).getWeight().getDistance());
    }

    @Test
    void shouldNeverCreateCycles() {

        Graph<RailNode, RailLine> g = new MapGraph<>(false);

        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");
        RailNode d = n("D");
        RailNode e = n("E");
        RailNode f = n("F");
        RailNode g1 = n("G");
        RailNode h = n("H");

        g.addVertex(a); g.addVertex(b); g.addVertex(c); g.addVertex(d);
        g.addVertex(e); g.addVertex(f); g.addVertex(g1); g.addVertex(h);

        g.addEdge(a, b, l("A","B",4));
        g.addEdge(b, c, l("B","C",3));
        g.addEdge(c, a, l("C","A",1)); // ciclo barato
        g.addEdge(c, d, l("C","D",5));
        g.addEdge(d, e, l("D","E",6));
        g.addEdge(e, f, l("E","F",7));
        g.addEdge(f, g1, l("F","G",8));
        g.addEdge(g1, h, l("G","H",9));

        Graph<RailNode, RailLine> mst =
                service.computeMinimalBackbone(g);

        assertEquals(14, mst.numEdges());
        assertFalse(mst.edge(a, c) == null && mst.edge(c, a) == null);
    }

    @Test
    void shouldHandleEqualWeightEdges() {

        Graph<RailNode, RailLine> g = emptyGraphWithEightNodes();

        RailNode[] v = g.vertices().toArray(new RailNode[0]);

        for (int i = 0; i < v.length - 1; i++) {
            g.addEdge(v[i], v[i + 1], l(v[i].getId(), v[i + 1].getId(), 5));
        }

        Graph<RailNode, RailLine> mst =
                service.computeMinimalBackbone(g);

        assertEquals(8, mst.numVertices());
        assertEquals(14, mst.numEdges());
    }

    @Test
    void shouldAvoidGreedyTrap() {

        Graph<RailNode, RailLine> g = new MapGraph<>(false);

        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");
        RailNode d = n("D");
        RailNode e = n("E");
        RailNode f = n("F");
        RailNode g1 = n("G");
        RailNode h = n("H");

        g.addVertex(a); g.addVertex(b); g.addVertex(c); g.addVertex(d);
        g.addVertex(e); g.addVertex(f); g.addVertex(g1); g.addVertex(h);

        g.addEdge(a, b, l("A","B",1));
        g.addEdge(b, c, l("B","C",100));
        g.addEdge(a, c, l("A","C",2)); // correta
        g.addEdge(c, d, l("C","D",3));
        g.addEdge(d, e, l("D","E",4));
        g.addEdge(e, f, l("E","F",5));
        g.addEdge(f, g1, l("F","G",6));
        g.addEdge(g1, h, l("G","H",7));

        Graph<RailNode, RailLine> mst =
                service.computeMinimalBackbone(g);

        assertNotNull(mst.edge(a, c));
        assertEquals(2, mst.edge(a, c).getWeight().getDistance());
    }

    @Test
        void shouldWorkCorrectlyOnDenseGraph() {

            Graph<RailNode, RailLine> g = emptyGraphWithEightNodes();
            RailNode[] v = g.vertices().toArray(new RailNode[0]);

            double w = 1;
            for (int i = 0; i < v.length; i++) {
                for (int j = i + 1; j < v.length; j++) {
                    g.addEdge(v[i], v[j], l(v[i].getId(), v[j].getId(), w++));
                }
            }

            Graph<RailNode, RailLine> mst =
                    service.computeMinimalBackbone(g);

            assertEquals(8, mst.numVertices());
            assertEquals(14, mst.numEdges());
        }

        @Test
    void shouldHandleDisconnectedGraph() {
        Graph<RailNode, RailLine> g = new MapGraph<>(false);
        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");
        RailNode d = n("D");

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);

        g.addEdge(a, b, l("A", "B", 1));
        // No edge between c and d

        Graph<RailNode, RailLine> mst = service.computeMinimalBackbone(g);

        assertEquals(2, mst.numVertices());
        assertEquals(2, mst.numEdges()); 
    }

    @Test
    void shouldHandleSingleNodeGraph() {
        Graph<RailNode, RailLine> g = new MapGraph<>(false);
        RailNode a = n("A");

        g.addVertex(a);

        Graph<RailNode, RailLine> mst = service.computeMinimalBackbone(g);

        assertEquals(1, mst.numVertices()); 
        assertEquals(0, mst.numEdges()); 
    }

    @Test
    void shouldHandleMultipleEdgesWithSameWeight() {
        Graph<RailNode, RailLine> g = new MapGraph<>(false);
        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);

        g.addEdge(a, b, l("A", "B", 1));
        g.addEdge(a, c, l("A", "C", 1)); // Same weight as A-B

        Graph<RailNode, RailLine> mst = service.computeMinimalBackbone(g);

        assertEquals(4, mst.numEdges()); // MST should include both edges
    }

    @Test
    void shouldNotAddRedundantEdges() {
        Graph<RailNode, RailLine> g = new MapGraph<>(false);
        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");
        RailNode d = n("D");

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);

        g.addEdge(a, b, l("A", "B", 1));
        g.addEdge(b, c, l("B", "C", 1));
        g.addEdge(c, d, l("C", "D", 1));
        g.addEdge(a, d, l("A", "D", 10)); // High weight edge

        Graph<RailNode, RailLine> mst = service.computeMinimalBackbone(g);

        assertEquals(6, mst.numEdges()); 
    }

    @Test
    void shouldHandleGraphWithNegativeWeights() {
        Graph<RailNode, RailLine> g = new MapGraph<>(false);
        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);

        g.addEdge(a, b, l("A", "B", -1)); // Negative weight
        g.addEdge(b, c, l("B", "C", 2));

        Graph<RailNode, RailLine> mst = service.computeMinimalBackbone(g);

        assertEquals(4, mst.numEdges()); 
    }
}


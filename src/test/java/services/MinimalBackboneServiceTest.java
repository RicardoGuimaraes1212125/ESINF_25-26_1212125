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
        assertEquals(7, mst.numEdges() / 2); // MSF com 7 arestas (V-1), dividido por 2 para grafo não-dirigido
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

        assertEquals(7, mst.numEdges() / 2); // 7 arestas para 8 vértices (V-1)
        // Verificar que não há ciclos (grafo é árvore)
        assertTrue(mst.numEdges() == (mst.numVertices() - 1) * 2);
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
        assertEquals(7, mst.numEdges() / 2);
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
            assertEquals(7, mst.numEdges() / 2);
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
        assertEquals(1, mst.numEdges() / 2); 
    }

    @Test
    void shouldPreferMinimalEdgeOverAlternativePaths() {
        Graph<RailNode, RailLine> g = new MapGraph<>(false);
        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);

        g.addEdge(a, b, l("A", "B", 1));
        g.addEdge(a, c, l("A", "C", 1000)); // Aresta cara direta
        g.addEdge(b, c, l("B", "C", 5));

        Graph<RailNode, RailLine> mst = service.computeMinimalBackbone(g);

        // MSF deve preferir A-B (custo 1) e B-C (custo 5) ao invés de A-C (custo 1000)
        assertNotNull(mst.edge(a, b));
        assertEquals(1, mst.edge(a, b).getWeight().getDistance());
        assertNotNull(mst.edge(b, c));
        assertEquals(5, mst.edge(b, c).getWeight().getDistance());
    }

}


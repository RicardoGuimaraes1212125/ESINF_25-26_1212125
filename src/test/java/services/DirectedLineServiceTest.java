package services;

import domain.RailLine;
import domain.RailNode;
import dto.DirectedLineResultDTO;
import graph.Graph;
import graph.map.MapGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectedLineServiceTest {

    private RailNode n(String id) {
        return new RailNode(id, "Station " + id, 0, 0, 0, 0);
    }

    private Graph<RailNode, RailLine> newGraph() {
        return new MapGraph<>(true);
    }

    @Test
    void largeLinearGraphShouldReturnValidTopologicalOrder() {
        Graph<RailNode, RailLine> g = newGraph();

        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");
        RailNode d = n("D");
        RailNode e = n("E");
        RailNode f = n("F");
        RailNode g1 = n("G");
        RailNode h = n("H");

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        g.addVertex(e);
        g.addVertex(f);
        g.addVertex(g1);
        g.addVertex(h);

        g.addEdge(a, b, new RailLine("A", "B", 1, 0, 0));
        g.addEdge(b, c, new RailLine("B", "C", 1, 0, 0));
        g.addEdge(c, d, new RailLine("C", "D", 1, 0, 0));
        g.addEdge(d, e, new RailLine("D", "E", 1, 0, 0));
        g.addEdge(e, f, new RailLine("E", "F", 1, 0, 0));
        g.addEdge(f, g1, new RailLine("F", "G", 1, 0, 0));
        g.addEdge(g1, h, new RailLine("G", "H", 1, 0, 0));

        DirectedLineResultDTO result = new DirectedLineService().computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getCycleCount());
        assertEquals(8, result.getUpgradeOrder().size());
        assertTrue(result.getUpgradeOrder().indexOf(a) < result.getUpgradeOrder().indexOf(h));
        assertTrue(result.getCycleLinks().isEmpty());
    }

    @Test
    void emptyGraphShouldReturnEmptyResult() {
        Graph<RailNode, RailLine> g = newGraph();
        DirectedLineResultDTO result = new DirectedLineService().computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getCycleCount());
        assertEquals(0, result.getUpgradeOrder().size());
        assertTrue(result.getCycleLinks().isEmpty());
    }

    @Test
    void singleNodeGraphShouldReturnSingleNode() {
        Graph<RailNode, RailLine> g = newGraph();
        RailNode a = n("A");
        g.addVertex(a);

        DirectedLineResultDTO result = new DirectedLineService().computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getCycleCount());
        assertEquals(1, result.getUpgradeOrder().size());
        assertTrue(result.getUpgradeOrder().contains(a));
        assertTrue(result.getCycleLinks().isEmpty());
    }

    @Test
    void graphWithCycleShouldDetectIt() {
        Graph<RailNode, RailLine> g = newGraph();
        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);

        RailLine ab = new RailLine("A", "B", 1, 0, 0);
        RailLine bc = new RailLine("B", "C", 1, 0, 0);
        RailLine ca = new RailLine("C", "A", 1, 0, 0);

        g.addEdge(a, b, ab);
        g.addEdge(b, c, bc);
        g.addEdge(c, a, ca);

        DirectedLineResultDTO result = new DirectedLineService().computeUpgradePlan(g);

        assertTrue(result.hasCycle());
        assertEquals(1, result.getCycleCount());
        assertEquals(3, result.getCycleStations().size());
        assertTrue(result.getCycleStations().contains(a));
        assertTrue(result.getCycleStations().contains(b));
        assertTrue(result.getCycleStations().contains(c));
        assertTrue(result.getCycleLinks().contains(ab) || result.getCycleLinks().contains(bc) || result.getCycleLinks().contains(ca));
    }

    @Test
    void complexGraphShouldMaintainOrdering() {
        Graph<RailNode, RailLine> g = newGraph();
        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");
        RailNode d = n("D");

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);

        g.addEdge(a, b, new RailLine("A", "B", 1, 0, 0));
        g.addEdge(a, c, new RailLine("A", "C", 1, 0, 0));
        g.addEdge(b, d, new RailLine("B", "D", 1, 0, 0));
        g.addEdge(c, d, new RailLine("C", "D", 1, 0, 0));

        DirectedLineResultDTO result = new DirectedLineService().computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getCycleCount());
        assertEquals(4, result.getUpgradeOrder().size());
        assertTrue(result.getUpgradeOrder().indexOf(a) < result.getUpgradeOrder().indexOf(d));
        assertTrue(result.getCycleLinks().isEmpty());
    }

    @Test
    void multipleCyclesShouldBeDetected() {
        Graph<RailNode, RailLine> g = newGraph();
        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");
        RailNode d = n("D");
        RailNode e = n("E");

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);
        g.addVertex(e);

        // Primeiro ciclo: A → B → C → A
        g.addEdge(a, b, new RailLine("A", "B", 1, 0, 0));
        g.addEdge(b, c, new RailLine("B", "C", 1, 0, 0));
        g.addEdge(c, a, new RailLine("C", "A", 1, 0, 0));

        // Segundo ciclo: D → E → D
        g.addEdge(d, e, new RailLine("D", "E", 1, 0, 0));
        g.addEdge(e, d, new RailLine("E", "D", 1, 0, 0));

        DirectedLineResultDTO result = new DirectedLineService().computeUpgradePlan(g);

        assertTrue(result.hasCycle());
        assertEquals(2, result.getCycleCount()); // Dois ciclos detectados
        assertTrue(result.getCycleStations().size() >= 5); // Pelo menos 5 estações (3 + 2)
        assertTrue(result.getCycleLinks().size() >= 2); // Pelo menos 2 arestas
    }
}

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
        assertEquals(8, result.getUpgradeOrder().size());
        assertTrue(result.getUpgradeOrder().indexOf(a) < result.getUpgradeOrder().indexOf(h));
    }

    @Test
    void emptyGraphShouldReturnEmptyResult() {
        Graph<RailNode, RailLine> g = newGraph();
        DirectedLineResultDTO result = new DirectedLineService().computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getUpgradeOrder().size());
    }

    @Test
    void singleNodeGraphShouldReturnSingleNode() {
        Graph<RailNode, RailLine> g = newGraph();
        RailNode a = n("A");
        g.addVertex(a);

        DirectedLineResultDTO result = new DirectedLineService().computeUpgradePlan(g);

        assertFalse(result.hasCycle());
        assertEquals(1, result.getUpgradeOrder().size());
        assertTrue(result.getUpgradeOrder().contains(a));
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

        g.addEdge(a, b, new RailLine("A", "B", 1, 0, 0));
        g.addEdge(b, c, new RailLine("B", "C", 1, 0, 0));
        g.addEdge(c, a, new RailLine("C", "A", 1, 0, 0));

        DirectedLineResultDTO result = new DirectedLineService().computeUpgradePlan(g);

        assertTrue(result.hasCycle());
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
        assertEquals(4, result.getUpgradeOrder().size());
        assertTrue(result.getUpgradeOrder().indexOf(a) < result.getUpgradeOrder().indexOf(d));
    }
}

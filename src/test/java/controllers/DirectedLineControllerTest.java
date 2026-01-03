package controllers;

import domain.RailLine;
import domain.RailNode;
import dto.DirectedLineResultDTO;
import graph.Graph;
import graph.map.MapGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectedLineControllerTest {

    private RailNode n(String id) {
        return new RailNode(id, "Station " + id, 0, 0, 0, 0);
    }

    @Test
    void controllerShouldHandleMediumGraph() {

        Graph<RailNode, RailLine> g = new MapGraph<>(true);

        RailNode s1 = n("S1");
        RailNode s2 = n("S2");
        RailNode s3 = n("S3");
        RailNode s4 = n("S4");
        RailNode s5 = n("S5");
        RailNode s6 = n("S6");

        g.addEdge(s1, s2, new RailLine("S1", "S2", 5, 0, 0));
        g.addEdge(s2, s3, new RailLine("S2", "S3", 3, 0, 0));
        g.addEdge(s3, s4, new RailLine("S3", "S4", 2, 0, 0));
        g.addEdge(s1, s5, new RailLine("S1", "S5", 7, 0, 0));
        g.addEdge(s5, s6, new RailLine("S5", "S6", 1, 0, 0));

        DirectedLineResultDTO result =
                new DirectedLineController().execute(g);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getCycleCount());
        assertEquals(6, result.getUpgradeOrder().size());
        assertTrue(result.getCycleLinks().isEmpty());
    }

    @Test
    void controllerShouldReturnCycleInformation() {

        Graph<RailNode, RailLine> g = new MapGraph<>(true);

        RailNode x = n("X");
        RailNode y = n("Y");
        RailNode z = n("Z");

        RailLine xy = new RailLine("X", "Y", 1, 0, 0);
        RailLine yz = new RailLine("Y", "Z", 1, 0, 0);
        RailLine zx = new RailLine("Z", "X", 1, 0, 0);

        g.addEdge(x, y, xy);
        g.addEdge(y, z, yz);
        g.addEdge(z, x, zx);

        DirectedLineResultDTO result =
                new DirectedLineController().execute(g);

        assertTrue(result.hasCycle());
        assertEquals(1, result.getCycleCount());
        assertEquals(3, result.getCycleStations().size());
        assertTrue(result.getCycleLinks().contains(xy) || result.getCycleLinks().contains(yz) || result.getCycleLinks().contains(zx));
    }

    @Test
    void controllerShouldHandleComplexDependencies() {
        Graph<RailNode, RailLine> g = new MapGraph<>(true);

        RailNode p1 = n("P1");
        RailNode p2 = n("P2");
        RailNode p3 = n("P3");
        RailNode p4 = n("P4");

        g.addVertex(p1);
        g.addVertex(p2);
        g.addVertex(p3);
        g.addVertex(p4);

        g.addEdge(p1, p2, new RailLine("P1", "P2", 10, 0, 0));
        g.addEdge(p1, p3, new RailLine("P1", "P3", 5, 0, 0));
        g.addEdge(p2, p4, new RailLine("P2", "P4", 7, 0, 0));
        g.addEdge(p3, p4, new RailLine("P3", "P4", 3, 0, 0));

        DirectedLineResultDTO result = new DirectedLineController().execute(g);

        assertFalse(result.hasCycle());
        assertEquals(4, result.getUpgradeOrder().size());
        assertTrue(result.getUpgradeOrder().indexOf(p1) < result.getUpgradeOrder().indexOf(p2));
        assertTrue(result.getUpgradeOrder().indexOf(p1) < result.getUpgradeOrder().indexOf(p3));
        assertTrue(result.getUpgradeOrder().indexOf(p2) < result.getUpgradeOrder().indexOf(p4));
        assertTrue(result.getUpgradeOrder().indexOf(p3) < result.getUpgradeOrder().indexOf(p4));
    }

    @Test
    void controllerShouldDetectMultipleCycles() {
        Graph<RailNode, RailLine> g = new MapGraph<>(true);

        RailNode a = n("A");
        RailNode b = n("B");
        RailNode c = n("C");
        RailNode d = n("D");

        g.addVertex(a);
        g.addVertex(b);
        g.addVertex(c);
        g.addVertex(d);

        // Ciclo 1: A → B → A
        g.addEdge(a, b, new RailLine("A", "B", 1, 0, 0));
        g.addEdge(b, a, new RailLine("B", "A", 1, 0, 0));

        // Ciclo 2: C → D → C
        g.addEdge(c, d, new RailLine("C", "D", 1, 0, 0));
        g.addEdge(d, c, new RailLine("D", "C", 1, 0, 0));

        DirectedLineResultDTO result = new DirectedLineController().execute(g);

        assertTrue(result.hasCycle());
        assertEquals(2, result.getCycleCount());
        assertTrue(result.getCycleStations().contains(a));
        assertTrue(result.getCycleStations().contains(b));
        assertTrue(result.getCycleStations().contains(c));
        assertTrue(result.getCycleStations().contains(d));
    }

    @Test
    void controllerShouldHandleEmptyGraph() {
        Graph<RailNode, RailLine> g = new MapGraph<>(true);
        DirectedLineResultDTO result = new DirectedLineController().execute(g);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getCycleCount());
        assertEquals(0, result.getUpgradeOrder().size());
        assertTrue(result.getCycleLinks().isEmpty());
    }

    @Test
    void controllerShouldHandleSingleNodeWithoutCycle() {
        Graph<RailNode, RailLine> g = new MapGraph<>(true);
        RailNode single = n("SINGLE");
        g.addVertex(single);

        DirectedLineResultDTO result = new DirectedLineController().execute(g);

        assertFalse(result.hasCycle());
        assertEquals(0, result.getCycleCount());
        assertEquals(1, result.getUpgradeOrder().size());
        assertTrue(result.getUpgradeOrder().contains(single));
    }
}

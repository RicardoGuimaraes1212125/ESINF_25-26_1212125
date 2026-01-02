package e2e;

import controllers.MinimalBackboneController;
import domain.RailLine;
import domain.RailNode;
import graph.Graph;
import graph.map.MapGraph;
import org.junit.jupiter.api.Test;
import utils.GraphvizExporter;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class MinimalBackboneE2ETest {

    @Test
    void endToEnd_US12_MinimalBackboneNetwork() throws Exception {

        // ---------- Arrange ----------
        Graph<RailNode, RailLine> graph = new MapGraph<>(false);

        RailNode a = n("A", 0, 0);
        RailNode b = n("B", 1, 0);
        RailNode c = n("C", 2, 0);
        RailNode d = n("D", 3, 0);
        RailNode e = n("E", 4, 0);
        RailNode f = n("F", 5, 0);
        RailNode g = n("G", 6, 0);
        RailNode h = n("H", 7, 0);

        graph.addEdge(a, b, l("A","B",5));
        graph.addEdge(a, c, l("A","C",3));
        graph.addEdge(b, d, l("B","D",6));
        graph.addEdge(c, d, l("C","D",4));
        graph.addEdge(d, e, l("D","E",2));
        graph.addEdge(e, f, l("E","F",1));
        graph.addEdge(f, g, l("F","G",7));
        graph.addEdge(g, h, l("G","H",2));
        graph.addEdge(b, h, l("B","H",20)); 

        MinimalBackboneController controller = new MinimalBackboneController();

        Graph<RailNode, RailLine> backbone =
                controller.computeMinimalBackbone(graph);

        GraphvizExporter.exportUndirectedBackbone(
                backbone, "target/minimal_backbone_e2e.dot");

        assertEquals(8, backbone.numVertices());
        assertEquals(7, backbone.numEdges() / 2); 

        File dot = new File("target/minimal_backbone_e2e.dot");
        assertTrue(dot.exists());
    }

    private RailNode n(String id, double x, double y) {
        return new RailNode(id, id, 0, 0, x, y);
    }

    private RailLine l(String a, String b, double d) {
        return new RailLine(a, b, d, 1, d);
    }
}

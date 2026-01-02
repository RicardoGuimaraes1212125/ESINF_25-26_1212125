package controllers;

import domain.RailLine;
import domain.RailNode;
import graph.Graph;
import graph.map.MapGraph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinimalBackboneControllerTest {

    @Test
    void controllerShouldDelegateToServiceCorrectly() {
        // Arrange
        MinimalBackboneController controller =
                new MinimalBackboneController();

        Graph<RailNode, RailLine> graph =
                new MapGraph<>(false);

        RailNode a = new RailNode("A","Station A",0,0,0,0);
        RailNode b = new RailNode("B","Station B",0,0,0,0);
        RailNode c = new RailNode("C","Station C",0,0,0,0);
        RailNode d = new RailNode("D","Station D",0,0,0,0);
        RailNode e = new RailNode("E","Station E",0,0,0,0);
        RailNode f = new RailNode("F","Station F",0,0,0,0);
        RailNode g = new RailNode("G","Station G",0,0,0,0);
        RailNode h = new RailNode("H","Station H",0,0,0,0);

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addVertex(d);
        graph.addVertex(e);
        graph.addVertex(f);
        graph.addVertex(g);
        graph.addVertex(h);

        graph.addEdge(a, b, new RailLine("A","B",4,100,10));
        graph.addEdge(a, c, new RailLine("A","C",3,100,10));
        graph.addEdge(c, d, new RailLine("C","D",2,100,10));
        graph.addEdge(d, e, new RailLine("D","E",5,100,10));
        graph.addEdge(e, f, new RailLine("E","F",6,100,10));
        graph.addEdge(f, g, new RailLine("F","G",1,100,10));
        graph.addEdge(g, h, new RailLine("G","H",7,100,10));

        // Act
        Graph<RailNode, RailLine> result =
                controller.computeMinimalBackbone(graph);

        // Assert
        assertNotNull(result);
        assertEquals(8, result.numVertices());
        assertEquals(14, result.numEdges());
    }
}


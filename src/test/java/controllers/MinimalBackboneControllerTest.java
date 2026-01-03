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
        assertEquals(7, result.numEdges() / 2);
    }

    @Test
    void controllerShouldHandleDisconnectedComponents() {
        MinimalBackboneController controller = new MinimalBackboneController();
        Graph<RailNode, RailLine> graph = new MapGraph<>(false);

        RailNode a = new RailNode("A", "Station A", 0, 0, 0, 0);
        RailNode b = new RailNode("B", "Station B", 0, 0, 1, 0);
        RailNode c = new RailNode("C", "Station C", 0, 0, 2, 0);
        RailNode d = new RailNode("D", "Station D", 0, 0, 5, 0);
        RailNode e = new RailNode("E", "Station E", 0, 0, 6, 0);

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addVertex(d);
        graph.addVertex(e);

        // Componente 1
        graph.addEdge(a, b, new RailLine("A", "B", 1, 100, 10));
        graph.addEdge(b, c, new RailLine("B", "C", 1, 100, 10));

        // Componente 2 (isolado)
        graph.addEdge(d, e, new RailLine("D", "E", 1, 100, 10));

        Graph<RailNode, RailLine> result = controller.computeMinimalBackbone(graph);

        assertNotNull(result);
        assertEquals(5, result.numVertices());
        // 2 arestas para componente 1 (a-b, b-c) e 1 aresta para componente 2 (d-e)
        assertEquals(3, result.numEdges() / 2);
    }

    @Test
    void controllerShouldHandleIsolatedVertices() {
        MinimalBackboneController controller = new MinimalBackboneController();
        Graph<RailNode, RailLine> graph = new MapGraph<>(false);

        RailNode a = new RailNode("A", "Station A", 0, 0, 0, 0);
        RailNode b = new RailNode("B", "Station B", 0, 0, 1, 0);
        RailNode isolated = new RailNode("ISO", "Isolated", 0, 0, 10, 10);

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(isolated);

        graph.addEdge(a, b, new RailLine("A", "B", 5, 100, 10));
        // isolated não tem arestas

        Graph<RailNode, RailLine> result = controller.computeMinimalBackbone(graph);

        assertNotNull(result);
        assertEquals(2, result.numVertices()); // Vértice isolado removido
        assertEquals(1, result.numEdges() / 2);
        assertFalse(result.validVertex(isolated));
    }

    @Test
    void controllerShouldHandleDenseGraph() {
        MinimalBackboneController controller = new MinimalBackboneController();
        Graph<RailNode, RailLine> graph = new MapGraph<>(false);

        RailNode[] nodes = new RailNode[6];
        for (int i = 0; i < 6; i++) {
            nodes[i] = new RailNode("N" + i, "Station " + i, 0, 0, i, i);
            graph.addVertex(nodes[i]);
        }

        // Grafo completo
        int edgeNum = 1;
        for (int i = 0; i < 6; i++) {
            for (int j = i + 1; j < 6; j++) {
                graph.addEdge(nodes[i], nodes[j],
                        new RailLine("N" + i, "N" + j, edgeNum++, 100, 10));
            }
        }

        Graph<RailNode, RailLine> result = controller.computeMinimalBackbone(graph);

        assertNotNull(result);
        assertEquals(6, result.numVertices());
        assertEquals(5, result.numEdges() / 2); // V-1 = 5
    }

    @Test
    void controllerShouldHandleEmptyGraph() {
        MinimalBackboneController controller = new MinimalBackboneController();
        Graph<RailNode, RailLine> graph = new MapGraph<>(false);

        Graph<RailNode, RailLine> result = controller.computeMinimalBackbone(graph);

        assertNotNull(result);
        assertEquals(0, result.numVertices());
        assertEquals(0, result.numEdges());
    }

}


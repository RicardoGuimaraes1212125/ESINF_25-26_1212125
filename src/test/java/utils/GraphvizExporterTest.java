package utils;

import graph.Graph;
import graph.map.MapGraph;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GraphvizExporterTest {

    @Test
    void exporterShouldHighlightCycleAndRelatedNodes() throws Exception {

        Graph<String, Double> g = new MapGraph<>(true);

        g.addEdge("A", "B", 1.0);
        g.addEdge("B", "C", 1.0);
        g.addEdge("C", "A", 1.0);
        g.addEdge("C", "D", 1.0);
        g.addEdge("D", "E", 1.0);

        File file = File.createTempFile("rail", ".dot");

        GraphvizExporter.exportDirectedGraph(
                g,
                Set.of("A", "B", "C"),
                file.getAbsolutePath()
        );

        String content = Files.readString(file.toPath());

        assertTrue(content.contains("firebrick3"));
        assertTrue(content.contains("orange"));
        assertTrue(content.contains("khaki1"));

        file.delete();
    }
}


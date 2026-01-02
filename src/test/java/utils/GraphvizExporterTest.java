package utils;

import domain.RailLine;
import domain.RailNode;
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

        Graph<RailNode, RailLine> g = new MapGraph<>(true);

        RailNode a = new RailNode("A","A",0,0,0,0);
        RailNode b = new RailNode("B","B",0,0,0,0);
        RailNode c = new RailNode("C","C",0,0,0,0);
        RailNode d = new RailNode("D","D",0,0,0,0);
        RailNode e = new RailNode("E","E",0,0,0,0);

        g.addEdge(a,b,new RailLine("A","B",1,0,0));
        g.addEdge(b,c,new RailLine("B","C",1,0,0));
        g.addEdge(c,a,new RailLine("C","A",1,0,0));
        g.addEdge(c,d,new RailLine("C","D",1,0,0));
        g.addEdge(d,e,new RailLine("D","E",1,0,0));

        File file = File.createTempFile("rail", ".dot");

        GraphvizExporter.exportDirectedGraph(
                g,
                Set.of(a,b,c),
                file.getAbsolutePath()
        );

        String content = Files.readString(file.toPath());

        assertTrue(content.contains("firebrick3"));
        assertTrue(content.contains("orange"));
        assertTrue(content.contains("khaki1"));

        file.delete();
    }
}

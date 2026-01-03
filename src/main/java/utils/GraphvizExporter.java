package utils;

import graph.Edge;
import graph.Graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import domain.RailLine;
import domain.RailNode;

public class GraphvizExporter {

    public static void exportDirectedGraph(
        Graph<RailNode, RailLine> graph,
        Set<RailNode> cycleStations,
        String filePath) throws IOException {

        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("digraph Railway {\n");
            writer.write("    layout=neato;\n");
            writer.write("    overlap=false;\n");
            writer.write("    splines=true;\n");
            writer.write("    sep=1.2;\n");
            writer.write("    node [shape=circle, style=filled, fontname=\"Helvetica\"];\n");
            writer.write("    edge [fontname=\"Helvetica\", arrowsize=0.7];\n\n");

            // Nodes
            for (RailNode n : graph.vertices()) {

                boolean inCycle = cycleStations != null && cycleStations.contains(n);
                boolean pred = false;
                boolean succ = false;

                if (!inCycle && cycleStations != null) {
                    for (Edge<RailNode, RailLine> e : graph.outgoingEdges(n)) {
                        if (cycleStations.contains(e.getVDest())) {
                            pred = true;
                            break;
                        }
                    }
                    for (Edge<RailNode, RailLine> e : graph.incomingEdges(n)) {
                        if (cycleStations.contains(e.getVOrig())) {
                            succ = true;
                            break;
                        }
                    }
                }

                String fill;
                double size;

                if (inCycle) {
                    fill = "firebrick3";
                    size = 0.45;
                } else if (pred) {
                    fill = "orange";
                    size = 0.35;
                } else if (succ) {
                    fill = "khaki1";
                    size = 0.35;
                } else {
                    fill = "lightblue";
                    size = 0.25;
                }

                writer.write(String.format(
                    Locale.US,
                    "    \"%s\" [label=\"%s - %s\", fillcolor=%s, width=%.2f];\n",
                    safeId(n),
                    n.getId(),
                    n.getName().replace("\"", "\\\""),
                    fill,
                    size
            ));

            }

            writer.write("\n");

            // Edges
            for (RailNode v : graph.vertices()) {
                for (Edge<RailNode, RailLine> e : graph.outgoingEdges(v)) {

                    boolean fromCycle = cycleStations != null && cycleStations.contains(e.getVOrig());
                    boolean toCycle   = cycleStations != null && cycleStations.contains(e.getVDest());

                    String color = "gray60";
                    String width = "1.0";

                    if (fromCycle && toCycle) {
                        color = "firebrick3";
                        width = "2.5";
                    } else if (!fromCycle && toCycle) {
                        color = "orange";
                        width = "2.0";
                    } else if (fromCycle && !toCycle) {
                        color = "goldenrod";
                        width = "2.0";
                    }

                    writer.write(String.format(
                            "    %s -> %s [color=%s, penwidth=%s];\n",
                            safeId(e.getVOrig()),
                            safeId(e.getVDest()),
                            color,
                            width
                    ));
                }
            }

            writer.write("\n    subgraph cluster_legend {\n");
            writer.write("        label=\"Legenda\";\n");
            writer.write("        key1 [label=\"Em ciclo\", fillcolor=firebrick3, fontcolor=white];\n");
            writer.write("        key2 [label=\"Antecessor de ciclo\", fillcolor=orange];\n");
            writer.write("        key3 [label=\"Sucessor de ciclo\", fillcolor=khaki1];\n");
            writer.write("        key4 [label=\"Estação normal\", fillcolor=lightblue];\n");
            writer.write("    }\n");

            writer.write("}\n");
        }
    }

    public static void exportUndirectedBackbone(
        Graph<RailNode, RailLine> graph,
        String filePath) throws IOException {

        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("graph MinimalBackbone {\n");

            writer.write("    layout=neato;\n");
            writer.write("    overlap=false;\n");
            writer.write("    splines=true;\n");
            writer.write("    sep=1.2;\n");

            writer.write("    node [shape=circle, style=filled, ");
            writer.write("fillcolor=lightsteelblue, fontname=\"Helvetica\"];\n");
            writer.write("    edge [color=gray40, penwidth=1.4, fontname=\"Helvetica\"];\n\n");

            // Nodes with XY coordinates
            for (RailNode n : graph.vertices()) {

                writer.write(String.format(
                        "    %s [label=\"%s\", pos=\"%.2f,%.2f!\"];\n",
                        safeId(n),
                        n.getId() + " | " + n.getName(),
                        n.getX(),
                        n.getY()
                ));
            }

            writer.write("\n");

            // Edges (avoid duplicates)
            for (Edge<RailNode, RailLine> e : graph.edges()) {

                RailNode a = e.getVOrig();
                RailNode b = e.getVDest();

                if (a.getId().compareTo(b.getId()) < 0) {
                    writer.write(String.format(
                            "    %s -- %s [label=\"%.2f km\"];\n",
                            safeId(a),
                            safeId(b),
                            e.getWeight().getDistance()
                    ));
                }
            }

            writer.write("}\n");
        }
    }

    private static String safeId(RailNode n) {
        return "N" + n.getId();
    }
    
}

package utils;

import graph.Edge;
import graph.Graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class GraphvizExporter {

    public static <V> void exportDirectedGraph(
            Graph<V, Double> graph,
            Set<V> cycleStations,
            String filePath) throws IOException {

        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("digraph Railway {\n");

            // Layout settings
            writer.write("    layout=neato;\n");
            writer.write("    overlap=false;\n");
            writer.write("    splines=true;\n");
            writer.write("    sep=1.2;\n");

            // Node and edge styles
            writer.write("    node [shape=circle, style=filled, fontname=\"Helvetica\"];\n");
            writer.write("    edge [fontname=\"Helvetica\", arrowsize=0.7];\n\n");

            // Vertices
            for (V v : graph.vertices()) {

                boolean inCycle = cycleStations != null && cycleStations.contains(v);
                boolean predecessorOfCycle = false;
                boolean successorOfCycle = false;

                // Check if vertex is predecessor or successor of any cycle station
                if (cycleStations != null && !inCycle) {
                    // Check outgoing edges
                    for (Edge<V, Double> e : graph.outgoingEdges(v)) {
                        if (cycleStations.contains(e.getVDest())) {
                            predecessorOfCycle = true;
                            break;
                        }
                    }
                    // Check incoming edges
                    for (Edge<V, Double> e : graph.incomingEdges(v)) {
                        if (cycleStations.contains(e.getVOrig())) {
                            successorOfCycle = true;
                            break;
                        }
                    }
                }

                // Define node style based on its status
                if (inCycle) {
                    writer.write(String.format(
                            "    \"%s\" [fillcolor=firebrick3, fontcolor=white, width=0.45];\n", v));
                } else if (predecessorOfCycle) {
                    writer.write(String.format(
                            "    \"%s\" [fillcolor=orange, width=0.35];\n", v));
                } else if (successorOfCycle) {
                    writer.write(String.format(
                            "    \"%s\" [fillcolor=khaki1, width=0.35];\n", v));
                } else {
                    writer.write(String.format(
                            "    \"%s\" [fillcolor=lightblue, width=0.25];\n", v));
                }
            }

            writer.write("\n");

            // Edges
            for (V v : graph.vertices()) {
                for (Edge<V, Double> e : graph.outgoingEdges(v)) {

                    boolean fromCycle = cycleStations != null && cycleStations.contains(e.getVOrig());
                    boolean toCycle   = cycleStations != null && cycleStations.contains(e.getVDest());

                    String color = "gray60";
                    String width = "1.0";

                    // Adjust edge style based on connected vertices' status
                    if (fromCycle && toCycle) { // Both in cycle
                        color = "firebrick3";
                        width = "2.5";
                    } else if (!fromCycle && toCycle) { // To cycle
                        color = "orange";
                        width = "2.0";
                    } else if (fromCycle && !toCycle) { // From cycle
                        color = "goldenrod";
                        width = "2.0";
                    }

                    writer.write(String.format(
                            "    \"%s\" -> \"%s\" [color=%s, penwidth=%s];\n",
                            e.getVOrig(), e.getVDest(), color, width
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

    public static <V> void exportUndirectedBackbone(
            Graph<V, Double> graph,
            String filePath) throws IOException {

        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("graph MinimalBackbone {\n");

            // Layout settings
            writer.write("    layout=neato;\n");
            writer.write("    overlap=false;\n");
            writer.write("    splines=true;\n");
            writer.write("    sep=1.2;\n");
            writer.write("    node [shape=circle, width=0.25, fixedsize=true, ");
            writer.write("style=filled, fillcolor=lightsteelblue, fontname=\"Helvetica\"];\n");
            writer.write("    edge [color=gray40, penwidth=1.5];\n\n");

            // Vertices
            for (V v : graph.vertices()) {
                writer.write(String.format(
                        "    \"%s\";\n", v
                ));
            }

            writer.write("\n");

            // Edges (only one direction for undirected graph)
            for (Edge<V, Double> e : graph.edges()) {

                if (graph.isDirected() ||
                    e.getVOrig().toString().compareTo(e.getVDest().toString()) < 0) {

                    writer.write(String.format(
                            "    \"%s\" -- \"%s\" [label=\"%.2f\"];\n",
                            e.getVOrig(), e.getVDest(), e.getWeight()
                    ));
                }
            }

            writer.write("}\n");
        }
    }
}

package graph;

import graph.matrix.MatrixGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.BinaryOperator;

/**
 *
 * @author DEI-ISEP
 *
 */
public class Algorithms {

    /** Performs breadth-first search of a Graph starting in a vertex
     *
     * @param g Graph instance
     * @param vert vertex that will be the source of the search
     * @return a LinkedList with the vertices of breadth-first search
     */
    public static <V, E> LinkedList<V> BreadthFirstSearch(Graph<V, E> g, V vert) {

        if (g == null || vert == null || !g.validVertex(vert))
            return null;

        int n = g.numVertices();
        boolean[] visited = new boolean[n];
        LinkedList<V> q = new LinkedList<>();
        LinkedList<V> result = new LinkedList<>();

        int startKey = g.key(vert);
        if (startKey < 0) return null;

        q.add(vert);
        visited[startKey] = true;

        while (!q.isEmpty()) {
            V v = q.removeFirst();
            result.add(v);

            for (V adj : g.adjVertices(v)) {
                int k = g.key(adj);
                if (k >= 0 && !visited[k]) {
                    visited[k] = true;
                    q.addLast(adj);
                }
            }
        }

        return result;
    }

    /** Performs depth-first search starting in a vertex
     *
     * @param g Graph instance
     * @param vOrig vertex of graph g that will be the source of the search
     * @param visited set of previously visited vertices
     * @param qdfs return LinkedList with vertices of depth-first search
     */
    private static <V, E> void DepthFirstSearch(Graph<V, E> g, V vOrig, boolean[] visited, LinkedList<V> qdfs) {

        int key = g.key(vOrig);
        if (key < 0) return;

        visited[key] = true;
        qdfs.addLast(vOrig);

        for (V adj : g.adjVertices(vOrig)) {
            int k = g.key(adj);
            if (k >= 0 && !visited[k]) {
                DepthFirstSearch(g, adj, visited, qdfs);
            }
        }
    }

    /** Performs depth-first search starting in a vertex
     *
     * @param g Graph instance
     * @param vert vertex of graph g that will be the source of the search

     * @return a LinkedList with the vertices of depth-first search
     */
    public static <V, E> LinkedList<V> DepthFirstSearch(Graph<V, E> g, V vert) {

        if (g == null || vert == null || !g.validVertex(vert))
            return null;

        int n = g.numVertices();
        boolean[] visited = new boolean[n];
        LinkedList<V> qdfs = new LinkedList<>();

        DepthFirstSearch(g, vert, visited, qdfs);

        return qdfs;
    }

    /** Returns all paths from vOrig to vDest
     *
     * @param g       Graph instance
     * @param vOrig   Vertex that will be the source of the path
     * @param vDest   Vertex that will be the end of the path
     * @param visited set of discovered vertices
     * @param path    stack with vertices of the current path (the path is in reverse order)
     * @param paths   ArrayList with all the paths (in correct order)
     */
    private static <V, E> void allPaths(Graph<V, E> g, V vOrig, V vDest, boolean[] visited, LinkedList<V> path, ArrayList<LinkedList<V>> paths) {

        int kOrig = g.key(vOrig);
        int kDest = g.key(vDest);
        if (kOrig < 0 || kDest < 0) return;

        visited[kOrig] = true;
        path.addLast(vOrig);

        if (vOrig.equals(vDest)) {
            // Found a path: copy it (path is in correct order: origin -> ... -> dest)
            paths.add(new LinkedList<>(path));
        } else {
            for (V adj : g.adjVertices(vOrig)) {
                int kAdj = g.key(adj);
                if (kAdj >= 0 && !visited[kAdj]) {
                    allPaths(g, adj, vDest, visited, path, paths);
                }
            }
        }

        // backtrack
        path.removeLast();
        visited[kOrig] = false;
    }

    /** Returns all paths from vOrig to vDest
     *
     * @param g     Graph instance
     * @param vOrig information of the Vertex origin
     * @param vDest information of the Vertex destination
     * @return paths ArrayList with all paths from vOrig to vDest
     */
    public static <V, E> ArrayList<LinkedList<V>> allPaths(Graph<V, E> g, V vOrig, V vDest) {

        ArrayList<LinkedList<V>> paths = new ArrayList<>();

        if (g == null || vOrig == null || vDest == null) return paths;
        if (!g.validVertex(vOrig) || !g.validVertex(vDest)) return paths;

        int n = g.numVertices();
        boolean[] visited = new boolean[n];
        LinkedList<V> path = new LinkedList<>();

        allPaths(g, vOrig, vDest, visited, path, paths);

        return paths;
    }

    /**
     * Computes shortest-path distance from a source vertex to all reachable
     * vertices of a graph g with non-negative edge weights
     * This implementation uses Dijkstra's algorithm
     *
     * @param g        Graph instance
     * @param vOrig    Vertex that will be the source of the path
     * @param visited  set of previously visited vertices
     * @param pathKeys minimum path vertices keys
     * @param dist     minimum distances
     */
    private static <V, E> void shortestPathDijkstra(Graph<V, E> g, V vOrig,
                                                    Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                                    boolean[] visited, V [] pathKeys, E [] dist) {
        
       int n = g.numVertices();
        int startKey = g.key(vOrig);
        if (startKey < 0) return;

        // Initialize
        for (int i = 0; i < n; i++) {
            visited[i] = false;
            pathKeys[i] = null;
            dist[i] = null; // null means infinity/unreached
        }

        dist[startKey] = zero;

        // Main loop
        while (true) {
            // find unvisited vertex with minimal dist
            int u = -1;
            for (int i = 0; i < n; i++) {
                if (!visited[i] && dist[i] != null) {
                    if (u == -1 || ce.compare(dist[i], dist[u]) < 0) {
                        u = i;
                    }
                }
            }

            if (u == -1) break; // no more reachable vertices

            visited[u] = true;
            V uVert = g.vertex(u);

            // relax edges from u
            for (Edge<V, E> e : g.outgoingEdges(uVert)) {
                V v = e.getVDest();
                int kV = g.key(v);
                if (kV < 0 || visited[kV]) continue;

                E alt = sum.apply(dist[u], e.getWeight());
                if (dist[kV] == null || ce.compare(alt, dist[kV]) < 0) {
                    dist[kV] = alt;
                    pathKeys[kV] = uVert;
                }
            }
        }
    }

   
    /** Shortest-path between two vertices
     *
     * @param g graph
     * @param vOrig origin vertex
     * @param vDest destination vertex
     * @param ce comparator between elements of type E
     * @param sum sum two elements of type E
     * @param zero neutral element of the sum in elements of type E
     * @param shortPath returns the vertices which make the shortest path
     * @return if vertices exist in the graph and are connected, true, false otherwise
     */
    public static <V, E> E shortestPath(Graph<V, E> g, V vOrig, V vDest,
                                        Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                        LinkedList<V> shortPath) {

        if (g == null || vOrig == null || vDest == null || !g.validVertex(vOrig) || !g.validVertex(vDest))
            return null;

        int n = g.numVertices();
        boolean[] visited = new boolean[n];
        V[] pathKeys = (V[]) new Object[n];
        E[] dist = (E[]) new Object[n];

        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        int destKey = g.key(vDest);
        if (dist[destKey] == null) return null; // not reachable

        // reconstruct path
        if (shortPath != null) {
            shortPath.clear();
            getPath(g, vOrig, vDest, pathKeys, shortPath);
        }

        return dist[destKey];
    }

    /** Shortest-path between a vertex and all other vertices
     *
     * @param g graph
     * @param vOrig start vertex
     * @param ce comparator between elements of type E
     * @param sum sum two elements of type E
     * @param zero neutral element of the sum in elements of type E
     * @param paths returns all the minimum paths
     * @param dists returns the corresponding minimum distances
     * @return if vOrig exists in the graph true, false otherwise
     */
    public static <V, E> boolean shortestPaths(Graph<V, E> g, V vOrig,
                                               Comparator<E> ce, BinaryOperator<E> sum, E zero,
                                               ArrayList<LinkedList<V>> paths, ArrayList<E> dists) {

        if (g == null || vOrig == null || !g.validVertex(vOrig))
            return false;

        int n = g.numVertices();
        boolean[] visited = new boolean[n];
        V[] pathKeys = (V[]) new Object[n];
        E[] dist = (E[]) new Object[n];

        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        paths.clear();
        dists.clear();

        for (int i = 0; i < n; i++) {
            V v = g.vertex(i);
            if (dist[i] != null) {
                // build path from vOrig to v
                LinkedList<V> path = new LinkedList<>();
                getPath(g, vOrig, v, pathKeys, path);
                paths.add(path);
                dists.add(dist[i]);
            } else {
                // unreachable vertices are represented by empty path and null distance (skip or add nulls if desired)
            }
        }

        return true;
    }

    /**
     * Extracts from pathKeys the minimum path between voInf and vdInf
     * The path is constructed from the end to the beginning
     *
     * @param g        Graph instance
     * @param vOrig    information of the Vertex origin
     * @param vDest    information of the Vertex destination
     * @param pathKeys minimum path vertices keys
     * @param path     stack with the minimum path (correct order)
     */
    private static <V, E> void getPath(Graph<V, E> g, V vOrig, V vDest,
                                       V [] pathKeys, LinkedList<V> path) {

        path.clear();

        if (vOrig == null || vDest == null) return;
        if (!g.validVertex(vOrig) || !g.validVertex(vDest)) return;

        // Reconstruct by walking predecessors from vDest to vOrig
        V current = vDest;
        while (current != null) {
            path.addFirst(current);
            if (current.equals(vOrig)) break;
            int k = g.key(current);
            if (k < 0) break;
            current = pathKeys[k];
        }

        // If first vertex is not vOrig, then there is no valid path; clear path
        if (path.isEmpty() || !path.getFirst().equals(vOrig)) {
            path.clear();
        }
    }

    /** Calculates the minimum distance graph using Floyd-Warshall
     * 
     * @param g initial graph
     * @param ce comparator between elements of type E
     * @param sum sum two elements of type E
     * @return the minimum distance graph
     */
    public static <V,E> MatrixGraph <V,E> minDistGraph(Graph <V,E> g, Comparator<E> ce, BinaryOperator<E> sum) {
        
        if (g == null) return null;

        ArrayList<V> vs = g.vertices();
        int n = vs.size();

        // initialize matrix m with current edge weights (null means no edge / infinite)
        E[][] m = (E[][]) new Object[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    m[i][j] = null; // no self-loop weight (treat as zero only when needed)
                } else {
                    Edge<V, E> e = g.edge(vs.get(i), vs.get(j));
                    if (e != null) m[i][j] = e.getWeight();
                    else m[i][j] = null;
                }
            }
        }

        // Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (m[i][k] == null) continue;
                for (int j = 0; j < n; j++) {
                    if (m[k][j] == null) continue;
                    E via = sum.apply(m[i][k], m[k][j]);
                    if (m[i][j] == null || ce.compare(via, m[i][j]) < 0) {
                        m[i][j] = via;
                    }
                }
            }
        }

        // Build and return matrix graph with minimal distances
        return new MatrixGraph<>(g.isDirected(), vs, m);
    }

}
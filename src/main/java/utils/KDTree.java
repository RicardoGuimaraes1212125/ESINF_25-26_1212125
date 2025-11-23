package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import domain.Station;
import domain.StationByLat;
import domain.StationByLon;

/**
 * 2D KD-Tree over (latitude, longitude).
 * Each node stores a bucket of stations that share the same coordinates,
 * sorted by station name (ascending, case-insensitive).
 */

public class KDTree {

    //node structure for the KD-Tree
    private static class Node {
        double lat;
        double lon;
        List<Station> bucket = new ArrayList<>();
        Node left;
        Node right;
    }

    //root of the KD-Tree
    private Node root;
    //size of the KD-Tree (total number of stations)
    private int size;   
    //epsilon for floating-point comparisons
    private static final double EPS = 1e-8;

    public int size() {
        return size;
    }

    public long height() {
        return height(root);
    }

    //returns the height of the KD-Tree
    private long height(Node n) {
        if (n == null) return -1L;
        long hl = height(n.left);
        long hr = height(n.right);
        return 1L + (hl > hr ? hl : hr);
    }

    //returns all distinct bucket sizes in this KD-Tree.
    public Iterable<Integer> getDistinctBucketSizes() {
        Set<Integer> set = new TreeSet<>();
        collectBucketSizes(root, set);
        return set;
    }

    //collects all distinct bucket sizes in the KD-Tree
    private void collectBucketSizes(Node n, Set<Integer> acc) {
        if (n == null) return;
        if (!n.bucket.isEmpty()) acc.add(n.bucket.size());
        collectBucketSizes(n.left, acc);
        collectBucketSizes(n.right, acc);
    }

    
    //returns the station names stored in the bucket for the given coordinates, or null if none.
    public String[] getBucketFor(double lat, double lon) {
        Node n = findNode(root, lat, lon, 0);
        if (n == null) return new String[0];
        String[] res = new String[n.bucket.size()];
        for (int i = 0; i < n.bucket.size(); i++) {
            res[i] = n.bucket.get(i).getStationName();
        }
        return res;
    }

    //finds the node containing the station with the given coordinates, or null if not found
    private Node findNode(Node n, double lat, double lon, int depth) {
        if (n == null) return null;
        if (equalsCoord(n.lat, n.lon, lat, lon)) return n;

        int axis = depth % 2;
        if (axis == 0) {
            //compare by latitude (tie-break by lon)
            if (lat < n.lat || (Math.abs(lat - n.lat) <= EPS && lon < n.lon))
                return findNode(n.left, lat, lon, depth + 1);
            else
                return findNode(n.right, lat, lon, depth + 1);
        } else {
            //compare by longitude (tie-break by lat)
            if (lon < n.lon || (Math.abs(lon - n.lon) <= EPS && lat < n.lat))
                return findNode(n.left, lat, lon, depth + 1);
            else
                return findNode(n.right, lat, lon, depth + 1);
        }
    }

    /**
     * Bulk-build of a balanced KD-Tree from:
     *  - a list sorted by latitude (StationByLat)
     *  - a list sorted by longitude (StationByLon)
     *
     * Both lists must contain the same stations.
     */
    public static KDTree buildBalanced(List<StationByLat> byLat, List<StationByLon> byLon) {
        KDTree tree = new KDTree();
        if (byLat == null || byLon == null)
            throw new IllegalArgumentException("byLat and byLon must be non-null");
        if (byLat.size() != byLon.size())
            throw new IllegalArgumentException("byLat and byLon must have the same size");

        tree.root = buildRec(byLat, byLon, 0);
        tree.size = computeSize(tree.root);
        return tree;
    }

    //computes the total number of stations in the KD-Tree
    private static int computeSize(Node n) {
        if (n == null) return 0;
        int total = n.bucket.size();
        total += computeSize(n.left);
        total += computeSize(n.right);
        return total;
    }

    //builds the KD-Tree recursively
    private static Node buildRec(List<StationByLat> byLat,
                                 List<StationByLon> byLon,
                                 int depth) {

        if (byLat == null || byLat.isEmpty())
            return null;

        int axis = depth % 2; // 0 = latitude, 1 = longitude
        Node node = new Node();

        if (axis == 0) {
            //split using latitude as primary key
            int mid = byLat.size() / 2;
            Station pivot = byLat.get(mid).s;
            double pivotLat = pivot.getLatitude();
            double pivotLon = pivot.getLongitude();

            List<StationByLat> leftLat  = new ArrayList<>();
            List<StationByLat> rightLat = new ArrayList<>();
            List<StationByLon> leftLon  = new ArrayList<>();
            List<StationByLon> rightLon = new ArrayList<>();

            //build bucket + split byLat
            for (StationByLat sl : byLat) {
                Station st = sl.s;
                if (equalsCoord(st.getLatitude(), st.getLongitude(), pivotLat, pivotLon)) {
                    addToBucketUnique(node.bucket, st);
                } else if (lessByLat(st, pivotLat, pivotLon)) {
                    leftLat.add(sl);
                } else {
                    rightLat.add(sl);
                }
            }

            //split byLon consistently
            for (StationByLon so : byLon) {
                Station st = so.s;
                if (equalsCoord(st.getLatitude(), st.getLongitude(), pivotLat, pivotLon)) {
                    addToBucketUnique(node.bucket, st);
                } else if (lessByLat(st, pivotLat, pivotLon)) {
                    leftLon.add(so);
                } else {
                    rightLon.add(so);
                }
            }

            //sort bucket by name ascending
            node.bucket.sort(Comparator.comparing(Station::getStationName, String.CASE_INSENSITIVE_ORDER));
            node.lat = pivotLat;
            node.lon = pivotLon;

            node.left = buildRec(leftLat,  leftLon,  depth + 1);
            node.right = buildRec(rightLat, rightLon, depth + 1);

        } else {
            //split using longitude as primary key
            int mid = byLon.size() / 2;
            Station pivot = byLon.get(mid).s;
            double pivotLat = pivot.getLatitude();
            double pivotLon = pivot.getLongitude();

            List<StationByLat> leftLat  = new ArrayList<>();
            List<StationByLat> rightLat = new ArrayList<>();
            List<StationByLon> leftLon  = new ArrayList<>();
            List<StationByLon> rightLon = new ArrayList<>();

            //build bucket + split byLon
            for (StationByLon so : byLon) {
                Station st = so.s;
                if (equalsCoord(st.getLatitude(), st.getLongitude(), pivotLat, pivotLon)) {
                    addToBucketUnique(node.bucket, st);
                } else if (lessByLon(st, pivotLat, pivotLon)) {
                    leftLon.add(so);
                } else {
                    rightLon.add(so);
                }
            }

            //split byLat consistent
            for (StationByLat sl : byLat) {
                Station st = sl.s;
                if (equalsCoord(st.getLatitude(), st.getLongitude(), pivotLat, pivotLon)) {
                    addToBucketUnique(node.bucket, st);
                } else if (lessByLon(st, pivotLat, pivotLon)) {
                    leftLat.add(sl);
                } else {
                    rightLat.add(sl);
                }
            }

            //sort bucket by name ascending
            node.bucket.sort(Comparator.comparing(Station::getStationName, String.CASE_INSENSITIVE_ORDER));
            node.lat = pivotLat;
            node.lon = pivotLon;

            node.left = buildRec(leftLat,  leftLon,  depth + 1);
            node.right = buildRec(rightLat, rightLon, depth + 1);
        }

        return node;
    }

    //compare coordinates with epsilon
    private static boolean equalsCoord(double lat1, double lon1, double lat2, double lon2) {
        return Math.abs(lat1 - lat2) <= EPS && Math.abs(lon1 - lon2) <= EPS;
    }

    //order by latitude, tie-break by longitude
    private static boolean lessByLat(Station s, double pivotLat, double pivotLon) {
        if (s.getLatitude() < pivotLat - EPS) return true;
        if (s.getLatitude() > pivotLat + EPS) return false;
        return s.getLongitude() < pivotLon;
    }

    //order by longitude, tie-break by latitude
    private static boolean lessByLon(Station s, double pivotLat, double pivotLon) {
        if (s.getLongitude() < pivotLon - EPS) return true;
        if (s.getLongitude() > pivotLon + EPS) return false;
        return s.getLatitude() < pivotLat;
    }

    //adds a station to the bucket only if a station with the same name
    private static void addToBucketUnique(List<Station> bucket, Station s) {
        for (Station st : bucket) {
            if (st == s) return;
        }
        bucket.add(s);
    }

    //range search: returns all stations within the given bounding box
    public List<Station> rangeSearch(double latMin, double latMax, double lonMin, double lonMax) {
        List<Station> result = new ArrayList<>();
        rangeSearchRec(root, latMin, latMax, lonMin, lonMax, 0, result);
        return result;
    }

    //recursive range search
    private void rangeSearchRec(Node n, double latMin, double latMax,
                                double lonMin, double lonMax,
                                int depth, List<Station> acc) {

        if (n == null) return;

        //check if this node's coordinate is inside the box
        boolean inside =
                n.lat >= latMin - EPS && n.lat <= latMax + EPS &&
                n.lon >= lonMin - EPS && n.lon <= lonMax + EPS;

        if (inside) acc.addAll(n.bucket);

        int axis = depth % 2;

        if (axis == 0) {
            if (latMin <= n.lat + EPS) rangeSearchRec(n.left, latMin, latMax, lonMin, lonMax, depth + 1, acc);
            if (latMax >= n.lat - EPS) rangeSearchRec(n.right, latMin, latMax, lonMin, lonMax, depth + 1, acc);
        } else {
            if (lonMin <= n.lon + EPS) rangeSearchRec(n.left, latMin, latMax, lonMin, lonMax, depth + 1, acc);
            if (lonMax >= n.lon - EPS) rangeSearchRec(n.right, latMin, latMax, lonMin, lonMax, depth + 1, acc);
        }
    }

    //nearest neighbor search: returns the station closest to the given coordinates
    public Station nearest(double lat, double lon) {
        return nearestRec(root, lat, lon, 0, null, Double.MAX_VALUE);
    }

    private Station nearestRec(Node n, double lat, double lon,
                            int depth, Station best, double bestDist) {

        if (n == null) return best;

        //distance to the representative coordinate of this bucket
        double d = dist(lat, lon, n.lat, n.lon);
        if (d < bestDist) {
            if (!n.bucket.isEmpty()) {
                best = n.bucket.get(0);
                bestDist = d;
            }
        }

        int axis = depth % 2;

        Node primary, secondary;

        if (axis == 0) {
            if (lat < n.lat) {
                primary = n.left;
                secondary = n.right;
            } else {
                primary = n.right;
                secondary = n.left;
            }
        } else {
            if (lon < n.lon) {
                primary = n.left;
                secondary = n.right;
            } else {
                primary = n.right;
                secondary = n.left;
            }
        }

        //search primary branch first
        best = nearestRec(primary, lat, lon, depth + 1, best, bestDist);
        if (best != null) bestDist = dist(lat, lon, best.getLatitude(), best.getLongitude());

        //check whether secondary branch can still contain a closer station
        double axisDist = axis == 0 ? Math.abs(lat - n.lat) : Math.abs(lon - n.lon);

        if (axisDist < bestDist) {
            best = nearestRec(secondary, lat, lon, depth + 1, best, bestDist);
        }

        return best;
    }

    //calculates the Euclidean distance between two points (lat1, lon1) and (lat2, lon2)
    private double dist(double a1, double b1, double a2, double b2) {
        double dx = a1 - a2;
        double dy = b1 - b2;
        return Math.sqrt(dx*dx + dy*dy);
    }

}

package services;


import domain.StationByLat;
import domain.StationByLon;
import utils.AVL;
import utils.KDTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for building and querying the 2D KD-Tree (US07).
 * The KD-Tree is built using the AVL indexes from US06:
 *  - AVL<StationByLat> (sorted by latitude)
 *  - AVL<StationByLon> (sorted by longitude)
 */

public class KDTreeService {

    private KDTree kdTree;

    /**
     * Bulk-build the KD-Tree using the AVLs from StationIndexService (US06).
     */
    public void buildFromIndex(StationIndexService indexService) {
        
        //
        AVL<StationByLat> latAVL = indexService.getLatTree();
        AVL<StationByLon> lonAVL = indexService.getLonTree();

        List<StationByLat> byLat = new ArrayList<StationByLat>();
        for (StationByLat s : latAVL.inOrder()) {
            byLat.add(s);
        }

        List<StationByLon> byLon = new ArrayList<StationByLon>();
        for (StationByLon s : lonAVL.inOrder()) {
            byLon.add(s);
        }

        kdTree = KDTree.buildBalanced(byLat, byLon);
    }

    public KDTree getKDTree() {
        return kdTree;
    }

    public int getSize() {
        if (kdTree == null) return 0;
        return kdTree.size();
    }

    public long getHeight() {
        if (kdTree == null) return -1L;
        return kdTree.height();
    }

    public Iterable<Integer> getDistinctBucketSizes() {
        if (kdTree == null) return new ArrayList<Integer>();
        return kdTree.getDistinctBucketSizes();
    }

    public String[] getBucketFor(double lat, double lon) {
        if (kdTree == null) return null;
        return kdTree.getBucketFor(lat, lon);
    }
}

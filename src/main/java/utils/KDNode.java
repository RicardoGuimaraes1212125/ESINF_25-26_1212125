package utils;

import java.util.ArrayList;
import java.util.List;

import domain.Station;

public class KDNode {

    public Station station;       
    public List<Station> duplicates;
    public KDNode left;
    public KDNode right;

    public KDNode(Station station) {
        this.station = station;
        this.duplicates = new ArrayList<>();
        this.left = null;
        this.right = null;
    }

    public int bucketSize() {
        return 1 + duplicates.size();
    }
}


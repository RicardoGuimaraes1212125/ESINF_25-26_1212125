package controllers;

import domain.Station;
import services.KDTreeService;
import services.StationIndexService;
import utils.KDTree;


public class KDTreeController {

    private final KDTreeService kdService;
    private final StationIndexService indexService;

    public KDTreeController(StationIndexService indexService) {
        this.indexService = indexService;
        this.kdService = new KDTreeService();
    }

    public void buildKDTree() {
        kdService.buildFromIndex(indexService);
    }

    public KDTree getTree() {
        return kdService.getKDTree();
    }

    public int getSize() {
        return kdService.getSize();
    }

    public long getHeight() {
        return kdService.getHeight();
    }

    public Iterable<Integer> getBucketSizes() {
        return kdService.getDistinctBucketSizes();
    }

}

package utils;

import domain.Station;
import domain.StationByLat;
import domain.StationByLon;
import org.junit.*;
import java.util.*;

import static org.junit.Assert.*;

public class KDTreeUnitTest {

    @Test
    public void testBuildBalancedWithDuplicatesBuckets() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();

        // build 3 stations with identical coords, different names
        Station a = new Station("B", "PT", "('Europe/Lisbon',)", "WET/GMT", 38.713, -9.13, false, true, false);
        Station b = new Station("A", "PT", "('Europe/Lisbon',)", "WET/GMT", 38.713, -9.13, false, false, false);
        Station c = new Station("C", "PT", "('Europe/Lisbon',)", "WET/GMT", 38.713, -9.13, false, false, false);

        byLat.add(new StationByLat(a));
        byLat.add(new StationByLat(b));
        byLat.add(new StationByLat(c));
        byLon.add(new StationByLon(a));
        byLon.add(new StationByLon(b));
        byLon.add(new StationByLon(c));

        KDTree tree = KDTree.buildBalanced(byLat, byLon);
        assertEquals(3, tree.size());

        String[] bucket = tree.getBucketFor(38.713, -9.13);
        assertNotNull(bucket);
        //since we sort by name ascending, expect A, B, C
        assertEquals("A", bucket[0]);
        assertEquals("B", bucket[1]);
        assertEquals("C", bucket[2]);
    }


    @Test
    public void testGetBucketForEmptyReturnsEmptyArray() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();
        Station s = new Station("S","PT","tz","WET/GMT",10,10,false,false,false);
        byLat.add(new StationByLat(s));
        byLon.add(new StationByLon(s));

        KDTree tree = KDTree.buildBalanced(byLat, byLon);
        String[] bucket = tree.getBucketFor(0.0, 0.0);
        assertNotNull(bucket);
        assertEquals(0, bucket.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildBalancedThrowsOnMismatchedLists() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();
        byLat.add(new StationByLat(new Station("A","PT","tz","WET/GMT",1,1,false,false,false)));
        //byLon is empty -> mismatch
        KDTree.buildBalanced(byLat, byLon);
    }

    @Test
    public void testDuplicateStationNamesPreserved() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();

        //two distinct Station objects with same name and coords
        Station s1 = new Station("DUP","PT","tz","WET/GMT",50.0,5.0,false,false,false);
        Station s2 = new Station("DUP","PT","tz","WET/GMT",50.0,5.0,false,false,false);

        byLat.add(new StationByLat(s1));
        byLat.add(new StationByLat(s2));
        byLon.add(new StationByLon(s1));
        byLon.add(new StationByLon(s2));

        KDTree tree = KDTree.buildBalanced(byLat, byLon);
        assertEquals(2, tree.size());
        String[] bucket = tree.getBucketFor(50.0, 5.0);
        assertEquals(2, bucket.length);
        //both have same name "DUP"
        assertEquals("DUP", bucket[0]);
        assertEquals("DUP", bucket[1]);
    }

    @Test
    public void testSizeWithDistinctCoordinates() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();

        Station s1 = new Station("A","PT","tz","WET",10,10,false,false,false);
        Station s2 = new Station("B","PT","tz","WET",20,20,false,false,false);
        Station s3 = new Station("C","PT","tz","WET",30,30,false,false,false);

        byLat.add(new StationByLat(s1));
        byLat.add(new StationByLat(s2));
        byLat.add(new StationByLat(s3));

        byLon.add(new StationByLon(s1));
        byLon.add(new StationByLon(s2));
        byLon.add(new StationByLon(s3));

        KDTree tree = KDTree.buildBalanced(byLat, byLon);

        assertEquals(3, tree.size());
    }

    @Test
    public void testHeightSingleNode() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();

        Station s = new Station("S","PT","tz","WET",5,5,false,false,false);
        byLat.add(new StationByLat(s));
        byLon.add(new StationByLon(s));

        KDTree tree = KDTree.buildBalanced(byLat, byLon);

        assertEquals(0, tree.height());
    }

    @Test
    public void testDistinctBucketSizes() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();

        Station a = new Station("A","PT","tz","WET",1,1,false,false,false);
        Station b = new Station("B","PT","tz","WET",1,1,false,false,false);
        Station c = new Station("C","PT","tz","WET",2,2,false,false,false);

        byLat.add(new StationByLat(a));
        byLat.add(new StationByLat(b));
        byLat.add(new StationByLat(c));

        byLon.add(new StationByLon(a));
        byLon.add(new StationByLon(b));
        byLon.add(new StationByLon(c));

        KDTree tree = KDTree.buildBalanced(byLat, byLon);

        Set<Integer> sizes = new HashSet<>();
        for (int s : tree.getDistinctBucketSizes()) {
            sizes.add(s);
        }

        assertTrue(sizes.contains(1));
        assertTrue(sizes.contains(2));
    }

    @Test
    public void testBucketOrderIsAlwaysSortedByName() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();

        Station c = new Station("C","PT","tz","WET",9,9,false,false,false);
        Station a = new Station("A","PT","tz","WET",9,9,false,false,false);
        Station b = new Station("B","PT","tz","WET",9,9,false,false,false);

        byLat.add(new StationByLat(c));
        byLat.add(new StationByLat(a));
        byLat.add(new StationByLat(b));

        byLon.add(new StationByLon(c));
        byLon.add(new StationByLon(a));
        byLon.add(new StationByLon(b));

        KDTree tree = KDTree.buildBalanced(byLat, byLon);

        String[] bucket = tree.getBucketFor(9,9);

        assertArrayEquals(new String[]{"A","B","C"}, bucket);
    }

    @Test
    public void testBuildBalancedWithEmptyLists() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();

        KDTree tree = KDTree.buildBalanced(byLat, byLon);

        assertEquals(0, tree.size());
        assertEquals(-1, tree.height());
    }
}


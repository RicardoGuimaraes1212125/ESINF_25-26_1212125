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
        // since we sort by name ascending, expect A, B, C
        assertEquals("A", bucket[0]);
        assertEquals("B", bucket[1]);
        assertEquals("C", bucket[2]);
    }

    @Test
    public void testRangeSearchEmptyWhenNoMatch() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();
        byLat.add(new StationByLat(new Station("X","PT","tz","WET/GMT",10,10,false,false,false)));
        byLon.add(new StationByLon(new Station("X","PT","tz","WET/GMT",10,10,false,false,false)));

        KDTree tree = KDTree.buildBalanced(byLat, byLon);
        List<Station> res = tree.rangeSearch(20,21,20,21);
        assertTrue(res.isEmpty());
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
        // byLon is empty -> mismatch
        KDTree.buildBalanced(byLat, byLon);
    }

    @Test
    public void testDuplicateStationNamesPreserved() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();

        // two distinct Station objects with same name and coords
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
        // both have same name "DUP"
        assertEquals("DUP", bucket[0]);
        assertEquals("DUP", bucket[1]);
    }

    @Test
    public void testRangeSearchFindsStation() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();
        Station s = new Station("R","PT","tz","WET/GMT",15.0,15.0,false,false,false);
        byLat.add(new StationByLat(s));
        byLon.add(new StationByLon(s));

        KDTree tree = KDTree.buildBalanced(byLat, byLon);
        List<Station> res = tree.rangeSearch(14.0,16.0,14.0,16.0);
        assertEquals(1, res.size());
        assertEquals("R", res.get(0).getStationName());
    }

    @Test
    public void testNearestReturnsClosestStation() {
        List<StationByLat> byLat = new ArrayList<>();
        List<StationByLon> byLon = new ArrayList<>();
        Station s1 = new Station("Near","PT","tz","WET/GMT",0.0,0.0,false,false,false);
        Station s2 = new Station("Far","PT","tz","WET/GMT",10.0,10.0,false,false,false);
        byLat.add(new StationByLat(s1));
        byLat.add(new StationByLat(s2));
        byLon.add(new StationByLon(s1));
        byLon.add(new StationByLon(s2));

        KDTree tree = KDTree.buildBalanced(byLat, byLon);
        Station nn = tree.nearest(0.1, 0.1);
        assertNotNull(nn);
        assertEquals("Near", nn.getStationName());
    }
}


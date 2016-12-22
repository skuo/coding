package com.coding.inauth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.coding.inauth.model.Location;

public class RegionTest {

    @Test
    public void testInUsa() {
        Location la = new Location(34.0522f, -118.2437f);
        assertTrue(Region.inUSA(la));
        Location osaka = new Location(34.6937f, 135.5022f);
        assertFalse(Region.inUSA(osaka));
        Location boston = new Location(42.3747f, -71.0595f);
        assertTrue(Region.inUSA(boston));
        Location chicago = new Location(41.8777f, -87.6489f);
        assertTrue(Region.inUSA(chicago));
        Location miami = new Location(25.7702f, -80.1782f);
        assertTrue(Region.inUSA(miami));
        Location nassau = new Location(25.0656f, -77.3547f);
        assertFalse(Region.inUSA(nassau));
    }
}

package com.coding.inauth.service.data.spring;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.coding.inauth.model.Location;
import com.coding.inauth.service.data.DataServiceException;
import com.coding.inauth.service.data.LocationDs;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:./src/test/resources/in-auth-test-spring.xml"})
public class JdbcLocationDsTest {
   
    @Autowired
    private LocationDs locationDs;

    @Test
    public void testStatDs() throws DataServiceException{
        // save a list of locations
        List<Location> locations = new LinkedList<>();
        Location l1 = new Location(34.0522f, -118.2437f); // LA
        locations.add(l1);
        Location l2 = new Location(37.7749f, -122.4194f); // SF
        locations.add(l2);
        boolean status = locationDs.saveLocations(locations);
        assertTrue(status);
        // retrieve location one by one
        Location dbLoc = locationDs.find(34.0522f, -118.2437f);
        assertEquals(dbLoc, l1);
        dbLoc = locationDs.find(37.7749f, -122.4194f);
        assertEquals(dbLoc, l2);
        // Save another
        Location l3 = new Location(32.7157f, -117.1611f);
        locationDs.saveLocation(l3);
        // get all
        List<Location> dbLocs = locationDs.getLocations();
        assertEquals(3, dbLocs.size());
        int asserted = 0;
        for (Location l : dbLocs) {
            if (34.0522f == l.getLatitude()) {
                assertEquals(l, l1);
                asserted++;
            } else if (37.7749f == l.getLatitude()) {
                assertEquals(l, l2);
                asserted++;
            } else if (32.7157f == l.getLatitude()) {
                assertEquals(l, l3);
                asserted++;
            }
        }
        assertEquals(3, asserted);
    }
}


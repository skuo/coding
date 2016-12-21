package com.coding.inauth;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coding.inauth.model.Location;
import com.coding.inauth.service.data.DataServiceException;
import com.coding.inauth.service.data.LocationDs;

@Component
public class InAuth {
    private static final Log log = LogFactory.getLog(InAuth.class);

    @Autowired
    LocationDs locationDs;

    // use Random for now.
    Random random = new Random();

    private Location randomLocation() {
        // The latitude must be a number between -90 and 90 and the longitude
        // between -180 and 180.
        // random generates between 0.0 and 1.0
        float latitude = random.nextFloat() * 180f - 90f;
        float longitude = random.nextFloat() * 360f - 180f;
        return new Location(latitude, longitude);
    }

    public boolean initialLoad() throws DataServiceException {
        // generate a list of 10,000 random locations
        List<Location> locations = new LinkedList<>();
        for (int i = 0; i < 10000; i++) {
            Location loc = randomLocation();
            if (loc.validate(loc))
                locations.add(loc);
            else
                log.error("Invalid: " + loc.toString());
        }
        // save
        boolean result = locationDs.saveLocations(locations);
        if (result)
            log.info("Successfully saved 10,000 random locations");
        return result;
    }

    public static void main(String[] args) throws DataServiceException {
        InAuth inAuth = new InAuth();
        inAuth.initialLoad();
        
        System.out.println(GeoDistanceSource.distance(32.9697, -96.80322, 29.46786, -98.53506, "M") + " Miles\n");
        System.out.println(GeoDistanceSource.distance(32.9697, -96.80322, 29.46786, -98.53506, "K") + " Kilometers\n");
        System.out.println(GeoDistanceSource.distance(32.9697, -96.80322, 29.46786, -98.53506, "N") + " Nautical Miles\n");

        // LA to SF
        System.out.println("=== LA to SF ===");
        System.out.println(GeoDistanceSource.distance(34.0522f, -118.2437f, 37.7749, -122.4194, "M") + " Miles\n");
    }
}

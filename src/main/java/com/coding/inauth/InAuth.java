package com.coding.inauth;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collection;
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

    public static String HEADER = "latitude,longitude,"
            + "Tokyo In Range, Tokyo Distance,"
            + "Sydney In Range, Sydney Distance,"
            + "Riyadh In Range, Riyadh Distance,"
            + "Zurich In Range, Zurich Distance,"
            + "Reykjavik In Range, Reykjavik Distance,"
            + "Mexico City In Range, Mexico City Distance,"
            + "Lima In Range, Lima Distance,";
    @Autowired
    LocationDs locationDs;

    // use Random for now.
    private Random random = new Random();
    
    // locations for target cities
    private Location tokyo = new Location(35.6895f, 139.6917f);
    private Location sydney = new Location(-33.8688f, 151.2093f);
    private Location riyadh = new Location(24.7136f, 46.6753f);
    private Location zurich = new Location(47.3769f, 8.5417f);
    private Location reykjavik = new Location(64.1265f, -21.8174f);
    private Location mexicoCity = new Location(19.42847f, -99.12766f);
    private Location lima = new Location(-11.93284f, -76.641271f);
    
    private double range = 500;
    private String outCsvFile = "inAuthSolution.csv";

    private Location randomLocation() {
        // The latitude must be a number between -90 and 90 and the longitude
        // between -180 and 180.
        // Latitude:  + (North), - (South)
        // Longitude: + (East),  - (West)
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

    public void calcAllLocations(Collection<Location> locations) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outCsvFile))) {
            // Write header
            bw.write(HEADER + "\n");
            // loop and write the solution
            for (Location loc : locations) {
                String line = calc(loc);
                bw.write(line + "\n");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    
    public String calc(Location loc1) {
        StringBuilder sb = new StringBuilder(loc1.getLatitude() + "," + loc1.getLongitude() + ",");
        // Determine whether the location is in USA
        
        // Loop through the 7 target cities
        sb.append(inRangeInMiles(loc1, tokyo, range));
        sb.append(inRangeInMiles(loc1, sydney, range));
        sb.append(inRangeInMiles(loc1, riyadh, range));
        sb.append(inRangeInMiles(loc1, zurich, range));
        sb.append(inRangeInMiles(loc1, reykjavik, range));
        sb.append(inRangeInMiles(loc1, mexicoCity, range));
        sb.append(inRangeInMiles(loc1, lima, range));
        return sb.toString();
    }
    
    private String inRangeInMiles(Location loc1, Location targetLoc, double range) {
        StringBuilder sb = new StringBuilder();
        double dist = GeoDistanceSource.distance(loc1.getLatitude(), loc1.getLongitude(), targetLoc.getLatitude(),
                targetLoc.getLongitude(), "M");
        // is location in range
        if (dist > range)
            sb.append("N");
        else
            sb.append("Y");
        // append dist
        sb.append("," + String.format("%.0f", dist) + ",");
        return sb.toString();
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

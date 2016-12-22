package com.coding.inauth;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.coding.inauth.model.Location;

public class InAuthTest {

    private InAuth inAuth = new InAuth();
    private Location la = new Location(34.0522f, -118.2437f);
    private Location osaka = new Location(34.6937f, 135.5022f);
    private String laLine = "34.0522,-118.2437,N,5477,N,7502,N,8257,N,5917,N,4311,N,1548,N,4189,";
    private String osakaLine = "34.6937,135.5022,Y,246,N,4845,N,5205,N,5885,N,5497,N,7257,N,9881,"; 
    
    @Test
    public void testCalc() {
        // test la
        String str = inAuth.calc(la);
        assertEquals(laLine, str);
        // test osaka
        str = inAuth.calc(osaka);
        assertEquals(osakaLine, str);
    }
    
    @Test
    public void testCalcAllLocations() {
        List<Location> locations = new LinkedList<>();
        locations.add(la);
        locations.add(osaka);
        
        inAuth.calcAllLocations(locations);
        int asserted = 0, numOfLines = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("inAuthSolution.csv"))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (numOfLines == 0) {
                    assertEquals(line, InAuth.HEADER);
                    asserted++;
                } else {
                    if (line.contains("34.0522,-118.2437")) {
                        assertEquals(laLine, line);
                        asserted++;
                    } else if (line.contains("34.6937,135.5022")) {
                        assertEquals(osakaLine, line);
                        asserted++;
                    }
                }
                numOfLines++;
            }
            assertEquals(3, numOfLines);
            assertEquals(3, asserted);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

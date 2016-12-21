package com.coding.inauth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.coding.inauth.model.Location;

public class InAuthTest {

    private Location la = new Location(34.0522f, -118.2437f);

    @Test
    public void testCalc() {
        InAuth inAuth = new InAuth();
        // test la
        String str = inAuth.calc(la);
        assertEquals(
                "34.0522,-118.2437,N,5477,N,7502,N,8257,N,5917,N,4311,N,1548,N,4189,",
                str);
    }
}

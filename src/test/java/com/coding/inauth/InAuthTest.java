package com.coding.inauth;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.coding.inauth.model.Location;

public class InAuthTest {

    private Location la = new Location(34.0522f, -118.2437f);
    private Location osaka = new Location(34.6937f, 135.5022f);

    @Test
    public void testCalc() {
        InAuth inAuth = new InAuth();
        // test la
        String str = inAuth.calc(la);
        assertEquals(
                "34.0522,-118.2437,N,5477,N,7502,N,8257,N,5917,N,4311,N,1548,N,4189,",
                str);
        // test osaka
        str = inAuth.calc(osaka);
        assertEquals(
                "34.6937,135.5022,Y,246,N,4845,N,5205,N,5885,N,5497,N,7257,N,9881,",
                str);
    }
}

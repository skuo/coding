package com.inauth.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.inauth.model.Bid;
import com.inauth.model.BidStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:./src/test/resources/in-auth-test-spring.xml"})
public class BidControllerTest {

    @Autowired
    BidController bidController;

    @Test
    public void testBidController() throws SQLException, IOException {
        String sourceId = "sourceId";
        String source = "wp";
        float b = 1.2345f;
        Timestamp ts = new Timestamp(new Date().getTime());
        Bid bid = new Bid(sourceId, source, b, ts);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // insert a bid
        BidStatus bidStatus = bidController.putBid(sourceId, source, bid, response);
        assertEquals(BidStatus.SUCCESS,bidStatus.getStatus());
        // get the inserted bid
        Bid bid2 = bidController.getBid(sourceId, source, request, response);
        assertEquals(bid, bid2);
        // update bid
        bid.setBid(2.3456f);
        bidStatus = bidController.putBid(sourceId, source, bid, response);
        assertEquals(BidStatus.SUCCESS,bidStatus.getStatus());
        // get updated bid
        bid2 = bidController.getBid(sourceId, source, request, response);
        assertEquals(bid, bid2);        

    }

}

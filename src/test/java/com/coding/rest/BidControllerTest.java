package com.coding.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.coding.entity.Bid;
import com.coding.repository.BidRepository;

@RunWith(SpringRunner.class)
@SpringBootTest//(webEnvironment=WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase

public class BidControllerTest {
    @Autowired
    private BidRepository bidRepository;

    @Autowired
    BidController bidController;

    @After
    public void teardown() {
        // remove all bids
        bidRepository.deleteAll();
        assertTrue(0L == bidRepository.count());
    }
    
    @Test
    public void testBidController() throws SQLException, IOException {
        String sourceId = "sourceId";
        String source = "wp";
        Bid bid = new Bid();
        bid.setSourceId("sourceId");
        bid.setSource("source");
        bid.setBid(new BigDecimal("1.2345"));
        bid.setUpdatedAt(new Timestamp(new Date().getTime()));
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // insert a bid
        boolean status = bidController.putBid(sourceId, source, bid, response);
        assertTrue(status);
        // get the inserted bid
        Bid bid2 = bidController.getBid(sourceId, source, request, response);
        assertEquals(bid, bid2);
        // update bid
        bid.setBid(new BigDecimal("2.3456"));
        status = bidController.putBid(sourceId, source, bid, response);
        assertTrue(status);
        // get updated bid
        bid2 = bidController.getBid(sourceId, source, request, response);
        assertEquals(bid, bid2);        
    }

}

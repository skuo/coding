package com.coding.service;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
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
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class BidServiceTest {
    @Autowired
    private BidRepository bidRepository;
    
    @Autowired
    private BidService bidService;
    
    @Before
    public void setup() throws ParseException{
    }   
    
    @After
    public void teardown() {
        // remove all bids
        bidRepository.deleteAll();
        assertTrue(0L == bidRepository.count());
    }
    
    @Test
    public void testService() throws Exception{
        // first save Bids
        Bid bid1 = new Bid();
        bid1.setSourceId("sourceId");
        bid1.setSource("wp");
        bid1.setBid(new BigDecimal("1.2345"));
        bid1.setUpdatedAt(new Timestamp(new Date().getTime()));
        assertTrue(bidService.saveBid(bid1));

        Bid bid2 = new Bid();
        bid2.setSourceId("sourceId");
        bid2.setSource("wp2");
        bid2.setBid(new BigDecimal("2.3456"));
        bid2.setUpdatedAt(new Timestamp(new Date().getTime()));
        assertTrue(bidService.saveBid(bid2));

        // get a bid by sourceIs and source.  dbBid1 and dbBid2 are managed entities by JPA
        Bid dbBid1 = bidService.getBid("sourceId", "wp");
        assertTrue(dbBid1.getBid().compareTo(new BigDecimal("1.2345")) == 0);
        Bid dbBid2 = bidService.getBid("sourceId", "wp2");
        assertTrue(dbBid2.getBid().compareTo(new BigDecimal("2.3456")) == 0);
        // change dbBids. saveBid() actually updates
        dbBid1.setBid(new BigDecimal("12.3456"));
        assertTrue(bidService.saveBid(dbBid1));
        Bid bid = bidService.getBid("sourceId", "wp");
        assertTrue(bid.getBid().compareTo(new BigDecimal("12.3456")) == 0);
        dbBid2.setBid(new BigDecimal("23.4567"));
        assertTrue(bidService.saveBid(dbBid2));
        bid = bidService.getBid("sourceId", "wp2");
        assertTrue(bid.getBid().compareTo(new BigDecimal("23.4567")) == 0);
    }

}

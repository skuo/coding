package com.coding.service;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.coding.dto.BidDto;
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
        BidDto bidDto1 = new BidDto();
        bidDto1.setSourceId("sourceId");
        bidDto1.setSource("wp");
        bidDto1.setBid(new BigDecimal("1.2345"));
        assertTrue(bidService.saveBid(bidDto1));

        BidDto bidDto2 = new BidDto();
        bidDto2.setSourceId("sourceId");
        bidDto2.setSource("wp2");
        bidDto2.setBid(new BigDecimal("2.3456"));
        assertTrue(bidService.saveBid(bidDto2));

        // get a bid by sourceIs and source.  dbBidDtos1 and dbBidDtos2 are managed entities by JPA
        List<BidDto> dbBidDtos1 = bidService.getBids("sourceId", "wp");
        assertTrue(dbBidDtos1.get(0).getBid().compareTo(new BigDecimal("1.2345")) == 0);
        // change dbBids. saveBid() actually updates
        BidDto dbBid1 = dbBidDtos1.get(0);
        dbBid1.setBid(new BigDecimal("12.3456"));
        assertTrue(bidService.saveBid(dbBid1));
        dbBidDtos1 = bidService.getBids("sourceId", "wp");
        assertTrue(dbBidDtos1.get(0).getBid().compareTo(new BigDecimal("12.3456")) == 0);
        //
        List<BidDto> dbBidDtos2 = bidService.getBids("sourceId", "wp2");
        assertTrue(dbBidDtos2.get(0).getBid().compareTo(new BigDecimal("2.3456")) == 0);
        BidDto dbBid2 = dbBidDtos2.get(0); 
        dbBid2.setBid(new BigDecimal("23.4567"));
        assertTrue(bidService.saveBid(dbBid2));
        dbBidDtos2 = bidService.getBids("sourceId", "wp2");
        assertTrue(dbBidDtos2.get(0).getBid().compareTo(new BigDecimal("23.4567")) == 0);
    }

}

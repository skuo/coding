package com.coding.service;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

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
    
    @Test
    public void getAllMonthly() throws Exception{
        // first save a Bid
        Bid bid = new Bid();
        bid.setSourceId("sourceId");
        bid.setSource("wp");
        bid.setBid(new BigDecimal("1.2345"));
        bid.setUpdatedAt(new Timestamp(new Date().getTime()));
        Bid dbBid = bidRepository.save(bid);
        
        assertTrue(this.bidRepository.findOne(1L) != null);
    }

}

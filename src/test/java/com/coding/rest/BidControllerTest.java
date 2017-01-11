package com.coding.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

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

import com.coding.dto.BidDto;
import com.coding.dto.BidStatus;
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
        BidDto bidDto = new BidDto();
        bidDto.setSourceId(sourceId);
        bidDto.setSource(source);
        bidDto.setBid(new BigDecimal("1.2345"));
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // insert a bid
        BidStatus bidStatus = bidController.putBid(sourceId, source, bidDto, response);
        assertEquals(bidStatus.getStatus(), BidStatus.SUCCESS);
        // get the inserted bid
        List<BidDto> bidDtos2 = bidController.getBids(sourceId, source, request, response);
        assertEquals(1, bidDtos2.size());
        BidDto dbBidDto = bidDtos2.get(0);
        assertEquals(bidDto, bidDtos2.get(0));
        // update bid
        dbBidDto.setBid(new BigDecimal("2.3456"));
        bidStatus = bidController.putBid(sourceId, source, dbBidDto, response);
        assertEquals(bidStatus.getStatus(), BidStatus.SUCCESS);
        // get updated bid
        bidDtos2 = bidController.getBids(sourceId, source, request, response);
        assertEquals(1, bidDtos2.size());
        assertEquals(dbBidDto, bidDtos2.get(0));
    }

}

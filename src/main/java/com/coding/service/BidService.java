package com.coding.service;

import java.util.LinkedList;
import java.util.List;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coding.dto.BidDto;
import com.coding.entity.Bid;
import com.coding.repository.BidRepository;

@Service
public class BidService {
    //private final Logger log = LoggerFactory.getLogger(this.getClass());
    private BidRepository bidRepository;
    
    @Autowired
    public BidService(BidRepository bidRepository){
        this.bidRepository = bidRepository;
    }
    
    /**
    * Get a bid by sourceId and source
    * @return Bid in JSON format
    * @throws 
    */
    public List<BidDto> getBids(String sourceId, String source) {
        
        List<Bid> bids= bidRepository.get(sourceId, source);
        List<BidDto> bidDtos = new LinkedList<>();
        for (Bid bid : bids) {
            bidDtos.add(new BidDto(bid));
        }
        return bidDtos;
    }
    
    /**
    * Save a bid by either inserting or updating
    * @return boolean
    * @throws 
    */
    public boolean saveBid(BidDto bidDto) {
        boolean status = true;
        Bid bid = new Bid(bidDto);
        Bid dbBid = bidRepository.save(bid);
        if (dbBid == null)
            status = false;
        return status;
    }
    
    /**
     * Delete all bids
     */
    public void deleteBids() {
        bidRepository.deleteAll();
    }
}

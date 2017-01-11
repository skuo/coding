package com.coding.service;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Bid getBid(String sourceId, String source) throws Exception{
        
        Bid bid= bidRepository.get(sourceId, source);
        return bid;
    }
    
    /**
    * Save a bid by either inserting or updating
    * @return boolean
    * @throws 
    */
    public boolean saveBid(Bid bid) throws Exception{
        boolean status = true;
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

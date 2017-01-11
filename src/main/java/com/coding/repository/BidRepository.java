package com.coding.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.coding.entity.Bid;

@RepositoryRestResource(exported=false)
public interface BidRepository extends CrudRepository<Bid,Long> {

    @Query("SELECT b FROM Bid b WHERE source_id = :sourceId and source = :source")
    public Bid get(@Param("sourceId") String sourceId, @Param("source") String source);
    
}

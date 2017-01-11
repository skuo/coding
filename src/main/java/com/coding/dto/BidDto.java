package com.coding.dto;

import java.math.BigDecimal;

import com.coding.entity.Bid;

public class BidDto {
    private Long id;
    private String sourceId;
    private String source;
    private BigDecimal bid;
    
    public BidDto(){
        
    }

    public BidDto(Bid bid) {
        this.id = bid.getId();
        this.sourceId = bid.getSourceId();
        this.source = bid.getSource();
        this.bid = bid.getBid();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bid == null) ? 0 : bid.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BidDto other = (BidDto) obj;
        if (bid == null) {
            if (other.bid != null)
                return false;
        } else if (bid.compareTo(other.bid) != 0)
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (sourceId == null) {
            if (other.sourceId != null)
                return false;
        } else if (!sourceId.equals(other.sourceId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Bid [id=" + id + ", sourceId=" + sourceId + ", source=" + source + ", bid=" + bid + "]";
    }
    
}

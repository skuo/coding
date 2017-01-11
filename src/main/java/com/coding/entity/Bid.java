package com.coding.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="bid")
public class Bid {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Column(name="source_id")
    private String sourceId;
    @Column(name="source")
    private String source;
    @Column(name="bid",precision=8, scale=5)
    private BigDecimal bid;
    @Column(name="updated_at")
    private Timestamp updatedAt;
    
    public Bid(){
        
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

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Bid [id=" + id + ", sourceId=" + sourceId + ", source=" + source + ", bid=" + bid + ", updatedAt="
                + updatedAt + "]";
    }
    
}

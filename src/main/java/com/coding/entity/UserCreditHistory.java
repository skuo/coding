package com.coding.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Data
@Builder
@EqualsAndHashCode(exclude={"userCredit"})
@Entity
@Table(name="user_credit_History")
public class UserCreditHistory implements java.io.Serializable {
    private static final long serialVersionUID = -3358312800940124898L;

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_credit_id", nullable= false)
    private UserCredit userCredit;
    
    @Column(name="purchaseCredit",precision=8, scale=2)
    private BigDecimal purchaseCredit;
    
    @Column(name="storeCredit",precision=8, scale=2)
    private BigDecimal storeCredit;
    
    @Column(name="type")
    private UserCreditHistoryType type;
    
    @Column(name="created_at")
    private Date createdAt;
    
    @Column(name="updated_at")
    private Date updatedAt;

    @Tolerate
    public UserCreditHistory() {
        
    }
    
    @PrePersist
    private void prePersist() {
        Date now = new Date();
        createdAt = now;
        updatedAt = now;
    }
    
    @PreUpdate
    private void preUpdate() {
        Date now = new Date();
        updatedAt = now;
    }

}

package com.coding.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.Tolerate;

@Data
@Builder
@EqualsAndHashCode(exclude={"histories"})
@Entity
@Table(name="user_credit")
public class UserCredit implements java.io.Serializable{
    private static final long serialVersionUID = -9168657911103444091L;

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(name="user_id")
    private Long userId;
    
    @Column(name="purchaseCredit",precision=8, scale=2)
    private BigDecimal purchaseCredit;
    
    @Column(name="storeCredit",precision=8, scale=2)
    private BigDecimal storeCredit;
    
    @Version
    private Integer version;
    
    @Column(name="created_at")
    private Date createdAt;
    
    @Column(name="updated_at")
    private Date updatedAt;

    @Singular
    @OneToMany(mappedBy = "userCredit", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    Set<UserCreditHistory> histories;
    
    @Tolerate
    // JPA needs a default constructor
    public UserCredit() {
        
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

    public void add(UserCreditHistory history) {
        if (histories.isEmpty())
            histories = new HashSet<>();
        
        histories.add(history);
    }
}

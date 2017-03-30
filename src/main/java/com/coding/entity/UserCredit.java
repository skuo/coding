package com.coding.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
@Entity
@Table(name="user_credit")
public class UserCredit {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Column(name="user_id")
    private Long userId;
    @Column(name="purchaseCredit",precision=8, scale=2)
    private BigDecimal purchaseCredit;
    @Column(name="storeCredit",precision=8, scale=2)
    private BigDecimal storeCredit;
    @Column(name="created_at")
    private Timestamp createdAt;
    @Column(name="updated_at")
    private Timestamp updatedAt;

}

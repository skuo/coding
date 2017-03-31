package com.coding.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreditDto implements Serializable {
    private static final long serialVersionUID = -415512715712828734L;

    @JsonProperty
    private Long id;
    
    @JsonProperty
    private Long userId;
    
    @JsonProperty
    private BigDecimal purchaseCredit;
    
    @JsonProperty
    private BigDecimal storeCredit;
    
    @JsonProperty
    private Date createdAt;
    
    @JsonProperty
    private Date updatedAt;
    
    @JsonProperty
    private Set<UserCreditHistoryDto> histories;

}

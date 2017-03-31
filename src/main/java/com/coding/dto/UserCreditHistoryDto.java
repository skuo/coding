package com.coding.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.coding.entity.UserCreditHistoryType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreditHistoryDto implements Serializable {
    private static final long serialVersionUID = 8816738435665058108L;

    @JsonProperty
    private Long id;

    @JsonProperty
    private UserCreditDto userCredit;
    
    @JsonProperty
    private BigDecimal purchaseCredit;
    
    @JsonProperty
    private BigDecimal storeCredit;
    
    @JsonProperty
    private UserCreditHistoryType type;
    
    @JsonProperty
    private Date createdAt;
    
    @JsonProperty
    private Date updatedAt;

}

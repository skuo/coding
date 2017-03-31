package com.coding.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//builder has problem with bi-directional Jackson processing
//@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    
    @JsonManagedReference
    //@Builder.Default
    private Set<UserCreditHistoryDto> histories = Collections.emptySet();

    public void add(UserCreditHistoryDto history) {
        if (histories == null || histories.isEmpty())
            histories = new HashSet<>();
        
        histories.add(history);
    }
}

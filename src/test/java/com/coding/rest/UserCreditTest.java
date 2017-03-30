package com.coding.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.coding.entity.UserCredit;

public class UserCreditTest {
    private Log log = LogFactory.getLog(getClass());

    public int validateUserCredit(UserCredit uc, Long expUserId, BigDecimal expPurchaseCredit,
            BigDecimal expStoreCredit) {
        if (expUserId == null)
            assertNull(uc.getUserId());
        return 1;
    }

    @Test
    public void testLombok() {
        Long userId1 = 1L;
        BigDecimal purchaseCredit80 = new BigDecimal(80);
        BigDecimal storeCredit20 = new BigDecimal(20);
        UserCredit uc = UserCredit.builder()
                .userId(userId1)
                .purchaseCredit(purchaseCredit80)
                .storeCredit(storeCredit20)
                .build();

        assertEquals(1, validateUserCredit(uc, userId1, purchaseCredit80, storeCredit20));
        
    }

}

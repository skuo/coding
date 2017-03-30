package com.coding.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.coding.entity.UserCredit;
import com.coding.entity.UserCreditHistory;

public class UserCreditTest {
    public static int validateUserCredit(UserCredit uc, Long expUserId, BigDecimal expPurchaseCredit,
            BigDecimal expStoreCredit) {
        if (expUserId == null)
            assertNull(uc.getUserId());
        else 
            assertEquals(expUserId, uc.getUserId());

        if (expPurchaseCredit == null)
            assertNull(uc.getPurchaseCredit());
        else 
            assertTrue(expPurchaseCredit.compareTo(uc.getPurchaseCredit()) == 0);

        if (expStoreCredit == null)
            assertNull(uc.getStoreCredit());
        else 
            assertTrue(expStoreCredit.compareTo(uc.getStoreCredit()) == 0);

        return 1;
    }

    public static int validateUserCreditHistory(UserCreditHistory uch, Long expUserId, BigDecimal expPurchaseCredit,
            BigDecimal expStoreCredit) {
        if (expUserId == null)
            assertNull(uch.getUserCredit().getUserId());
        else 
            assertEquals(expUserId, uch.getUserCredit().getUserId());

        if (expPurchaseCredit == null)
            assertNull(uch.getPurchaseCredit());
        else 
            assertTrue(expPurchaseCredit.compareTo(uch.getPurchaseCredit()) == 0);

        if (expStoreCredit == null)
            assertNull(uch.getStoreCredit());
        else 
            assertTrue(expStoreCredit.compareTo(uch.getStoreCredit()) == 0);

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
        UserCreditHistory uch = UserCreditHistory.builder()
                .userCredit(uc)
                .purchaseCredit(purchaseCredit80)
                .storeCredit(storeCredit20)
                .build();
        uc.add(uch);
        
        assertEquals(1, validateUserCredit(uc, userId1, purchaseCredit80, storeCredit20));
        assertEquals(1, validateUserCreditHistory(uch, userId1, purchaseCredit80, storeCredit20));
    }

}

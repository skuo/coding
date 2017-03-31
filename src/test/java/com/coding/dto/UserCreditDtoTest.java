package com.coding.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Test;

import com.coding.entity.UserCreditHistoryType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserCreditDtoTest {
    private Long userId1 = 1L;
    private BigDecimal purchaseCredit80 = new BigDecimal(80);
    private BigDecimal storeCredit20 = new BigDecimal(20);

    public static int validateUserCreditDto(UserCreditDto uc, Long expUserId, BigDecimal expPurchaseCredit,
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

    public static int validateUserCreditHistoryDto(UserCreditHistoryDto uch, Long expUserId, BigDecimal expPurchaseCredit,
            BigDecimal expStoreCredit) {
        if (expUserId == null) {
            if (uch.getUserCredit() != null)
                assertNull(uch.getUserCredit().getUserId());
        }
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
    public void testDto() throws IOException {
        // @builder has problem with bi-directional Jackson processing
        /*
        UserCreditDto ucDto = UserCreditDto.builder()
                .userId(userId1)
                .purchaseCredit(purchaseCredit80)
                .storeCredit(storeCredit20)
                .build();
        UserCreditHistoryDto uchDto = UserCreditHistoryDto.builder()
                .userCredit(ucDto)
                .purchaseCredit(purchaseCredit80)
                .storeCredit(storeCredit20)
                //.type(UserCreditHistoryType.CREATE)
                .build();
        */
        UserCreditDto ucDto = new UserCreditDto(null, userId1, purchaseCredit80, storeCredit20, null, null, null);
        UserCreditHistoryDto uchDto = new UserCreditHistoryDto();
        uchDto.setUserCredit(ucDto);
        uchDto.setPurchaseCredit(purchaseCredit80);
        uchDto.setStoreCredit(storeCredit20);
        uchDto.setType(UserCreditHistoryType.CREATE);
        ucDto.add(uchDto);
        
        // jackson
        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(ucDto);
        
        UserCreditDto ucDto2 = mapper.readValue(str, UserCreditDto.class);
        assertEquals(1, validateUserCreditDto(ucDto2, userId1, purchaseCredit80, storeCredit20));
        UserCreditHistoryDto uchDto2 = (UserCreditHistoryDto) ucDto2.getHistories().toArray()[0];
        // UserCreditHistoryDto's userCreditDto is not restored.
        assertEquals(1, validateUserCreditHistoryDto(uchDto2, null, purchaseCredit80, storeCredit20));
    }
}

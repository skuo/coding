package com.coding.mapping;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

import com.coding.dto.UserCreditDto;
import com.coding.entity.UserCredit;
import com.coding.entity.UserCreditHistory;
import com.coding.entity.UserCreditHistoryType;

@Ignore
// Wait until an example for https://github.com/mapstruct/mapstruct/issues/469 is out 
public class EntityToDtoMapperTest {
    private Long userId1 = 1L;
    private BigDecimal purchaseCredit80 = new BigDecimal(80);
    private BigDecimal storeCredit20 = new BigDecimal(20);

    @Test
    public void testMapStruct() {
        UserCredit uc = UserCredit.builder()
                .userId(userId1)
                .purchaseCredit(purchaseCredit80)
                .storeCredit(storeCredit20)
                .build();
        UserCreditHistory uch = UserCreditHistory.builder()
                .userCredit(uc)
                .purchaseCredit(purchaseCredit80)
                .storeCredit(storeCredit20)
                .type(UserCreditHistoryType.CREATE)
                .build();
        uc.add(uch);
        
        UserCreditDto userCreditDto = EntityToDtoMapper.INSTANCE.userCreditToUserCreditDto(uc);
        System.out.println(userCreditDto);
    }
}

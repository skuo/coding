package com.coding.repository;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.coding.entity.UserCredit;
import com.coding.rest.UserCreditTest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class UserCreditRepositoryTest {

    private Long userId1 = 1L;
    private Long userId2 = 2L;
    private BigDecimal purchaseCredit80 = new BigDecimal(80);
    private BigDecimal purchaseCredit50 = new BigDecimal(50);
    private BigDecimal storeCredit20 = new BigDecimal(20);

    @Autowired
    private UserCreditRepository userCreditRepo;
    
    @Before public void setup() {
        UserCredit userCredit = UserCredit.builder()
                .userId(userId1)
                .purchaseCredit(purchaseCredit80)
                .storeCredit(storeCredit20)
                .build();
        userCreditRepo.save(userCredit);

        userCredit = UserCredit.builder()
                .userId(userId2)
                .purchaseCredit(purchaseCredit80)
                .storeCredit(storeCredit20)
                .build();
        userCreditRepo.save(userCredit);

    }
    
    @Test
    public void testRepo() {
        // find by userId1
        UserCredit dbUserCredit1 = userCreditRepo.findOne(userId1);
        assertEquals(1,UserCreditTest.validateUserCredit(dbUserCredit1, userId1, purchaseCredit80, storeCredit20));
        // update
        dbUserCredit1.setPurchaseCredit(purchaseCredit50);
        UserCredit updatedUserCredit1 = userCreditRepo.save(dbUserCredit1);
        assertEquals(1,UserCreditTest.validateUserCredit(updatedUserCredit1, userId1, purchaseCredit50, storeCredit20));
        
        // find UserCredit with credits
        List<UserCredit> userCredits = userCreditRepo.availableCredit();
        assertEquals(2, userCredits.size());
        int asserted = 0;
        for (UserCredit uc: userCredits) {
            if (uc.getUserId() == userId1)
                asserted += UserCreditTest.validateUserCredit(dbUserCredit1, userId1, purchaseCredit50, storeCredit20);
            else if (uc.getUserId() == userId2)
                asserted += UserCreditTest.validateUserCredit(dbUserCredit1, userId2, purchaseCredit80, storeCredit20);
        }
        assertEquals(2, asserted);
    }
       
}

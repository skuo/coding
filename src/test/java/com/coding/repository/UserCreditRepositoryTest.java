package com.coding.repository;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.coding.entity.UserCredit;
import com.coding.entity.UserCreditHistory;
import com.coding.entity.UserCreditHistoryType;
import com.coding.rest.UserCreditTest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class UserCreditRepositoryTest {

    private Long userId1 = 1L;
    private Long userId2 = 2L;
    private BigDecimal purchaseCredit30 = new BigDecimal(30);
    private BigDecimal purchaseCredit50 = new BigDecimal(50);
    private BigDecimal purchaseCredit80 = new BigDecimal(80);
    private BigDecimal storeCredit20 = new BigDecimal(20);
    private BigDecimal storeCredit30 = new BigDecimal(30);
    private BigDecimal storeCredit50 = new BigDecimal(50);

    @Autowired
    private UserCreditRepository userCreditRepo;
    
    @Before
    @Transactional
    public void setup() {
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
        uch = UserCreditHistory.builder()
                .userCredit(uc)
                .purchaseCredit(purchaseCredit30)
                .storeCredit(storeCredit30)
                .type(UserCreditHistoryType.CAPTURE)
                .build();
        uc.add(uch);
        userCreditRepo.save(uc);
        

        UserCredit uc2 = UserCredit.builder()
                .userId(userId2)
                .purchaseCredit(purchaseCredit50)
                .storeCredit(storeCredit20)
                .build();
        UserCreditHistory uch2 = UserCreditHistory.builder()
                .userCredit(uc2)
                .purchaseCredit(purchaseCredit80)
                .storeCredit(storeCredit50)
                .type(UserCreditHistoryType.CREATE)
                .build();
        uc2.add(uch2);
        uch2 = UserCreditHistory.builder()
                .userCredit(uc2)
                .purchaseCredit(purchaseCredit30)
                .storeCredit(storeCredit30)
                .type(UserCreditHistoryType.CAPTURE)
                .build();
        uc2.add(uch2);
        userCreditRepo.save(uc2);

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
        int assertedUc = 0, assertedUch = 0;
        for (UserCredit uc: userCredits) {
            if (uc.getUserId() == userId1) {
                assertedUc += UserCreditTest.validateUserCredit(uc, userId1, purchaseCredit50, storeCredit20);
                for (UserCreditHistory uch : uc.getHistories()) {
                    if (uch.getType() == UserCreditHistoryType.CREATE)
                        assertedUch += UserCreditTest.validateUserCreditHistory(uch, userId1, purchaseCredit80, storeCredit20);
                    else if (uch.getType() == UserCreditHistoryType.CAPTURE)
                        assertedUch += UserCreditTest.validateUserCreditHistory(uch, userId1, purchaseCredit30, storeCredit30);                    
                }
            }
            else if (uc.getUserId() == userId2) {
                assertedUc += UserCreditTest.validateUserCredit(uc, userId2, purchaseCredit50, storeCredit20);
                for (UserCreditHistory uch : uc.getHistories()) {
                    if (uch.getType() == UserCreditHistoryType.CREATE)
                        assertedUch += UserCreditTest.validateUserCreditHistory(uch, userId2, purchaseCredit80, storeCredit50);
                    else if (uch.getType() == UserCreditHistoryType.CAPTURE)
                        assertedUch += UserCreditTest.validateUserCreditHistory(uch, userId2, purchaseCredit30, storeCredit30);
                }
            }
        }
        assertEquals(2, assertedUc);
        assertEquals(4, assertedUch);
        
        // test getByUserIdAndType
        userCredits = userCreditRepo.getByUserIdAndType(userId1, UserCreditHistoryType.CAPTURE);
        assertedUc = 0;
        assertedUch = 0;
        for (UserCredit uc: userCredits) {
            if (uc.getUserId() == userId1) {
                assertedUc += UserCreditTest.validateUserCredit(uc, userId1, purchaseCredit50, storeCredit20);
                for (UserCreditHistory uch : uc.getHistories()) {
                    if (uch.getType() == UserCreditHistoryType.CREATE)
                        assertedUch += UserCreditTest.validateUserCreditHistory(uch, userId1, purchaseCredit80, storeCredit20);
                    else if (uch.getType() == UserCreditHistoryType.CAPTURE)
                        assertedUch += UserCreditTest.validateUserCreditHistory(uch, userId1, purchaseCredit30, storeCredit30);                    
                }
            }
        }
        assertEquals(1, assertedUc);
        assertEquals(1, assertedUch);
    }
       
}

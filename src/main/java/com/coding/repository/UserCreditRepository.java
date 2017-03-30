package com.coding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.coding.entity.UserCredit;
import com.coding.entity.UserCreditHistoryType;

public interface UserCreditRepository extends CrudRepository<UserCredit,Long>{
    
    @Query("SELECT uc FROM UserCredit uc WHERE uc.purchaseCredit > 0 and uc.storeCredit > 0")
    public List<UserCredit> availableCredit();
    
    @Query("SELECT uc FROM UserCredit uc JOIN FETCH uc.histories uch WHERE uc.userId = :userId and uch.type = :type")
    public List<UserCredit> getByUserIdAndType(@Param("userId") Long userId, @Param("type") UserCreditHistoryType type);
    
}

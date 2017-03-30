package com.coding.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.coding.entity.UserCredit;

public interface UserCreditRepository extends CrudRepository<UserCredit,Long>{
    
    @Query("SELECT uc FROM UserCredit uc WHERE uc.purchaseCredit > 0 and uc.storeCredit > 0")
    public List<UserCredit> availableCredit();
    
}

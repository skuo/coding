package com.coding.entity;

public enum UserCreditHistoryType {
    CREATE("CREATE"), CAPTURE("CAPTURE"), CANCELLED("CANCELLED"), REFUND("REFUND");
    
    private String name;
    
    UserCreditHistoryType(String name) {
        this.name= name;
    }
    
    public String getName() {
        return name;
    }
}

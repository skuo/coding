package com.coding.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.coding.dto.UserCreditDto;
import com.coding.entity.UserCredit;

@Mapper
public interface EntityToDtoMapper {
    EntityToDtoMapper INSTANCE = Mappers.getMapper( EntityToDtoMapper.class ); 
    
    UserCreditDto userCreditToUserCreditDto(UserCredit userCredit); 
}

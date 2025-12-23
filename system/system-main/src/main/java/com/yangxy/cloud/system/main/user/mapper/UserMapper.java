package com.yangxy.cloud.system.main.user.mapper;

import com.yangxy.cloud.system.main.user.dto.UserDTO;
import com.yangxy.cloud.system.main.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(User user);

    User toEntity(UserDTO userDTO);

    @Mapping(target = "id", ignore = true)
    User createEntity(UserDTO userDTO);

}

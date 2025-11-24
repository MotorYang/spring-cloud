package com.yangxy.cloud.system.main.user.mapper;

import com.yangxy.cloud.system.main.user.dto.User;
import com.yangxy.cloud.system.main.user.entity.UserEntity;
import com.yangxy.cloud.system.main.user.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 17:17
 */
@Mapper
public interface UserMapStruct {

    UserMapStruct INSTANCE = Mappers.getMapper(UserMapStruct.class);

    UserEntity userToUserEntity(User user);

    User userEntityToUser(UserEntity userEntity);

    User userVoToUser(UserVO userVO);

    UserVO userToUserVO(User user);
}

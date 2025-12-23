package com.yangxy.cloud.system.main.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangxy.cloud.system.main.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDAO extends BaseMapper<User> {

}

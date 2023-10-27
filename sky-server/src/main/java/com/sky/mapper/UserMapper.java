package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {


    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid=#{openid}")
    User geByOpenid(String openid);

    /**
     * 插入用户
     * @param user
     */
    void insert(User user);//由于插入后需要对主键赋值,后续要用到,所以

    @Select("select * from user where id=#{userId}")
    User getById(Long userId);


    /**
     * 统计用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}

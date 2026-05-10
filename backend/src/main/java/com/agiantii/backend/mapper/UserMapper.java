package com.agiantii.backend.mapper;

import com.agiantii.backend.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("insert into t_user (id,password,role) values (#{id},#{password},#{role})")
    void insertUser(User user);
    @Delete("delete from t_user where id=#{id}")
    void deleteUserNyId(int id);
    @Update("update t_user set password=#{password} where id=#{id}")
    void updateUser(User user);

    @Select("select * from t_user where id = #{name}")
    List<User> selectByUserName(@Param("name") String userName);

    @Select("select * from t_user where role = #{role}")
    List<User> selectUserByRole(String role);
    @Select("select * from t_user where id = #{id}")
    User selectUserById(int id);
    @Select("select * from t_user where id = #{id} and password = #{password}")
    User selectUserByIdAndPassword(@Param("id") Integer id, @Param("password") String password);
}

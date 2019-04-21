package com.dreamfish.fishblog.core.mapper;

import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.entity.UserExtened;
import org.apache.ibatis.annotations.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户结果集
     *
     * @param username 用户名
     * @return 查询结果
     */
    @Select("SELECT * FROM fish_users WHERE name = #{username}")
    List<User> findByUserName(@Param("username") String username);

    /**
     * 根据用户 id 查询用户结果集
     * @param id 用户 id
     * @return 查询结果
     */
    @Select("SELECT * FROM fish_users WHERE id = #{id}")
    User findById(@Param("id") Integer id);

    @Cacheable(value = "blog-user-cache", key = "'user_full_'+#p0")
    @Select("SELECT * FROM fish_users WHERE id = #{id}")
    UserExtened findByFullById(@Param("id") Integer id);

    @Select("SELECT * FROM fish_users WHERE thrid_id = #{thirdId}")
    UserExtened findByFullByThirdId(@Param("thirdId") String thirdId);

    /**
     * 根据用户 id 查询用户头像
     * @param id 用户 id
     * @return 用户头像
     */
    @Select("SELECT headimg FROM fish_users WHERE id = #{id}")
    String getUserHeadById(@Param("id") Integer id);

    @Select("SELECT name FROM fish_users WHERE id = #{id}")
    String getUserNameById(@Param("id") Integer id);

    @Select("SELECT friendly_name FROM fish_users WHERE id = #{id}")
    String getUserFriendlyNameById(@Param("id") Integer id);

    @Select("SELECT level FROM fish_users WHERE id = #{id}")
    Integer getUserLevelById(@Param("id") Integer id);

    @Select("SELECT passwd FROM fish_users WHERE id = #{id}")
    String getUserPasswordById(@Param("id") Integer id);

    @Select("SELECT id,headimg,level FROM fish_users")
    List<User> getAllUserHeads();

    @Select("select id from `fish_users` where id=#{id} limit 1")
    Integer isUserIdExists(@Param("id") int id);

    @CacheEvict(value = "blog-user-cache", key = "'user_full_'+#p0")
    @Delete("DELETE FROM fish_users WHERE id = #{id}")
    void delUser(@Param("id") int id);

    @Update("UPDATE fish_users SET passwd=#{passwd} WHERE id=#{id}")
    void updateUserPassword(@Param("id") int id, @Param("passwd") String passwd);

    @CacheEvict(value = "blog-user-cache", key = "'user_full_'+#p0")
    @Update("UPDATE fish_users SET headimg=#{newHead} WHERE id=#{id}")
    void updateUserHead(@Param("id") int id, @Param("newHead") String newHead);

    @CacheEvict(value = "blog-user-cache", key = "'user_full_'+#p0")
    @Update("UPDATE fish_users SET id=#{newId} WHERE id=#{id}")
    void updateUserId(@Param("id") int id, @Param("newId") int newId);

    @CacheEvict(value = "blog-user-cache", key = "'user_full_'+#p0")
    @Update("UPDATE fish_users SET old_level=level,level=#{level} WHERE id=#{id}")
    void updateUserLevel(@Param("id") int id, @Param("level") int level);

    @CacheEvict(value = "blog-user-cache", key = "'user_full_'+#p0")
    @Update("UPDATE fish_users SET level=old_level WHERE id=#{id}")
    void updateUserLevelSetToOld(@Param("id") int id);

    @CacheEvict(value = "blog-user-cache", key = "'user_full_'+#p0")
    @Update("UPDATE fish_users SET privilege=#{privilege} WHERE id=#{id}")
    void updateUsePrivilege(@Param("id") int id, @Param("privilege") int privilege);
}

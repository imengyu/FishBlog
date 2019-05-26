package com.dreamfish.fishblog.core.mapper;

import com.dreamfish.fishblog.core.entity.MessageItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Select("SELECT * FROM `fish_messages` WHERE `user_id`=#{userId} AND `have_read`=#{have_read} ORDER BY `date` DESC LIMIT #{maxCount}")
    List<MessageItem> findAllByUserIdAndReadOrderByDateDescLimit(@Param("userId") Integer userId, @Param("have_read") Boolean read, @Param("maxCount")Integer maxCount);

    @Update("UPDATE fish_messages SET `have_read`=#{have_read} WHERE `id` IN(#{ids})")
    void updateReadByIdIn(@Param("have_read") Boolean read, String ids);
    @Update("UPDATE fish_messages SET `have_read`=#{have_read} WHERE `user_id`=#{userId}")
    void updateReadByUserId(@Param("have_read") Boolean read, Integer userId);

    @Select("SELECT COUNT(`have_read`) FROM fish_messages WHERE `user_id`=#{userId} AND `have_read`=0")
    Integer getNotReadMessageCountByUserId(Integer userId);
}

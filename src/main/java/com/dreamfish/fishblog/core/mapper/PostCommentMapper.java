package com.dreamfish.fishblog.core.mapper;

import com.dreamfish.fishblog.core.entity.PostComment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostCommentMapper {

    /**
     * 获取文章的所有评论
     * @param id 目标文章 ID
     * @return
     */
    @Select("SELECT * FROM fish_comments WHERE post_id=#{id}")
    List<PostComment> getCommentForPost(@Param("id") Integer id);

    /**
     * 获取单条评论
     * @param id 评论 ID
     * @return
     */
    @Select("SELECT * FROM fish_comments WHERE id=#{id} ORDER BY post_date DESC ")
    List<PostComment> getComment(@Param("id") Integer id);

    /**
     * 获取评论的作者 ID
     * @param id 评论 ID
     * @return
     */
    @Select("SELECT author_id FROM fish_comments WHERE id=#{id}")
    Integer getCommentUserId(@Param("id") Integer id);

    /**
     * 删除评论
     * @param id 评论 ID
     * @return
     */
    @Delete("DELETE FROM fish_comments WHERE id=#{id}")
    void deleteComment(@Param("id") Integer id);

}

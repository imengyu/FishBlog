package com.dreamfish.fishblog.core.mapper;


import com.dreamfish.fishblog.core.entity.PostTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostTagsMapper {

    /**
     * 获取文章标签并分页
     * @param startIndex
     * @param pageSize
     * @return
     */
    @Select("SELECT * FROM fish_post_tags LIMIT #{startIndex},#{pageSize}")
    List<PostTag> getTags(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
    @Select("SELECT * FROM fish_post_tags WHERE id=#{id}")
    List<PostTag> getTagById(@Param("id") Integer id);

    /**
     * 获取所有文章标签
     * @return
     */
    @Select("SELECT * FROM fish_post_tags")
    List<PostTag> getAllTags();

    /**
     * 删除文章标签
     * @param id 文章标签 ID
     * @return
     */
    @Delete("DELETE FROM fish_post_tags WHERE id=#{id}")
    void deleteTag(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM fish_post_tags")
    Integer getTagsCount();

    /**
     * 新建文章标签
     * @param tag 文章标签
     * @return 返回文章标签 ID
     */
    @Insert("INSERT INTO fish_post_tags (name,color) VALUES(#{tag.name},#{tag.color})")
    @Options(useGeneratedKeys = true, keyProperty = "tag.id", keyColumn = "id")
    void addTag(@Param("tag") PostTag tag);

    /**
     * 更新文章标签
     */
    @Update("UPDATE fish_post_tags SET name =#{tag.name},color=#{tag.color} where id=#{tag.id}")
    void updateTag(@Param("tag") PostTag tag);
}

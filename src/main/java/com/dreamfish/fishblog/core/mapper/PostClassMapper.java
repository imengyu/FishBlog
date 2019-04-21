package com.dreamfish.fishblog.core.mapper;

import com.dreamfish.fishblog.core.entity.PostClass;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostClassMapper {

    /**
     * 获取分类并分页
     * @param startIndex
     * @param pageSize
     * @return
     */
    @Select("SELECT * FROM fish_post_classes LIMIT #{startIndex},#{pageSize}")
    List<PostClass> getClasses(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
    @Select("SELECT * FROM fish_post_classes WHERE id=#{id}")
    PostClass getClassesById(@Param("id") Integer id);
    @Select("SELECT * FROM fish_post_classes WHERE url_name=#{urlName}")
    PostClass getClassesByUrlName(@Param("urlName") String urlName);

    /**
     * 获取所有分类
     * @return
     */
    @Select("SELECT * FROM fish_post_classes")
    List<PostClass> getAllClasses();

    @Select("SELECT COUNT(*) FROM fish_post_classes")
    Integer getClassesCount();

    /**
     * 删除分类
     * @param id 分类 ID
     * @return
     */
    @Delete("DELETE FROM fish_post_classes WHERE id=#{id}")
    void deleteClass(@Param("id") Integer id);
}

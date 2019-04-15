package com.dreamfish.fishblog.core.mapper;


import com.dreamfish.fishblog.core.entity.Post;
import com.dreamfish.fishblog.core.entity.PostClass;
import com.dreamfish.fishblog.core.entity.PostDate;
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
    @Select("SELECT * FROM fish_post_classes WHERE status=1 LIMIT #{startIndex},#{pageSize}")
    List<PostClass> getClasses(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
    /**
     * 获取分类并分页（包括未公开的）
     * @param startIndex
     * @param pageSize
     * @return
     */
    @Select("SELECT * FROM fish_post_classes LIMIT #{startIndex},#{pageSize}")
    List<PostClass> getClassesIncludeingPrivate(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
    @Select("SELECT * FROM fish_post_classes WHERE id=#{id} AND status=1")
    PostClass getClassesById(@Param("id") Integer id);
    @Select("SELECT * FROM fish_post_classes WHERE url_name=#{urlName} AND status=1")
    PostClass getClassesByUrlName(@Param("urlName") String urlName);

    /**
     * 获取所有分类
     * @return
     */
    @Select("SELECT * FROM fish_post_classes WHERE status=1")
    List<PostClass> getAllClasses();

    @Select("SELECT COUNT(*) FROM fish_post_classes")
    Integer getClassesCount();

    /**
     * 获取所有分类数
     * @return
     */
    @Select("select count(*) from fish_post_classes where status=1")
    List<Post> getAllClassCount();
    /**
     * 获取所有分类数（包括未公开的）
     * @return
     */
    @Select("select count(*) from fish_post_classes")
    List<Post> ggetAllClassCountIncludeingPrivate();

    /**
     * 删除分类
     * @param id 分类 ID
     * @return
     */
    @Delete("DELETE FROM fish_post_classes WHERE id=#{id}")
    void deleteClass(@Param("id") Integer id);
}

package com.dreamfish.fishblog.core.mapper;

import com.dreamfish.fishblog.core.entity.Post;
import com.dreamfish.fishblog.core.entity.PostAbstract;
import com.dreamfish.fishblog.core.entity.PostTag;
import com.dreamfish.fishblog.core.entity.PostUrl;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {


    /**
     * 获取文章并分页
     * @param startIndex 起始记录位置
     * @param pageSize 一页的大小
     * @return 返回文章数组
     */
    @Select("SELECT * FROM fish_posts WHERE status=1 LIMIT #{startIndex},#{pageSize}")
    List<Post> getPosts(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);
    /**
     * 获取文章并分页（包括未公开的）
     * @param startIndex 起始记录位置
     * @param pageSize 一页的大小
     * @return 返回文章数组
     */
    @Select("SELECT * FROM fish_posts LIMIT #{startIndex},#{pageSize}")
    List<Post> getPostsIncludeingPrivate(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    /**
     * 获取所有文章
     * @return 返回文章数组
     */
    @Select("SELECT * FROM fish_posts WHERE status=1")
    List<Post> getAllPosts();

    /**
     * 获取所有文章数
     * @return 文章数
     */
    @Select("select count(*) from fish_posts where status=1")
    Integer getAllPostCount();

    /**
     * 获取所有文章（包括未公开的）
     * @return 返回文章数组
     */
    @Select("SELECT * FROM fish_posts")
    List<Post> getAllPostsIncludeingPrivate();

    /**
     * 获取所有文章数（包括未公开的）
     * @return 文章数
     */
    @Select("select count(*) from fish_posts")
    Integer getAllPostCountIncludeingPrivate();

    /**
     * 删除文章
     * @param id 文章 ID
     */
    @Delete("DELETE FROM fish_posts WHERE id=#{id}")
    void deletePost(@Param("id") Integer id);

    /**
     * 获取文章作者 ID
     * @param id 文章 ID
     * @return 作者 ID
     */
    @Select("SELECT author_id FROM fish_posts WHERE id=#{id}")
    Integer getPostAuthorId(@Param("id") Integer id);

    /**
     * 更新文章某个数值+1
     * @param id 文章 ID
     */
    @Update("UPDATE fish_posts SET ${valueName}=${valueName}+1 WHERE id=#{id}")
    void increasePostValue(@Param("id") Integer id, @Param("valueName") String valueName);

    /**
     * 更新文章某个数值-1
     * @param id 文章 ID
     */
    @Update("UPDATE fish_posts SET ${valueName}=${valueName}+1 WHERE id=#{id}")
    void decreasePostValue(@Param("id") Integer id, @Param("valueName") String valueName);

    /**
     * 根据文章 ID 获取文章标题
     * @param id 文章 ID
     */
    @Select("SELECT title FROM fish_posts WHERE status=1 AND id=#{id}")
    String getPostTitle(@Param("id") Integer id);

    @Select("SELECT * FROM fish_post_tags WHERE id=#{id}")
    PostTag getTag(@Param("id") Integer id);


    @Select("SELECT title,preview_text,keywords,content FROM fish_posts WHERE url_name=#{urlName}")
    PostAbstract findAbstractByUrlName(@Param("urlName") String urlName);
    @Select("SELECT title,preview_text,keywords,content FROM fish_posts WHERE id=#{id}")
    PostAbstract findAbstractById(@Param("id") Integer id);

    @Select("SELECT id,title,url_name,preview_text,keywords FROM fish_posts WHERE status=1 ORDER BY post_date DESC LIMIT #{maxCount}")
    List<PostUrl> findAbstractTitles(@Param("maxCount") Integer maxCount);



    @Select("SELECT id,title,url_name,preview_text,keywords FROM fish_posts WHERE status=1 AND tags LIKE %-${tagId}-% ORDER BY post_date DESC LIMIT #{maxCount}")
    List<PostUrl> findAbstractTitlesWithTag(@Param("tagId") Integer tagId, @Param("maxCount") Integer maxCount);
    @Select("SELECT id,title,url_name,preview_text,keywords FROM fish_posts WHERE status=1 AND post_date LIKE %${year}-${month}% ORDER BY post_date DESC LIMIT #{maxCount}")
    List<PostUrl> findAbstractTitlesWithDate(@Param("year") String year, @Param("month") String month, @Param("maxCount") Integer maxCount);
    @Select("SELECT id,title,url_name,preview_text,keywords FROM fish_posts WHERE status=1 AND post_class LIKE %${sclass}% ORDER BY post_date DESC LIMIT #{maxCount}")
    List<PostUrl> findAbstractTitlesWithClass(@Param("sclass") String sclass, @Param("maxCount") Integer maxCount);

    @Select("select id from `fish_posts` where id=#{id} limit 1")
    Integer isPostIdExists(@Param("id") int id);

}

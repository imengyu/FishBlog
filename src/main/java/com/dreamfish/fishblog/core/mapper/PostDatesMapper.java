package com.dreamfish.fishblog.core.mapper;

import com.dreamfish.fishblog.core.entity.PostDate;
import com.dreamfish.fishblog.core.entity.PostTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostDatesMapper {

    /**
     * 获取所有文章归档时间
     *
     * @return
     */
    @Select("SELECT * FROM fish_post_dates ORDER BY date DESC")
    List<PostDate> getAllDates();

    @Select("SELECT * FROM fish_post_dates ORDER BY date DESC LIMIT #{startIndex},#{pageSize}")
    List<PostDate> getAllDatesWithLimit(@Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    @Select("SELECT * FROM fish_post_dates WHERE id=#{id}")
    PostDate getDateById(@Param("id") Integer id);
    @Select("SELECT * FROM fish_post_dates WHERE date=#{date}")
    PostDate getDateByDate(@Param("date") String date);

    /**
     * 删除文章归档时间
     *
     * @param id 文章归档时间 ID
     * @return
     */
    @Delete("DELETE FROM fish_post_dates WHERE id=#{id}")
    void deletDate(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM fish_post_dates")
    Integer getDatesCount();

    /**
     * 新建文章归档时间
     *
     * @param date 文章归档时间
     * @return 返回 ID
     */
    @Insert("INSERT INTO fish_post_dates (date,count) VALUES(#{date.date},#{date.count})")
    @Options(useGeneratedKeys = true, keyProperty = "date.id", keyColumn = "id")
    void addDate(@Param("date") PostDate date);

    /**
     * 更新文章归档时间的文章数
     *
     * @param date 文章归档时间
     * @return
     */
    @Update("UPDATE fish_post_dates SET count=#{date.count} where id=#{date.id}")
    void updateDate(@Param("date") PostDate date);

    @Update("UPDATE fish_post_dates SET count=count-1 where id=#{id}")
    void updateDecreaseDateCount(@Param("id") Integer id);
    @Update("UPDATE fish_post_dates SET count=count+1 where id=#{id}")
    void updateIncreaseDateCount(@Param("id") Integer id);
}

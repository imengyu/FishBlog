package com.dreamfish.fishblog.core.mapper;

import com.dreamfish.fishblog.core.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StatMapper {


    /**
     * 更新 指定 状态数据
     * @param statKey 指定状态数据名称
     * @param data 数据
     */
    @Update("UPDATE fish_stats SET data=#{data} WHERE name=#{statKey}")
    void updateStat(@Param("statKey") String statKey, @Param("data") String data);

    @Update("UPDATE fish_stats SET int_data=#{data} WHERE name=#{statKey}")
    void updateStatInt(@Param("statKey") String statKey, @Param("data") Integer data);

    @Select("select * from `fish_stats` WHERE name=#{statKey}")
    Stat getStat(@Param("statKey") String statKey);

    @Select("select int_data from `fish_stats` WHERE name=#{statKey}")
    Integer getStatIntData(@Param("statKey") String statKey);

    @Select("select * from `fish_stats` WHERE name IN (${statKeys})")
    List<Stat> getStats(@Param("statKeys") String statKeys);

    @Select("select COUNT(*) from `fish_users` WHERE level=#{level}")
    Integer getUserCountWithLevel(@Param("level") Integer level);

    /**
     * 自增 指定 状态数据
     * @param statKey 指定状态数据名称
     */
    @Update("UPDATE fish_stats SET int_data=int_data+1,data=int_data WHERE name=#{statKey}")
    void updateStatIncrease(@Param("statKey") String statKey);

    @Insert("INSERT INTO fish_stat_pages (date,url,count) VALUES(NOW(),#{url},1)")
    void addStatPage(@Param("url") String url);

    @Select("select id from `fish_stat_pages` where url=#{url} AND TO_DAYS(date)=TO_DAYS(NOW()) limit 1")
    Integer findTodatStatPage(@Param("url") String url);

    @Update("UPDATE fish_stat_pages SET count=count+1 WHERE id=#{id}")
    void updateStatPageIncrease(@Param("id") Integer id);
    @Select("select * from `fish_stat_pages` WHERE date=#{date} ORDER BY date DESC limit #{maxCount}")
    List<StatPage> getStatTopPage(@Param("date") String date, @Param("maxCount") Integer maxCount);
    @Select("select * from `fish_stat_pages` WHERE TO_DAYS(date)=TO_DAYS(NOW()) ORDER BY date DESC limit #{maxCount}")
    List<StatPage> getStatTodayTopPage(@Param("maxCount") Integer maxCount);


    @Select("select @rownum := @rownum +1 AS rowsnumber from `fish_stat_daylog` ORDER BY date ASC WHERE date=#{date}")
    Integer getStatDayLogMonthRowNum(@Param("date") String date);
    @Select("select * from `fish_stat_daylog` ORDER BY date ASC limit #{start}, #{maxCount}")
    List<StatDay> getStatDayLogMonth(@Param("start") Integer start, @Param("maxCount") Integer maxCount);
    @Select("select * from `fish_stat_daylog` ORDER BY date ASC limit 30")
    List<StatDay> getStatDayLogMonthThisMonth();

    @Insert("INSERT INTO fish_stat_daylog (date,ip,pv,comment,tag) VALUES(NOW(),#{ip},#{pv},#{comment},0)")
    void addDayLog(@Param("pv") Integer pv, @Param("ip") Integer ip, @Param("comment") Integer comment);
    @Delete("DELETE FROM fish_stat_daylog WHERE TO_DAYS(NOW())-TO_DAYS(date)>#{outdays}")
    void deleteDayLog(@Param("outdays") Integer outdays);


    @Select("select id,url_name,view_count,title from `fish_posts` WHERE status=1 ORDER BY view_count DESC limit #{maxCount}")
    List<StatTopPost> getStatTodayTopPost(@Param("maxCount") Integer maxCount);

    @Delete("DELETE FROM fish_stat_ip")
    void clearIpTable();
}

package com.dreamfish.fishblog.core.mapper;

import com.dreamfish.fishblog.core.entity.SettingItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SettingsMapper {

    @Select("select * from `fish_settings` WHERE name=#{statKey}")
    SettingItem getSettingItem(@Param("statKey") String statKey);
    @Select("select data from `fish_settings` WHERE name=#{statKey}")
    String getSetting(@Param("statKey") String statKey);
    @Update("UPDATE `fish_settings` SET data=#{data} WHERE name=#{statKey}")
    void setSetting(@Param("statKey") String statKey, @Param("data") String data);
    @Select("SELECT * FROM `fish_settings`")
    List<SettingItem> getSettings();



}

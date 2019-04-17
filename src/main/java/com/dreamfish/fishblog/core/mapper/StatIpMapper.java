package com.dreamfish.fishblog.core.mapper;

import com.dreamfish.fishblog.core.entity.PostTag;
import org.apache.ibatis.annotations.*;

@Mapper
public interface StatIpMapper {

    @Insert("INSERT INTO fish_stat_ip (ip) VALUES(#{ip})")
    void addStaIp(@Param("ip") String ip);

    @Select("select id from `fish_stat_ip` where ip=#{ip} limit 1")
    Integer isStaIpExists(@Param("ip") String ip);
}

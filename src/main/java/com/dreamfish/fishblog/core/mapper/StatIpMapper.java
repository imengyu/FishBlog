package com.dreamfish.fishblog.core.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface StatIpMapper {

    @Insert("INSERT INTO fish_stat_ip (ip) VALUES(#{ip})")
    void addStatIp(@Param("ip") String ip);

    @Insert("UPDATE fish_stat_ip SET visit_count=visit_count+1 WHERE ip=#{ip}")
    void updateStayIpIncrease(@Param("ip") String ip);

    @Select("select id from `fish_stat_ip` where ip=#{ip} limit 1")
    Integer isStaIpExists(@Param("ip") String ip);
}

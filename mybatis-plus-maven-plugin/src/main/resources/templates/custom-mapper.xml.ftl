<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${cfg.basePackage}.${cfg.moduleName}.mapper.${entity}Mapper">

<#if baseResultMap!false>
    <resultMap id="ResultMap" type="${package.Entity}.${entity}" extends="${cfg.basePackage}.${cfg.moduleName}.mapper.base.${entity}BaseMapper.BaseResultMap">
    </resultMap>

</#if>
<#if baseColumnList!false>
    <sql id="Column_List">
        <include refid="${cfg.basePackage}.${cfg.moduleName}.mapper.base.${entity}BaseMapper.Base_Column_List"/>
    </sql>

</#if>

</mapper>

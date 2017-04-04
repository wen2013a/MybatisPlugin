<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${Namespace}" >
<#if (ConfigCDATA)?? >${ConfigCDATA}</#if>
<#if (BaseResultMap)?? >${BaseResultMap}</#if>
<#if (BlobResultMap)?? >${BlobResultMap}</#if>
<#if (BaseColumnList)?? >${BaseColumnList}</#if>
<#if (BlobColumnList)?? >${BlobColumnList}</#if>
<#if (AdapterWhereCondition)?? >${AdapterWhereCondition}</#if>
<#if (AdapterUpdateWhereCondition)?? >${AdapterUpdateWhereCondition}</#if>
<#if (insert)?? >${insert}</#if>
<#if (insertSelective)?? >${insertSelective}</#if>
<#if (deleteByAdapter)?? >${deleteByAdapter}</#if>
<#if (deleteByPrimaryKey)?? >${deleteByPrimaryKey}</#if>
<#if (updateByAdapter)?? >${updateByAdapter}</#if>
<#if (updateByAdapterWithBlob)?? >${updateByAdapterWithBlob}</#if>
<#if (updateByAdapterSelective)?? >${updateByAdapterSelective}</#if>
<#if (updateByPrimaryKey)?? >${updateByPrimaryKey}</#if>
<#if (updateByPrimaryKeyWithBlob)?? >${updateByPrimaryKeyWithBlob}</#if>
<#if (updateByPrimaryKeySelective)?? >${updateByPrimaryKeySelective}</#if>
<#if (selectByAdapter)?? >${selectByAdapter}</#if>
<#if (selectByAdapterWithBlob)?? >${selectByAdapterWithBlob}</#if>
<#if (selectByPrimaryKey)?? >${selectByPrimaryKey}</#if>
<#if (countByAdapter)?? >${countByAdapter}</#if>
</mapper>
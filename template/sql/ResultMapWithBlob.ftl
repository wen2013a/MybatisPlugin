<#if (Fields?size > 0)>  <resultMap id="ResultMapWithBlob" type="${BeanClass}" extends="BaseResultMap">
    <#list Fields as e>
    <${e.primaryKey?string("id","result")} column="${e.columnName}" property="${e.property}" jdbcType="${e.jdbcType}" />
    </#list>
  </resultMap></#if>
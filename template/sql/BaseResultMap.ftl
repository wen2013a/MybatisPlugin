  <resultMap id="BaseResultMap" type="${BeanClass}">
    <#list Fields as e>
    <${e.primaryKey?string("id","result")} column="${e.columnName}" property="${e.property}" jdbcType="${e.jdbcType}" />
    </#list>
  </resultMap>
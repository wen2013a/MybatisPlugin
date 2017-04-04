<#if (PrimaryKeys?size > 0)>  <update id="${UpdateName}" parameterType="${BeanClass}">
    update ${TableName}
    set <#list Fields as e><#if (!e.identity)>${e.columnName} = ${e.paramStr}${(e_index!=(Fields?size)-1)?string(",\r\n      ","")}</#if></#list>
    where <#list PrimaryKeys as e><#if (e_index > 0)>    and </#if>${e.columnName} = ${e.paramStr}
    </#list>
  </update></#if>
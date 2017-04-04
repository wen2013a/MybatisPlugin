<#if (PrimaryKeys?size == 1)>  <delete id="deleteByPrimaryKey" parameterType="${JavaClass}">
    delete from ${TableName}
    where ${Column} = ${MapperParameter}
  </delete>
<#elseif (PrimaryKeys?size > 1)>  <delete id="deleteByPrimaryKey" parameterType="${KeyBeanClass}">
    delete from ${TableName}
    where <#list PrimaryKeys as e><#if (e_index > 0)>    and </#if>${e.columnName} = ${e.paramStr}
    </#list>
  </delete></#if>
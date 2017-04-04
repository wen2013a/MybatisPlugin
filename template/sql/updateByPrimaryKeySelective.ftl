<#if (PrimaryKeys?size > 0)>  <update id="updateByPrimaryKeySelective" parameterType="${BeanClass}">
    update ${TableName}
    <set>
      <#list Fields as e>
      <#if (!e.identity)>
      <if test="${e.property} != null">${e.columnName} = ${e.paramStr},</if>
      </#if>
      </#list>
    </set>
    where <#list PrimaryKeys as e><#if (e_index > 0)>    and </#if>${e.columnName} = ${e.paramStr}
    </#list>
  </update></#if>
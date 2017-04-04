<#if (PrimaryKeys?size == 1)>  <select id="selectByPrimaryKey" resultMap="<#if (IsBlob)>ResultMapWithBlob<#else>BaseResultMap</#if>" parameterType="${JavaClass}">
    select 
    <include refid="BaseColumnList" />
    <#if (IsBlob)>,<include refid="BlobColumnList" /></#if>
    from ${TableName}
    where ${Column} = ${MapperParameter}
  </select>
<#elseif (PrimaryKeys?size > 1)>  <select id="selectByPrimaryKey" resultMap="<#if (IsBlob)>ResultMapWithBlob<#else>BaseResultMap</#if>" parameterType="${KeyBeanClass}">
    select 
    <include refid="BaseColumnList" />
    <#if (IsBlob)>,<include refid="BlobColumnList" /></#if>
    from ${TableName}
    where <#list PrimaryKeys as e><#if (e_index > 0)>    and </#if>${e.columnName} = ${e.paramStr}
    </#list>
  </select></#if>
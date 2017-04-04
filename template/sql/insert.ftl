  <insert id="insert" parameterType="${BeanClass}" <#list Fields as e><#if (e.identity)>useGeneratedKeys="true" keyProperty="${e.property}"</#if></#list>>
    insert into ${TableName} (<#list Fields as e><#if (!e.identity)>${e.columnName}${(e_index!=(Fields?size)-1)?string(", ","")}<#if ((e_index+1)%6==0)&&(e_index!=(Fields?size)-1)>${"\r\n      "}</#if></#if></#list>)
    values (<#list Fields as e><#if (!e.identity)>${e.paramStr}${(e_index!=(Fields?size)-1)?string(", ","")}<#if ((e_index+1)%3==0)&&(e_index!=(Fields?size)-1)>${"\r\n      "}</#if></#if></#list>)
  </insert>
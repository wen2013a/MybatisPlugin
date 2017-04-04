  <insert id="insertSelective" parameterType="${BeanClass}" <#list Fields as e><#if (e.identity)>useGeneratedKeys="true" keyProperty="${e.property}"</#if></#list>>
    insert into ${TableName}
    <trim prefix="(" suffix=")" suffixOverrides=",">
      <#list Fields as e>
      <#if (!e.identity)>
      <if test="${e.property} != null">${e.columnName},</if>
      </#if>
      </#list>
    </trim>
    <trim prefix="values (" suffix=")" suffixOverrides=",">
      <#list Fields as e>
      <#if (!e.identity)>
      <if test="${e.property} != null">${e.paramStr},</if>
      </#if>
      </#list>
    </trim>
  </insert>
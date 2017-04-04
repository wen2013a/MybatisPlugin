  <update id="updateByAdapterSelective" parameterType="map">
    update ${TableName}
    <set>
      <#list Fields as e>
      <#if (!e.identity)>
      <if test="record.${e.property} != null">${e.columnName} = ${e.recordParamStr},</if>
      </#if>
      </#list>
    </set>
    <if test="_parameter != null">
      <include refid="AdapterUpdateWhereCondition" />
    </if>
  </update>
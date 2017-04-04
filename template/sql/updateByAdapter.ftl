  <update id="${UpdateName}" parameterType="map">
    update ${TableName}
    set <#list Fields as e><#if (!e.identity)>${e.columnName} = ${e.recordParamStr}${(e_index!=(Fields?size)-1)?string(",\r\n      ","")}</#if></#list>
    <if test="_parameter != null">
      <include refid="AdapterUpdateWhereCondition" />
    </if>
  </update>
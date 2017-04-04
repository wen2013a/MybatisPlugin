  <delete id="deleteByAdapter" parameterType="${Adapter}">
    delete from ${TableName}
    <if test="_parameter != null">
      <include refid="AdapterWhereCondition" />
    </if>
  </delete>
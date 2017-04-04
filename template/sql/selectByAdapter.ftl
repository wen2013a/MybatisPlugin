  <select id="selectByAdapter" resultMap="BaseResultMap" parameterType="${Adapter}">
    select <if test="distinct">distinct</if>
    <include refid="BaseColumnList" />
    from ${TableName}
    <if test="_parameter != null">
      <include refid="AdapterWhereCondition" />
    </if>
    <if test="orderByClause != null">
      ${r"order by ${orderByClause}"}
    </if>
  </select>
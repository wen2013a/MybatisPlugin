  <select id="selectByAdapterWithBlob" resultMap="ResultMapWithBlob" parameterType="${Adapter}">
    select <if test="distinct">distinct</if>
    <include refid="BaseColumnList" />,
    <include refid="BlobColumnList" />
    from ${TableName}
    <if test="_parameter != null">
      <include refid="AdapterWhereCondition" />
    </if>
    <if test="orderByClause != null">
      ${r"order by ${orderByClause}"}
    </if>
  </select>
  <select id="countByAdapter" parameterType="${Adapter}" resultType="java.lang.Integer">
    select count(*) from ${TableName}
    <if test="_parameter != null">
      <include refid="AdapterWhereCondition" />
    </if>
  </select>
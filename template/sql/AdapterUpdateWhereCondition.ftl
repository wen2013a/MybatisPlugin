  <sql id="AdapterUpdateWhereCondition" >
    <where>
      <foreach collection="adapter.oredCriteria" item="criteria" separator="or">
        <if test="criteria.valid" >
          <trim prefix="(" suffix=")" prefixOverrides="and">
            <foreach collection="criteria.criteria" item="criterion">
              <choose>
                <when test="criterion.noValue">
                  ${r"and ${criterion.condition}"}
                </when>
                <when test="criterion.singleValue">
                  ${r"and ${criterion.condition} #{criterion.value}"}
                </when>
                <when test="criterion.betweenValue">
                  ${r"and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}"}
                </when>
                <when test="criterion.listValue">
                  ${r"and ${criterion.condition}"}
                  <foreach collection="criterion.value" item="listItem" open="(" close=")" separator=",">
                    ${r"#{listItem}"}
                  </foreach>
                </when>
              </choose>
            </foreach>
          </trim>
        </if>
      </foreach>
    </where>
  </sql>
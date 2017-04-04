  <sql id="CONFIG_NODE">
    <![CDATA[ ${CFG_CDATA_FLAG}
	<#if (datasource!="")>
    datasource=${datasource}
    </#if>
	<#if (tablename!="")>
    tablename=${tablename}
    </#if>
    <#if (identitycolumn!="")>
    identitycolumn=${identitycolumn}
    </#if>
    <#if (beanclass!="")>
    beanclass=${beanclass}
    </#if>
    <#if (interclass!="")>
    interclass=${interclass}
    </#if>
    <#if (daoclass!="")>
    daoclass=${daoclass}
    </#if>
    ]]>
  </sql>
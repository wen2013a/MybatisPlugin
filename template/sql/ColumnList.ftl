<#if (Fields?size > 0)>  <sql id="${SqlName}">
    <#list Fields as e>${e.columnName}${(e_index!=(Fields?size)-1)?string(", ","")}<#if ((e_index+1)%6==0)&&(e_index!=(Fields?size)-1)>${"\r\n    "}</#if></#list>
  </sql></#if>
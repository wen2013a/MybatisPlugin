package ${PackageName};

${Import}
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ${Inter} {

<#list Methods as e>
    static final String M_${e.METHOD_NAME} = "${e.METHOD_NAME}";

</#list>
<#list Methods as e>
	<#if ((e.METHOD_REMARKS!)||(e.PARM_REMARKS?size != 0))>
    /**
    * <#if (e.METHOD_REMARKS!)>${e.METHOD_REMARKS}</#if>
	<#list e.PARM_REMARKS as param>
    * ${param}
	</#list>
    */
	</#if>
    ${e.RETURN_TYPE} ${e.METHOD_NAME}(${e.PARAMETERS});

</#list>
}
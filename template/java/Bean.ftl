package ${PackageName};

import java.io.Serializable;
${Import}
public class ${BeanName} implements Serializable {

	private static final long serialVersionUID = 1L;
	<#list Fields as e>

    private ${e.shorJavaClass} ${e.property};
	</#list>
	<#list Fields as e>

    public ${e.shorJavaClass} ${e.methodGet}() {
        return ${e.property};
    }

    public void ${e.methodSet}(${e.shorJavaClass} ${e.property}) {
        this.${e.property} = ${e.property};
    }
	</#list>
}
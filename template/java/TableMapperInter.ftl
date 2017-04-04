package ${PackageName};

import ${BeanClass};
import ${AdapterClass};
<#if (IsBlob)>
import ${BlobBeanClass};
</#if>
<#if (PrimaryKeys?size > 1)>
import ${KeyBeanClass};
</#if>

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ${Inter} {
<#if (isBuildInsert)>

    static final String M_insert = "insert";

    static final String M_insertSelective = "insertSelective";
</#if>
<#if (isBuildDelete)>

    static final String M_deleteByAdapter = "deleteByAdapter";
    <#if (PrimaryKeys?size > 0)>

    static final String M_deleteByPrimaryKey = "deleteByPrimaryKey";
    </#if>
</#if>
<#if (isBuildUpdate)>

    static final String M_updateByAdapter = "updateByAdapter";
    <#if (IsBlob)>

    static final String M_updateByAdapterWithBlob = "updateByAdapterWithBlob";
	</#if>

    static final String M_updateByAdapterSelective = "updateByAdapterSelective";
    <#if (PrimaryKeys?size > 0)>

    static final String M_updateByPrimaryKey = "updateByPrimaryKey";
    </#if>
    <#if (IsBlob)>

    static final String M_updateByPrimaryKeyWithBlob = "updateByPrimaryKeyWithBlob";
    </#if>
	<#if (PrimaryKeys?size > 0)>

    static final String M_updateByPrimaryKeySelective = "updateByPrimaryKeySelective";
    </#if>
</#if>
<#if (isBuildSelect)>

    static final String M_selectByAdapter = "selectByAdapter";
    <#if (PrimaryKeys?size > 0)>

    static final String M_selectByPrimaryKey = "selectByPrimaryKey";
    </#if>
    <#if (IsBlob)>

    static final String M_selectByAdapterWithBlob = "selectByAdapterWithBlob";
    </#if>

    static final String M_countByAdapter = "countByAdapter";
</#if>
<#if (isBuildInsert)>

    int insert(<#if (IsBlob)>${BlobBean} ${ProBlobBean}<#else>${Bean} ${ProBean}</#if>);

    int insertSelective(<#if (IsBlob)>${BlobBean} ${ProBlobBean}<#else>${Bean} ${ProBean}</#if>);
</#if>
<#if (isBuildDelete)>

    int deleteByAdapter(${Adapter} adapter);
    <#if (PrimaryKeys?size == 1)>

    int deleteByPrimaryKey(${PrimaryKeys[0].shorJavaClass} ${PrimaryKeys[0].property});
    <#elseif (PrimaryKeys?size > 1)>

    int deleteByPrimaryKey(${KeyBean} key);
    </#if>
</#if>
<#if (isBuildUpdate)>

    int updateByAdapter(@Param("record") ${Bean} ${ProBean}, @Param("adapter") ${Adapter} adapter);
    <#if (IsBlob)>

    int updateByAdapterWithBlob(@Param("record") ${BlobBean} ${ProBlobBean}, @Param("adapter") ${Adapter} adapter);
	</#if>

    int updateByAdapterSelective(@Param("record") <#if (IsBlob)>${BlobBean} ${ProBlobBean}<#else>${Bean} ${ProBean}</#if>, @Param("adapter") ${Adapter} adapter);
    <#if (PrimaryKeys?size > 0)>

    int updateByPrimaryKey(${Bean} ${ProBean});
    </#if>
    <#if (IsBlob)>

    int updateByPrimaryKeyWithBlob(${BlobBean} ${ProBlobBean});
    </#if>
	<#if (PrimaryKeys?size > 0)>

    int updateByPrimaryKeySelective(<#if (IsBlob)>${BlobBean} ${ProBlobBean}<#else>${Bean} ${ProBean}</#if>);
    </#if>
</#if>
<#if (isBuildSelect)>

    List<${Bean}> selectByAdapter(${Adapter} adapter);
    <#if (IsBlob)>

    List<${BlobBean}> selectByAdapterWithBlob(${Adapter} adapter);
    </#if>
    <#if (PrimaryKeys?size == 1)>

    <#if (IsBlob)>${BlobBean}<#else>${Bean}</#if> selectByPrimaryKey(${PrimaryKeys[0].shorJavaClass} ${PrimaryKeys[0].property});
    <#elseif (PrimaryKeys?size > 1)>

    <#if (IsBlob)>${BlobBean}<#else>${Bean}</#if> selectByPrimaryKey(${KeyBean} key);
    </#if>

    int countByAdapter(${Adapter} adapter);
</#if>

}
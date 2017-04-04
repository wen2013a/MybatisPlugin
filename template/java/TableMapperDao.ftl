package ${PackageName};

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.caohua.common.component.dataaccess.mybatis.DaoSupport;
import ${BeanClass};
import ${AdapterClass};
import ${InterClass};
<#if (IsBlob)>
import ${BlobBeanClass};
</#if>
<#if (PrimaryKeys?size > 1)>
import ${KeyBeanClass};
</#if>

@Repository("${RepositoryName}")
public class ${DaoName} extends DaoSupport<${Inter}> {
<#if (isBuildInsert)>

	public int insert(<#if (IsBlob)>${BlobBean} ${ProBlobBean}<#else>${Bean} ${ProBean}</#if>) {
		return mapper.insert(<#if (IsBlob)>${ProBlobBean}<#else>${ProBean}</#if>);
	}
	
	public int insertSelective(<#if (IsBlob)>${BlobBean} ${ProBlobBean}<#else>${Bean} ${ProBean}</#if>) {
		return mapper.insertSelective(<#if (IsBlob)>${ProBlobBean}<#else>${ProBean}</#if>);
	} 
</#if>
<#if (isBuildDelete)>
	<#if (PrimaryKeys?size == 1)>
	
	public int deleteByPrimaryKey(${PrimaryKeys[0].shorJavaClass} ${PrimaryKeys[0].property}) {
		return mapper.deleteByPrimaryKey(${PrimaryKeys[0].property});
	}
    <#elseif (PrimaryKeys?size > 1)>
    
	public int deleteByPrimaryKey(${KeyBean} key) {
		return mapper.deleteByPrimaryKey(key);
	}
    </#if> 
</#if>
<#if (isBuildUpdate)>
	<#if (PrimaryKeys?size > 0)>
	
	public int updateByPrimaryKey(${Bean} ${ProBean}) {
		return mapper.updateByPrimaryKey(${ProBean});
	}
	</#if>
	<#if (IsBlob)>
	
	public int updateByPrimaryKeyWithBlob(${BlobBean} ${ProBlobBean}) {
		return mapper.updateByPrimaryKeyWithBlob(${ProBlobBean});
	}
	</#if>
	<#if (PrimaryKeys?size > 0)>
	
	public int updateByPrimaryKeySelective(<#if (IsBlob)>${BlobBean} ${ProBlobBean}<#else>${Bean} ${ProBean}</#if>) {
		return mapper.updateByPrimaryKeySelective(<#if (IsBlob)>${ProBlobBean}<#else>${ProBean}</#if>);
	}
    </#if> 
</#if>
<#if (isBuildSelect)>
	<#if (PrimaryKeys?size == 1)>
	
	public <#if (IsBlob)>${BlobBean}<#else>${Bean}</#if> selectByPrimaryKey(${PrimaryKeys[0].shorJavaClass} ${PrimaryKeys[0].property}) {
		return mapper.selectByPrimaryKey(${PrimaryKeys[0].property});
	}
	<#elseif (PrimaryKeys?size > 1)>
	
	public <#if (IsBlob)>${BlobBean}<#else>${Bean}</#if> selectByPrimaryKey(${KeyBean} key) {
		return mapper.selectByPrimaryKey(key);
	}
	</#if>
	
	public List<${Bean}> selectAll() {
		${Adapter} adapter = new ${Adapter}();
		List<${Bean}> list = mapper.selectByAdapter(adapter);
		return (list == null) ? new ArrayList<${Bean}>() : list;
	} 
</#if>
}
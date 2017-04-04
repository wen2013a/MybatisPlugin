package com.newair.studioplugin;

/**
 * Mapper文件配置
 */
public class ConfigCDATA {

	private String datasource = "";
	private String tablename = "";
	private String beanclass = "";
	private String interclass = "";
	private String daoclass = "";
	private String identitycolumn = "";
	private String namespace = "";
	
	public ConfigCDATA() {
		
	}
	
	public ConfigCDATA(String datasource, String tablename, String beanclass, String intermapperclass, String daoclass, String identitycolumn) {
		this.datasource = datasource;
		this.tablename = tablename;
		this.beanclass = beanclass;
		this.interclass = intermapperclass;
		this.daoclass = daoclass;
		this.identitycolumn = identitycolumn;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getBeanclass() {
		return beanclass;
	}

	public void setBeanclass(String beanclass) {
		this.beanclass = beanclass;
	}

	public String getInterclass() {
		return interclass;
	}

	public void setInterclass(String interclass) {
		this.interclass = interclass;
	}

	public String getDaoclass() {
		return daoclass;
	}

	public void setDaoclass(String daoclass) {
		this.daoclass = daoclass;
	}

	public String getIdentitycolumn() {
		return identitycolumn;
	}

	public void setIdentitycolumn(String identityColumn) {
		this.identitycolumn = identityColumn;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}

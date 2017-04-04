package com.newair.studioplugin;

public class DataSourceCfg {

	private String datasource = ""; // 数据源名称
	private String driverFile = ""; // 数据库驱动程序
	private String driverClass = ""; // 数据库驱动类
	private String url = ""; // 数据库URL
	private String username = ""; // 用户名
	private String password = ""; // 密码

	public String getDriverFile() {
		return driverFile;
	}

	public void setDriverFile(String driverFile) {
		this.driverFile = driverFile;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
}

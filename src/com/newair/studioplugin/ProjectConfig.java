package com.newair.studioplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工程配置
 */
public class ProjectConfig {
	
	private String dbConfigFile = ""; // 数据配置文件
	private String dataSource = "";   // 当前数据源
	private String mapperRoot = "";   // Mapper映射文件源目录
	private String javaSrc = "";      // Java代码源目录
	private String mapperDir = "";    // Mapper映射文件相对目录
	private String modelDir = "";     // Bean类文件相对目录
	private String interDir = "";     // 接口文件相对目录
	private String implDir = "";      // dao实现类相对目录
	private String intfProject = "";  // java接口工程名称
	private String intfSrc = "";      // java接口代码目录
	private Map<String, DataSourceCfg> dsMap = new HashMap<String, DataSourceCfg>(); // 数据源列表

	public String getUrl() {
		DataSourceCfg ds = dsMap.get(this.dataSource);
		return (ds != null) ? ds.getUrl() : null;
	}
	
	public String getUrl(String dataSource) {
		DataSourceCfg ds = dsMap.get(dataSource);
		return (ds != null) ? ds.getUrl() : null;
	}

	public String getUsername() {
		DataSourceCfg ds = dsMap.get(this.dataSource);
		return (ds != null) ? ds.getUsername() : null;
	}

	public String getUsername(String dataSource) {
		DataSourceCfg ds = dsMap.get(dataSource);
		return (ds != null) ? ds.getUsername() : null;
	}

	public String getPassword() {
		DataSourceCfg ds = dsMap.get(this.dataSource);
		return (ds != null) ? ds.getPassword() : null;
	}

	public String getPassword(String dataSource) {
		DataSourceCfg ds = dsMap.get(dataSource);
		return (ds != null) ? ds.getPassword() : null;
	}

	public String getDriverClass() {
		DataSourceCfg ds = dsMap.get(this.dataSource);
		return (ds != null) ? ds.getDriverClass() : null;
	}

	public String getDriverClass(String dataSource) {
		DataSourceCfg ds = dsMap.get(dataSource);
		return (ds != null) ? ds.getDriverClass() : null;
	}
	
	public String getDriverFile() {
		DataSourceCfg ds = dsMap.get(this.dataSource);
		return (ds != null) ? ds.getDriverFile() : null;
	}
	
	public String getDriverFile(String dataSource) {
		DataSourceCfg ds = dsMap.get(dataSource);
		return (ds != null) ? ds.getDriverFile() : null;
	}

	public void setJavaSrc(String javaSrc) {
		this.javaSrc = javaSrc;
	}

	public String getJavaSrc() {
		return javaSrc;
	}

	public void setMapperRoot(String mapperRoot) {
		this.mapperRoot = mapperRoot;
	}

	public String getMapperRoot() {
		return mapperRoot;
	}

	public void setMapperDir(String mapperDir) {
		this.mapperDir = mapperDir;
	}

	public String getMapperDir() {
		return mapperDir;
	}

	public void setModelDir(String modelDir) {
		this.modelDir = modelDir;
	}

	public String getModelDir() {
		return modelDir;
	}

	public void setInterDir(String interDir) {
		this.interDir = interDir;
	}

	public String getInterDir() {
		return interDir;
	}

	public void setImplDir(String implDir) {
		this.implDir = implDir;
	}

	public String getImplDir() {
		return implDir;
	}

	public String getDataSource() {
		return dataSource;
	}
	
	public DataSourceCfg getDataSource(String dataSource) {
		return dsMap.get(dataSource);
	}

	public void setDataSource(String source) {
		this.dataSource = source;
	}

	public String getDbConfigFile() {
		return dbConfigFile;
	}

	public void setDbConfigFile(String dbConfigFile) {
		this.dbConfigFile = dbConfigFile;
	}

	public String getIntfProject() {
		return intfProject;
	}

	public void setIntfProject(String intfProject) {
		this.intfProject = intfProject;
	}

	public String getIntfSrc() {
		return intfSrc;
	}

	public void setIntfSrc(String intfSrc) {
		this.intfSrc = intfSrc;
	}
	
	public void addDataSource(DataSourceCfg datasource) {
		dsMap.put(datasource.getDatasource(), datasource);
	}

	public void addDataSource(String datasource, String driverFile, String driverClass, String url, String username, String password) {
		DataSourceCfg ds = new DataSourceCfg();
		ds.setDatasource(datasource);
		ds.setDriverFile(driverFile);
		ds.setDriverClass(driverClass);
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		dsMap.put(ds.getDatasource(), ds);
	}
	
	public List<DataSourceCfg> getDataSources() {
		return new ArrayList<DataSourceCfg>(dsMap.values());
	}
	
	public void clearDataSources() {
		dsMap.clear();
	}

}

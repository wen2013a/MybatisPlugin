package com.newair.studioplugin.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import com.newair.studioplugin.DataSourceCfg;
import com.newair.studioplugin.ProjectConfig;
import com.newair.studioplugin.StudioConst;
import com.newair.studioplugin.StudioUtil;
import com.newair.studioplugin.wizard.CreateTableMapperWizard;

public class DbUtil {

	private static final Logger log = Logger.getLogger(CreateTableMapperWizard.class);
	
	public static Map<String, Driver> drivers = new HashMap<String, Driver>();

	/**
	 * 初始化数据库驱动，一般当前项目切换时、打开新项目或修改驱动配置时要调用
	 */
	public static Driver getDriver() throws Exception {
		ProjectConfig config = StudioUtil.getConfig();
		String key = config.getDriverFile();
		Driver driver = drivers.get(key);
		if (driver != null) {
			return driver;
		}
		File file = new File(config.getDriverFile());
		URL url = file.toURI().toURL();
		URLClassLoader DbLoader = new URLClassLoader(new URL[] { url }, ClassLoader.getSystemClassLoader());
		driver = (Driver)DbLoader.loadClass(config.getDriverClass()).newInstance();
		drivers.put(key, driver);
		return driver;
	}

	/**
	 * 获取新的JDBC连接
	 */
	public static Connection getConnection(String url, String username, String password) throws Exception {
		Driver driver = getDriver();
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		props.setProperty("ResultSetMetaDataOptions","1");
		Connection con = driver.connect(url, props);
		return con;
	}

	/**
	 * 获取新的JDBC连接
	 */
	public static Connection getConnection() throws Exception {
		ProjectConfig config = StudioUtil.getConfig();
		return getConnection(config.getUrl(), config.getUsername(), config.getPassword());
	}

	/**
	 * 获取新的JDBC连接
	 */
	public static Connection getConnection(String jarfile, String classname, String url, String username, String password) throws Exception {
		File file = new File(jarfile);
		URL _url = file.toURI().toURL();
		URLClassLoader DbLoader = new URLClassLoader(new URL[] { _url }, ClassLoader.getSystemClassLoader());
		Driver drv = (Driver)DbLoader.loadClass(classname).newInstance();
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		Connection con = drv.connect(url, props);
		return con;
	}
	
	/**
	 * 从Spring应用框架application.properties配置文件取数据源名称
	 */
	public static String[] getDataSources() {
		try {
			IProject proj = StudioUtil.getCurrentProject();
			IFile cfgfile = proj.getFile("src/main/webapp/WEB-INF/config/application.properties");
			String filename = ((IFile)cfgfile).getLocation().makeAbsolute().toFile().getAbsolutePath();
			log.debug("配置文件:" + filename);
			File file = new File(filename);
			if (!file.exists()) {
				return null;
			}
			InputStream input = new FileInputStream(filename);
			Properties prop = new Properties();
			prop.load(input);
			String DATASOURCES = prop.getProperty("DATASOURCES");
			if (StringUtils.isNotEmpty(DATASOURCES)) {
				return DATASOURCES.split(",");
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 从Spring应用框架中配置文件取数据库配置
	 */
	public static void getDBConfigByApplicationProperties(ProjectConfig config, String datasource) {
		try {
			String filename = config.getDbConfigFile();
			if (filename.startsWith(".settings/")) {
				IProject proj = StudioUtil.getCurrentProject();
				IFile cfgfile1 = proj.getFile(filename);
				filename = ((IFile) cfgfile1).getLocation().makeAbsolute().toFile().getAbsolutePath();
			}
			log.debug("配置文件:" + filename);
			File file = new File(filename);
			if (!file.exists()) {
				return;
			}
			InputStream input = new FileInputStream(filename);
			Properties prop = new Properties();
			prop.load(input);
			
			DataSourceCfg datasourcecfg = new DataSourceCfg();
			datasourcecfg.setDriverFile(prop.getProperty(datasource + ".driverFile", ""));
			datasourcecfg.setDriverClass(prop.getProperty(datasource + ".driverClassName", ""));
			datasourcecfg.setUrl(prop.getProperty(datasource + ".url", ""));
			datasourcecfg.setUsername(prop.getProperty(datasource + ".username", ""));
			datasourcecfg.setPassword(prop.getProperty(datasource + ".password", ""));
			config.addDataSource(datasourcecfg);
			
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	/**
	 * 从Spring应用框架中配置文件取数据库配置
	 */
	public static void getDBConfigByApplicationProperties(ProjectConfig config) {
		try {
			IProject proj = StudioUtil.getCurrentProject();
			IFile cfgfile = proj.getFile("src/main/webapp/WEB-INF/config/application.properties");
			String filename = ((IFile)cfgfile).getLocation().makeAbsolute().toFile().getAbsolutePath();
			log.debug("配置文件:" + filename);
			File file = new File(filename);
			if (!file.exists()) {
				return;
			}
			InputStream input = new FileInputStream(filename);
			Properties prop = new Properties();
			prop.load(input);
			String DATASOURCES = prop.getProperty("DATASOURCES");
			if (StringUtils.isNotEmpty(DATASOURCES)) {
				String[] dataSources = DATASOURCES.split(",");
				String ds = dataSources[0];
				DataSourceCfg datasource = new DataSourceCfg();
				datasource.setDriverFile(prop.getProperty(ds + ".driverFile", ""));
				datasource.setDriverClass(prop.getProperty(ds + ".driverClassName", ""));
				datasource.setUrl(prop.getProperty(ds + ".url", ""));
				datasource.setUsername(prop.getProperty(ds + ".username", ""));
				datasource.setPassword(prop.getProperty(ds + ".password", ""));
				config.addDataSource(datasource);
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	/**
	 * 从Spring应用框架中配置文件取数据库配置
	 */
	public static void getDBConfigBySpringApplicationContext(ProjectConfig config) {
		try {
			IProject proj = StudioUtil.getCurrentProject();
			IFile cfgfile = proj.getFile("config/applicationContext.xml");
			String filename = ((IFile)cfgfile).getLocation().makeAbsolute().toFile().getAbsolutePath();
			InputStream input = new FileInputStream(filename);
			InputStreamReader in = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(in);
			StringBuffer sb = new StringBuffer();
			String data = null;
			while ((data = br.readLine()) != null) {
				sb.append(data).append("\n");
			}
			br.close();
			in.close();
			input.close();
			DataSourceCfg datasourcecfg = new DataSourceCfg();
			Document doc = StudioUtil.parseXmlText(sb.toString());
			Node node = DbUtil.getNodeByXPath(doc, "/beans/bean[@id='dataSource']/property[@name='driverClassName']");
			if (node != null) {
				datasourcecfg.setDriverClass(((Element)node).attributeValue("value"));
			}
			node = DbUtil.getNodeByXPath(doc, "/beans/bean[@id='dataSource']/property[@name='url']");
			if (node != null) {
				datasourcecfg.setUrl(((Element)node).attributeValue("value"));
			}
			node = DbUtil.getNodeByXPath(doc, "/beans/bean[@id='dataSource']/property[@name='username']");
			if (node != null) {
				datasourcecfg.setUsername(((Element)node).attributeValue("value"));
			}
			node = DbUtil.getNodeByXPath(doc, "/beans/bean[@id='dataSource']/property[@name='password']");
			if (node != null) {
				datasourcecfg.setPassword(((Element)node).attributeValue("value"));
			}
			datasourcecfg.setDatasource("MyDataSource_XML");
			config.addDataSource(datasourcecfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Node getNodeByXPath(Document doc, String path) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("abc", "http://www.springframework.org/schema/beans");
		String s = path.replaceAll("/", "/abc:");
		XPath xpath = doc.createXPath(s);
		xpath.setNamespaceURIs(map);
		Node node = xpath.selectSingleNode(doc);
		return node;
	}

	/**
	 * 获取数据库引擎类
	 */
	public static IDbEngine getEngine() {
		ProjectConfig config = StudioUtil.getConfig();
		String driverclass = config.getDriverClass();
		if ("com.microsoft.sqlserver.jdbc.SQLServerDriver".equals(driverclass)
				|| "com.microsoft.jdbc.sqlserver.SQLServerDriver".equals(driverclass)) {  //SQLServer
			return new SqlServerEngine();
		} else if ("com.mysql.jdbc.Driver".equals(driverclass)) {  //Mysql
			//jdbc:mysql://localhost:3306/mydb
			String url = config.getUrl();
			int index = url.indexOf("//");
			index = url.indexOf("/", index + 2);
			String tableSchema = url.substring(index + 1);
			index = tableSchema.indexOf('?');
			if (index > 0) {
				tableSchema = tableSchema.substring(0, index);
			}
			MySqlEngine engine = new MySqlEngine();
			engine.setTableSchema(tableSchema);
			return engine;
		} else if ("oracle.jdbc.driver.OracleDriver".equals(config.getDriverClass())) {  //Oracle
			return new OracleEngine();
		}
		return new MySqlEngine();
	}
	
	/**
	 * 获取JDBC类型
	 */
	public static int getJdbcTypeByName(String jdbcTypeName) {
		if ("BIT".equals(jdbcTypeName)) {
			return -7;
		} else if ("TINYINT".equals(jdbcTypeName)) {
			return -6;
		} else if ("SMALLINT".equals(jdbcTypeName)) {
			return 5;
		} else if ("INTEGER".equals(jdbcTypeName)) {
			return 4;
		} else if ("BIGINT".equals(jdbcTypeName)) {
			return -5;
		} else if ("FLOAT".equals(jdbcTypeName)) {
			return 6;
		} else if ("REAL".equals(jdbcTypeName)) {
			return 7;
		} else if ("DOUBLE".equals(jdbcTypeName)) {
			return 8;
		} else if ("NUMERIC".equals(jdbcTypeName)) {
			return 2;
		} else if ("DECIMAL".equals(jdbcTypeName)) {
			return 3;
		} else if ("CHAR".equals(jdbcTypeName)) {
			return 1;
		} else if ("VARCHAR".equals(jdbcTypeName)) {
			return 12;
		} else if ("LONGVARCHAR".equals(jdbcTypeName)) {
			return -1;
		} else if ("DATE".equals(jdbcTypeName)) {
			return 91;
		} else if ("TIME".equals(jdbcTypeName)) {
			return 92;
		} else if ("TIMESTAMP".equals(jdbcTypeName)) {
			return 93;
		} else if ("BINARY".equals(jdbcTypeName)) {
			return -2;
		} else if ("VARBINARY".equals(jdbcTypeName)) {
			return -3;
		} else if ("LONGVARBINARY".equals(jdbcTypeName)) {
			return -4;
		} else if ("NULL".equals(jdbcTypeName)) {
			return 0;
		} else if ("OTHER".equals(jdbcTypeName)) {
			return 1111;
		} else if ("JAVA_OBJECT".equals(jdbcTypeName)) {
			return 2000;
		} else if ("DISTINCT".equals(jdbcTypeName)) {
			return 2001;
		} else if ("STRUCT".equals(jdbcTypeName)) {
			return 2002;
		} else if ("ARRAY".equals(jdbcTypeName)) {
			return 2003;
		} else if ("BLOB".equals(jdbcTypeName)) {
			return 2004;
		} else if ("CLOB".equals(jdbcTypeName)) {
			return 2005;
		} else if ("REF".equals(jdbcTypeName)) {
			return 2006;
		} else if ("DATALINK".equals(jdbcTypeName)) {
			return 70;
		} else if ("BOOLEAN".equals(jdbcTypeName)) {
			return 16;
		} else if ("ROWID".equals(jdbcTypeName)) {
			return -8;
		} else if ("NVARCHAR".equals(jdbcTypeName)) {
			return -15;
		} else if ("LONGNVARCHAR".equals(jdbcTypeName)) {
			return -16;
		} else if ("NCLOB".equals(jdbcTypeName)) {
			return 2011;
		} else if ("SQLXML".equals(jdbcTypeName)) {
			return 2009;
		} else {
			return 0;
		}
	}
	
	/**
	 * JDBC类型名称
	 */
	public static String getJdbcTypeName(int jdbctype) {
		String jdbcTypeName = "";
		switch (jdbctype) {
			case -7: jdbcTypeName = "BIT"; break;
			case -6: jdbcTypeName = "TINYINT"; break;
			case 5: jdbcTypeName = "SMALLINT"; break;
			case 4: jdbcTypeName = "INTEGER"; break;
			case -5: jdbcTypeName = "BIGINT"; break;
			case 6: jdbcTypeName = "FLOAT"; break;
			case 7: jdbcTypeName = "REAL"; break;
			case 8: jdbcTypeName = "DOUBLE"; break;
			case 2: jdbcTypeName = "NUMERIC"; break;
			case 3: jdbcTypeName = "DECIMAL"; break;
			case 1: jdbcTypeName = "CHAR"; break;
			case 12: jdbcTypeName = "VARCHAR"; break;
			case -1: jdbcTypeName = "LONGVARCHAR"; break;
			case 91: jdbcTypeName = "DATE"; break;
			case 92: jdbcTypeName = "TIME"; break;
			case 93: jdbcTypeName = "TIMESTAMP"; break;
			case -2: jdbcTypeName = "BINARY"; break;
			case -3: jdbcTypeName = "VARBINARY"; break;
			case -4: jdbcTypeName = "LONGVARBINARY"; break;
			case 0: jdbcTypeName = "NULL"; break;
			case 1111: jdbcTypeName = "OTHER"; break;
			case 2000: jdbcTypeName = "JAVA_OBJECT"; break;
			case 2001: jdbcTypeName = "DISTINCT"; break;
			case 2002: jdbcTypeName = "STRUCT"; break;
			case 2003: jdbcTypeName = "ARRAY"; break;
			case 2004: jdbcTypeName = "BLOB"; break;
			case 2005: jdbcTypeName = "CLOB"; break;
			case 2006: jdbcTypeName = "REF"; break;
			case 70: jdbcTypeName = "DATALINK"; break;
			case 16: jdbcTypeName = "BOOLEAN"; break;
			case -8: jdbcTypeName = "ROWID"; break;
			case -15: jdbcTypeName = "NVARCHAR"; break;
			case -16: jdbcTypeName = "LONGNVARCHAR"; break;
			case 2011: jdbcTypeName = "NCLOB"; break;
			case 2009: jdbcTypeName = "SQLXML"; break;
			case -150: jdbcTypeName = "OTHER"; break; //SQL Server的（sql_variant）
		}
		return jdbcTypeName;
	}
	
	/**
	 * Jdbc类型对应Java类型名称
	 */
	public static String getJavaClass(int jdbctype) {
		String javaClass = "";
		switch (jdbctype) {
			case -7: javaClass = "java.lang.Boolean"; break;
			case -6: javaClass = "java.lang.Integer"; break;
			case 5: javaClass = "java.lang.Short"; break;
			case 4: javaClass = "java.lang.Integer"; break;
			case -5: javaClass = "java.lang.Long"; break; 
			case 6: javaClass = "java.lang.Float"; break;
			case 7: javaClass = "java.lang.Float"; break;
			case 8: javaClass = "java.lang.Double"; break;
			case 2: javaClass = "java.math.BigDecimal"; break;
			case 3: javaClass = "java.math.BigDecimal"; break;
			case 1: javaClass = "java.lang.String"; break; 
			case 12: javaClass = "java.lang.String"; break;
			case -1: javaClass = "java.lang.String"; break;
			case 91: javaClass = "java.sql.Date"; break;
			case 92: javaClass = "java.sql.Time"; break;
			case 93: javaClass = "java.sql.Timestamp"; break;
			case -2: javaClass = "byte[]"; break;
			case -3: javaClass = "byte[]"; break;
			case -4: javaClass = "byte[]"; break;
			case 0: javaClass = "java.lang.Object"; break; //NULL
			case 1111: javaClass = "java.lang.Object"; break; //OTHER
			case 2000: javaClass = "java.lang.Object"; break; //JAVA_OBJECT
			case 2001: javaClass = "java.lang.Object"; break; //DISTINCT
			case 2002: javaClass = "java.lang.Object"; break; //STRUCT
			case 2003: javaClass = "java.lang.Object"; break; //ARRAY
			case 2004: javaClass = "java.lang.Object"; break; //BLOB
			case 2005: javaClass = "java.lang.Object"; break; //CLOB
			case 2006: javaClass = "java.lang.Object"; break; //REF
			case 70: javaClass = "java.lang.Object"; break; //DATALINK
			case 16: javaClass = "java.lang.Object"; break; //BOOLEAN
			case -8: javaClass = "java.lang.Object"; break; //ROWID
			case -15: javaClass = "java.lang.String"; break;
			case -16: javaClass = "java.lang.String"; break;
			case 2011: javaClass = "byte[]"; break; //SQLXML
			case 2009: javaClass = "java.lang.Object"; break;
			case -150: javaClass = "java.lang.Object"; break;
		}
		return javaClass;
	}
	/**
	 * 是否为大数据类型
	 * @param jdbctype JDBC数据类型
	 * @param size 数据宽度（对于字符串数据类型，宽度大于设定值时为大数据类型）
	 */
	public static boolean isBLOBType(int jdbctype, int size) {
		if (size >= StudioConst.BLOB_DATA_SIZE) {
			return true;
		}
		switch (jdbctype) {
			case -1: return true;
			case -2: return true;
			case -3: return true;
			case -4: return true;
			case 2004: return true;
			case 2005: return true;
			default: return false;
		}
	}

	/**
	 * 是否为大数据类型
	 * @param jdbctype JDBC数据类型
	 */
	public static boolean isBLOBType(int jdbctype) {
		return isBLOBType(jdbctype, 0);
	}
	
	/**
	 * Java类型名称
	 */
	public static String getShortJavaClass(int datatype) {
		String s = getJavaClass(datatype);
		int k = s.lastIndexOf(".");
		if (k > 0) {
			return s.substring(k + 1);
		} else {
			return s;
		} 			
	}

	/**
	 * 格式化字段名称，首字母小写，如：updateUserId
	 */
	public static String formatProperty(String colunm) {
		if (StringUtils.isBlank(colunm)) {
			return "";
		}
		String ss[] = colunm.split("_");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++) {
			if (StringUtils.isBlank(ss[i])) {
				continue;
			}
			if (ss[i].toUpperCase().equals(ss[i])) {
				StringBuffer s = new StringBuffer(ss[i].toLowerCase());
				s.setCharAt(0, Character.toUpperCase(s.charAt(0)));
				sb.append(s.toString());
			} else {
				StringBuffer s = new StringBuffer(ss[i]);
				s.setCharAt(0, Character.toUpperCase(s.charAt(0)));
				sb.append(s.toString());
			}
		}
		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0))); // 整个字符串首字母小写
		return sb.toString();
	}
	
	/**
	 * 格式化表的类名 如：SysAction
	 */
	public static String getTableBeanName(String tablename) {
		String ss[] = tablename.split("_");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ss.length; i++) {
			if (StringUtils.isBlank(ss[i])) {
				continue;
			}
			if (ss[i].toUpperCase().equals(ss[i])) {
				StringBuffer s = new StringBuffer(ss[i].toLowerCase());
				s.setCharAt(0, Character.toUpperCase(s.charAt(0)));
				sb.append(s.toString());
			} else {
				StringBuffer s = new StringBuffer(ss[i]);
				s.setCharAt(0, Character.toUpperCase(s.charAt(0)));
				sb.append(s.toString());
			}
		}
		return sb.toString();
	}

	/**
	 * 填充数据库字段的其他属性
	 */
	public static void fillField(Field field) {
		String property = formatProperty(field.getColumnName());
		String jdbcType = getJdbcTypeName(field.getDataType());
		String paramStr = "#{" + property +",jdbcType=" + jdbcType +"}";
		String recordParamStr = "#{record." + property +",jdbcType=" + jdbcType +"}";
		String methodGet = StringUtils.isNotBlank(property) ? "get" + property.substring(0, 1).toUpperCase() + property.substring(1) : "getX";
		String methodSet =  StringUtils.isNotBlank(property)? "set" + property.substring(0, 1).toUpperCase() + property.substring(1): "setX";
		String javaClass = getJavaClass(field.getDataType());
		String shorJavaClass = getShortJavaClass(field.getDataType());
		String property1 = StringUtils.isNotBlank(property)? property.substring(0, 1).toUpperCase() + property.substring(1) : "";
		
		field.setProperty(property);
		field.setProperty1(property1);
		field.setJdbcType(jdbcType);
		field.setParamStr(paramStr);
		field.setRecordParamStr(recordParamStr);
		field.setMethodGet(methodGet);
		field.setMethodSet(methodSet);
		field.setJavaClass(javaClass);
		field.setShorJavaClass(shorJavaClass);
	}
}

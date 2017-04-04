package com.newair.studioplugin.db;

public class Field {
	private String columnName; // COLUMN_NAME
	private String typeName; // TYPE_NAME
	private int dataType; // DATA_TYPE
	private int columnSize; // COLUMN_SIZE
	private int decimalDigits; // DECIMAL_DIGITS
	private String remarks; // REMARKS

	private boolean primaryKey;
	private boolean identity; //是否自增字段
	private boolean nullable; // 是否可为空

	private String property; //属性名称，格式如“updateUserId”
	private String property1; //属性名称(首字母大写)，格式如“UpdateUserId”
	private String jdbcType; //JDBC类型，如：VARCHAR
	private String paramStr; //参数串，如：#{updateDate,jdbcType=TIMESTAMP}
	private String recordParamStr; //参数串，如：#{record.updateUserId,jdbcType=BIGINT},
	private String methodGet;  //如：getUpdateUserId
	private String methodSet;  //如：setUpdateUserId
	private String javaClass; //对应Java类，如：java.lang.Integer
	private String shorJavaClass; //对应类，如： Integer

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getDataType() {
		return dataType;
	}

	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	public int getColumnSize() {
		return columnSize;
	}

	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	public String getJdbcType() {
		return jdbcType;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

	public void setParamStr(String paramStr) {
		this.paramStr = paramStr;
	}

	public String getParamStr() {
		return paramStr;
	}

	public void setRecordParamStr(String recordParamStr) {
		this.recordParamStr = recordParamStr;
	}

	public String getRecordParamStr() {
		return recordParamStr;
	}

	public void setMethodGet(String methodGet) {
		this.methodGet = methodGet;
	}

	public String getMethodGet() {
		return methodGet;
	}

	public void setMethodSet(String methodSet) {
		this.methodSet = methodSet;
	}

	public String getMethodSet() {
		return methodSet;
	}

	public void setJavaClass(String javaClass) {
		this.javaClass = javaClass;
	}

	public String getJavaClass() {
		return javaClass;
	}

	public void setShorJavaClass(String shorJavaClass) {
		this.shorJavaClass = shorJavaClass;
	}

	public String getShorJavaClass() {
		return shorJavaClass;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	public String getProperty1() {
		return property1;
	}

	public boolean isIdentity() {
		return identity;
	}

	public void setIdentity(boolean identity) {
		this.identity = identity;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
}

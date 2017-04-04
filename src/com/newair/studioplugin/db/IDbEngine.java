package com.newair.studioplugin.db;

public interface IDbEngine {
	/**
	 * 获取当前用户所有列表
	 */
	public String[] getAllTable();
	
	/**
	 * 获取当前用户表
	 */
	public String[] getTableLikeName(String name);
	
	/**
	 * 获取表的字段定义
	 */
	public Field[] getColumnDefine(String tabelname);
	
	/**
	 * 执行查询
	 */
	public void ExcuteQuery (String sql, Object[] params, IDoResult doresult);
	
	/**
	 * 执行SQL语句
	 */
	public void ExecteUpdate (String sql, Object[] params, boolean autoCommit, IDoResult doresult);
	
}

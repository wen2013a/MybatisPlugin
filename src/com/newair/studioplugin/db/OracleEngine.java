package com.newair.studioplugin.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OracleEngine implements IDbEngine {

	public String[] getAllTable() {
		String sql = "select name from sysobjects where type='U' and xtype='U' order by name";
		List<String> list = new ArrayList<String>();
		try {
			Connection conn = null;
			try {
				conn = DbUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					list.add(rs.getString("name"));
				}
				rs.close();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.toArray(new String[] {});
	}

	public String[] getTableLikeName(String name) {
		String sql = "select name from sysobjects where type='U' and xtype='U' where name like %" + name + "% order by name";
		List<String> list = new ArrayList<String>();
		try {
			Connection conn = null;
			try {
				conn = DbUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					list.add(rs.getString("name"));
				}
				rs.close();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.toArray(new String[] {});
	}

	public Field[] getColumnDefine(String tabelname) {
		List<Field> list = new ArrayList<Field>();
		try {
			Connection conn = null;
			try {
				conn = DbUtil.getConnection();
				DatabaseMetaData dmd = conn.getMetaData();
				ResultSet rs = dmd.getColumns(null, "%", tabelname, "%");
				while (rs.next()) {
					Field field = new Field();
					field.setColumnName(rs.getString("COLUMN_NAME"));
					field.setDataType(rs.getInt("DATA_TYPE"));
					field.setTypeName(rs.getString("TYPE_NAME"));
					field.setColumnSize(rs.getInt("COLUMN_SIZE"));
					field.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
					field.setNullable(rs.getInt("NULLABLE") != 0);
					field.setRemarks(rs.getString("REMARKS"));
					
					boolean identity = isIdentityColumn(tabelname, field.getColumnName());
					field.setIdentity(identity);
					DbUtil.fillField(field);
					list.add(field);
				}
				rs = dmd.getPrimaryKeys(null, null, tabelname);
				while (rs.next()) {
					String columnName = rs.getString("COLUMN_NAME");
					for (int i = 0; i < list.size(); i++) {
						Field field = list.get(i);
						if (columnName.equals(field.getColumnName())) {
							field.setPrimaryKey(true);
							break;
						}
					}
				}
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.toArray(new Field[] {});
	}

	public void ExcuteQuery (String sql, Object[] params, IDoResult doresult) {
		try {
			Connection conn = null;
			try {
				conn = DbUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				for (int i = 0; (params != null) && (i < params.length); i++) {
					ps.setObject(i + 1, params[i]);
				}
				ResultSet rs = ps.executeQuery();
				doresult.DoResutl(rs);
				rs.close();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			doresult.DoError(e);
		}
	}

	public void ExecteUpdate(String sql, Object[] params, boolean autoCommit, IDoResult doresult) {
		try {
			Connection conn = null;
			try {
				conn = DbUtil.getConnection();
				conn.setAutoCommit(autoCommit);
				PreparedStatement ps = conn.prepareStatement(sql);
				for (int i = 0; (params != null) && (i < params.length); i++) {
					ps.setObject(i + 1, params[i]);
				}
				int result = ps.executeUpdate();
				doresult.DoUpdate(conn, result);
			} finally {
				if (autoCommit) {
					conn.commit();
					conn.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			doresult.DoError(e);
		}
	}

	/**
	 * 获取自增字段
	 */
	@SuppressWarnings("unused")
	private String[] getIdentityColumn(String tablename) {
		String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.columns "
				+ " WHERE TABLE_NAME='"+tablename+"' AND COLUMNPROPERTY( "    
				+ " OBJECT_ID('"+tablename+"'),COLUMN_NAME,'IsIdentity')=1 ";
		List<String> list = new ArrayList<String>();
		try {
			Connection conn = null;
			try {
				conn = DbUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					list.add(rs.getString("name"));
				}
				rs.close();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.toArray(new String[] {});
	}
	
	/**
	 * 判断是否为自增字段
	 */
	private boolean isIdentityColumn(String tablename, String columnName) {
		String sql = " SELECT COLUMNPROPERTY( OBJECT_ID('"+tablename+"'),'"+columnName+"','IsIdentity') as is_identity ";
		boolean flag = false;
		try {
			Connection conn = null;
			try {
				conn = DbUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					int res = rs.getInt("is_identity");
					flag = (res == 1);
				}
				rs.close();
			} finally {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}

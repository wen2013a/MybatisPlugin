package com.newair.studioplugin.db;

import java.sql.Connection;
import java.sql.ResultSet;

public interface IDoResult {
	
	public void DoResutl(ResultSet rs);
	
	public void DoUpdate(Connection conn, int reflectCount);
	
	public void DoError(Exception e);
	
}

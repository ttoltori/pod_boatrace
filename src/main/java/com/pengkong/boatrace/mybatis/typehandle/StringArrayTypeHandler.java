package com.pengkong.boatrace.mybatis.typehandle;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setObject(i, parameter);
	}

	@Override
	public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return extractArray(rs.getArray(columnName));
	}

	@Override
	public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return extractArray(rs.getArray(columnIndex));
	}

	@Override
	public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return extractArray(cs.getArray(columnIndex));
	}

	protected String[] extractArray(Array array) throws SQLException {
		if (array == null) 
			return null;
		Object javaArray = array.getArray();
		
		return (String[])javaArray;
	}
}

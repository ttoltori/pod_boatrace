package com.pengkong.boatrace.mybatis.typehandle;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class IntegerArrayTypeHandler extends BaseTypeHandler<int[]> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, int[] parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setObject(i, parameter);
	}

	@Override
	public int[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return extractArray(rs.getArray(columnName));
	}

	@Override
	public int[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return extractArray(rs.getArray(columnIndex));
	}

	@Override
	public int[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return extractArray(cs.getArray(columnIndex));
	}

	protected int[] extractArray(Array array) throws SQLException {
		if (array == null) 
			return null;
		Object javaArray = array.getArray();
		
		return ArrayUtils.toPrimitive((Integer[])javaArray);
	}

}

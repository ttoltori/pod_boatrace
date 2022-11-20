package com.pengkong.boatrace.mybatis.typehandle;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class DoubleArrayTypeHandler extends BaseTypeHandler<double[]> {

	public DoubleArrayTypeHandler()  {
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, double[] parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setObject(i, parameter);
//		Double[] values = ArrayUtils.toObject(parameter);
//		Array arr = ps.getConnection().createArrayOf("double precision[]", values);
//		ps.setArray(i,arr);
	}

	@Override
	public double[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return extractArray(rs.getArray(columnName));
	}

	@Override
	public double[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return extractArray(rs.getArray(columnIndex));
	}

	@Override
	public double[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return extractArray(cs.getArray(columnIndex));
	}
	
	protected double[] extractArray(Array array) throws SQLException {
		if (array == null) 
			return null;
		Object javaArray = array.getArray();
		
		return ArrayUtils.toPrimitive((Double[])javaArray);
	}
}

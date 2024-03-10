package com.pengkong.boatrace.online.dao;

import org.apache.ibatis.session.SqlSession;

public abstract class AbstractDAO<E> {

	SqlSession session;
	
	public AbstractDAO(SqlSession session) {
		super();
		this.session = session;
	}

	public int insert(E e) {
		return insertImplementation(e);
	}

	public int update(E e) {
		return updateImplementation(e);
	}

	/** insert */
	public abstract int insertImplementation(E e);
	
	/** update */
	public abstract int updateImplementation(E e);
}

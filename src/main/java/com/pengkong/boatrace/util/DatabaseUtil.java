package com.pengkong.boatrace.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DatabaseUtil {

	private static SqlSession session;
	private static boolean isAutoCommit;
	private static boolean isOpened = false;
	
	/** 自動コミットSqlSessionキュー */
	private static ConcurrentLinkedDeque<SqlSession> autoCommitQueue = new ConcurrentLinkedDeque<>();
	
	/** 受動コミットSqlSessionキュー */
	private static ConcurrentLinkedDeque<SqlSession> nonAutoCommitQueue = new ConcurrentLinkedDeque<>();
	
	public DatabaseUtil() {
	}

	/**
	 * SqlSessionを取得する。
	 * @param targetDb mybatis設定ファイル名
	 * @param isAutoCommit 自動コミット
	 */
	public static SqlSession open(String targetDb, boolean isAutoCommit) throws IOException{
        InputStream inputStream = Resources.getResourceAsStream(targetDb);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory.openSession(false);
	    
//	    
//		if (isAutoCommit) {
//			return getAutoCommitSession(targetDb);
//		}
//		
//		return getNonAutoCommitSession(targetDb);
	}

	/** SqlSessionを返却する */
	public static void close(SqlSession session) {
		if (session == null) {
			return;
		}

		session.close();
	    session = null;
	    
//		if (session.getConnection().getAutoCommit()) {
//			autoCommitQueue.add(session);
//		} else {
//			nonAutoCommitQueue.add(session);
//		}
	}

	/** キューで保持していたSqlSessionを全てcloseする */
	public static void clearSessions() {
		SqlSession session;
		while((session = autoCommitQueue.poll()) != null ) {
			session.close();
			session = null;
		}
		while((session = nonAutoCommitQueue.poll()) != null ) {
			session.close();
			session = null;
		}
	}
	
	/** 自動コミットSqlSessionを取得する。キューになければ新規生成する */
	private static SqlSession getAutoCommitSession(String targetDb) throws IOException {
		SqlSession session = autoCommitQueue.poll();
		if (session == null) {
			InputStream inputStream = Resources.getResourceAsStream(targetDb);
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			session = sqlSessionFactory.openSession(true);
		}
		
		return session;
	}

	/** 受動コミットSqlSessionを取得する。キューになければ新規生成する */
	private static SqlSession getNonAutoCommitSession(String targetDb) throws IOException {
		SqlSession session = nonAutoCommitQueue.poll();
		if (session == null) {
			InputStream inputStream = Resources.getResourceAsStream(targetDb);
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			session = sqlSessionFactory.openSession(false);
		}
		
		return session;
	}
	
	@Deprecated
	public static boolean isOpened() {
		return isOpened;
	}
	
	@Deprecated
	public static SqlSession getSession() {
		return session;
	}
	
	@Deprecated
	public static void commit() {
		if (!isAutoCommit)
			session.commit();
	}
	
	@Deprecated
	public static void rollback() {
		if (!isAutoCommit) {
			session.rollback();
		}
	}
	
	@Deprecated
	public static void close() {
		session.close();
		session = null;
		isOpened = false;
	}
}

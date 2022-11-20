package com.pengkong.boatrace.online.race;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.ibatis.session.SqlSession;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;
import com.pengkong.boatrace.mybatis.entity.OlRace;
import com.pengkong.boatrace.mybatis.entity.OlRaceExample;
import com.pengkong.boatrace.online.dao.OlRaceDAO;
import com.pengkong.boatrace.util.DatabaseUtil;

/**
 * 当日の投票対象レースを保持する
 * @author ttolt
 *
 */
public abstract class AbstractDailyRaceQueue {
	MLPropertyUtil prop =  MLPropertyUtil.getInstance();

	/* レースキュー */
	Queue<OlRace> queue;

	/** 日付 */
	String ymd;
	
	public AbstractDailyRaceQueue(String ymd) {
		super();
		this.ymd = ymd;
	}
	
	/** poll時間を短縮するために外部からあらかじめ初期化を行うこと 
	 * @throws SQLException */
	public void ensureInitialized() throws IOException, SQLException{
		if (queue == null) {
			queue = new LinkedList<OlRace>();
			loadRaces(ymd).stream().forEach(race -> queue.add(race));
		}
	}

	public OlRace peek() {
		return queue.peek();
	}
	
	public OlRace poll() {
		return queue.poll();
	}

	public int size() {
		return queue.size();
	}
	
	/** 指定日付のOlRaceを取得する。
	 * @throws SQLException */ 
	List<OlRace> loadRaces(String ymd) throws IOException, SQLException {
		List<OlRace> results;
		SqlSession session = DatabaseUtil.open(prop.getString("target_db_resource"), false);
		OlRaceDAO dao = new OlRaceDAO(session);
		OlRaceExample example = createExample();
		
		// レースを取得する
		results = dao.select(example);
		
		DatabaseUtil.close(session);
		return results;
	}
	
	abstract OlRaceExample createExample();
}

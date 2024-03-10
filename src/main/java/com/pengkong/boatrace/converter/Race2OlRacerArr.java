package com.pengkong.boatrace.converter;

import com.pengkong.boatrace.model.Race;
import com.pengkong.boatrace.model.Waku;
import com.pengkong.boatrace.mybatis.entity.OlRacerArr;
import com.pengkong.common.MathUtil;

public class Race2OlRacerArr {
	public static OlRacerArr convert(Race race) {
		OlRacerArr rec = new OlRacerArr();
		
		int[] entry = new int[6];
		String[] sex = new String[6];
		int[] age = new int[6];
		String[] level = new String[6];
		double[] weight = new double[6];
		String[] branch = new String[6];
		int[] flying = new int[6];
		int[] late = new int[6];
		double[] avgstart = new double[6];
		double[] nationwiningrate = new double[6];
		double[] nation2winingrate = new double[6];
		double[] nation3winingrate = new double[6];
		double[] localwiningrate = new double[6];
		double[] local2winingrate = new double[6];
		double[] local3winingrate = new double[6];
		int[] motorno = new int[6];
		double[] motor2winingrate = new double[6];
		double[] motor3winingrate = new double[6];
		
		for (int i = 0; i < race.wakuList.size(); i++) {
			Waku waku = race.wakuList.get(i);
			entry[i] = Integer.valueOf(waku.entry);
			sex[i] = waku.sex;
			age[i] = waku.age;
			level[i] = waku.level;
			weight[i] = MathUtil.scale1((double)waku.weight);
			branch[i] = waku.branch;
			flying[i] = waku.flying;
			late[i] = waku.late;
			avgstart[i] = MathUtil.scale2((double)waku.averageStart);
			nationwiningrate[i] = MathUtil.scale2((double)waku.nationWiningRate);
			nation2winingrate[i] = MathUtil.scale2((double)waku.nation2WiningRate);
			nation3winingrate[i] = MathUtil.scale2((double)waku.nation3WiningRate);
			localwiningrate[i] = MathUtil.scale2((double)waku.localWiningRate);
			local2winingrate[i] = MathUtil.scale2((double)waku.local2WiningRate);
			local3winingrate[i] = MathUtil.scale2((double)waku.local3WiningRate);
			motorno[i] = Integer.valueOf(waku.motorNo);
			motor2winingrate[i] = MathUtil.scale2((double)waku.motor2WiningRate);
			motor3winingrate[i] = MathUtil.scale2((double)waku.motor3WiningRate);
		}

		rec.setYmd(race.raceInfo.ymd);
		rec.setJyocd(race.setu.jyoCd);
		rec.setRaceno(race.raceInfo.no);
		rec.setEntry(entry);
		rec.setSex(sex);
		rec.setAge(age);
		rec.setLevel(level);
		rec.setWeight(weight);
		rec.setBranch(branch);
		rec.setFlying(flying);
		rec.setLate(late);
		rec.setAveragestart(avgstart);
		rec.setNationwiningrate(nationwiningrate);
		rec.setNation2winingrate(nation2winingrate);
		rec.setNation3winingrate(nation3winingrate);
		rec.setLocalwiningrate(localwiningrate);
		rec.setLocal2winingrate(local2winingrate);
		rec.setLocal3winingrate(local3winingrate);
		rec.setMotorno(motorno);
		rec.setMotor2winingrate(motor2winingrate);
		rec.setMotor3winingrate(motor3winingrate);
		
		return rec;
	}
}

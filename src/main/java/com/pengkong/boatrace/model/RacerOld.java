package com.pengkong.boatrace.model;

public class RacerOld {

	public int entry;
	public String name;
	public int age;
	public String level;
	// 現在レベルの経歴日数
	//public int daysFromCurrentLevel;
	//public int weight;
	public String branch;
	
	// 支部の管轄場コード
	public String branchJyoCd;
	
	// 展示タイム min, max, avg
	public float[] exhibit = new float[3];
	
	// スタート展示タイム min, max, avg
	public float[] startExhibit = new float[3];
	
	// 全国勝率  min, max, avg
	public float[] nationWinningRate = new float[3];
	
	// 当地勝率  min, max, avg
	public float[] localWinningRate = new float[3];
	
	// 一着時のA級数の合計
	public int winningALevelCount;
	
	// 一着時の決まり手毎の回数(逃げ、差し、まくり差し、まくり、抜き）
	public int[] kimariteCount = new int[5];
	
	//一着時の決まり手毎の割合(個人) (逃げ、差し、まくり差し、まくり、抜き）
	public int[] kimaritePercent = new int[5];
	
	// 一着時の枠毎の回数 (1~6枠）
	public int[] wakuCount = new int[6];
	
	// 一着時の枠毎の割合(個人) (1~6枠）
	public int[] wakuPercent = new int[6];
	
	public int[] prize = new int[4];
	
	public RacerOld() {
		
	}

}

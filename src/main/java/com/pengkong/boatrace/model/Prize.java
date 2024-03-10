package com.pengkong.boatrace.model;

/**
 * 100円単位でベッティングされる金額
 *  
種類=ベット金額
----------------
３連単ベット（1位固定）=600
３連単ベット（１、2位固定）=200
３連単ベット（全部）=2400
3連複ベット=300
２連単（１固定）=300
２連単（全部）=1200
２連複=600
単勝=100
複勝(１位固定）=300
複勝(１、２位全部）=200

 * @author qwerty
 *
 */
public class Prize {
	public int sanrentan1Fix;
	public int sanrentan12Fix;
	public int sanrentanAll;
	public int sanrenhuku;
	public int nirentan1Fix;
	public int nirentanAll;
	public int nirentanhuku;
	public int tansyo;
	public int hukuyo1Fix;
	public int hukuyo12All;
}

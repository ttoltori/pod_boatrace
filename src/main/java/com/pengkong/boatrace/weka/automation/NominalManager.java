package com.pengkong.boatrace.weka.automation;

import java.util.Arrays;
import java.util.List;

public class NominalManager {

	public static class NominalManagerInstanceHolder {
		private static final NominalManager INSTANCE = new NominalManager();
	}
	
	public NominalManager() {
	}
	
	public static NominalManager getInstance() {
		return NominalManagerInstanceHolder.INSTANCE;
	}
	
	public List<String> getNominalAttrList(String nominalName) {
		String attrs = getNominalAttr(nominalName);
		attrs = attrs.replace("{", "");
		attrs = attrs.replace("}", "");
		List<String> result = Arrays.asList(attrs.split(","));
		return result;
	}
	
	public String getNominalAttr(String nominalName) {
		if(nominalName.endsWith("rank")) {
			return "{" + getRank() + "}";
		} else if(nominalName.endsWith("racetype_simple") || nominalName.equals("racetype")) {
			return "{" + getRaceTypeSimple() + "}";
		}else if(nominalName.endsWith("waku")) {
			return "{" + getWaku() + "}";
		} else if(nominalName.contains("rank_")) {
			return "{" + getNominalRank() + "}";
		}else if(nominalName.startsWith("entry")) {
			return "{" + getEntry() + "}";
		} else if(nominalName.startsWith("motorno")) {
			return "{" + getMotorno() + "}";
		} else if(nominalName.startsWith("sex")) {
			return "{" + getSex() + "}";
		} else if(nominalName.startsWith("level")) {
			return "{" + getLevel() + "}";
		} else if(nominalName.startsWith("branch")) {
			return "{" + getBranch() + "}";
		} else if(nominalName.startsWith("jyocd")) {
			return "{" + getJyocd() + "}";
		} else if(nominalName.startsWith("raceno")) {
			return "{" + getRaceno() + "}";
		} else if(nominalName.startsWith("turn")) {
			return "{" + getTurn() + "}";
		} else if(nominalName.startsWith("racetype")) {
			return "{" + getRaceType() + "}";
		} else if(nominalName.startsWith("timezone")) {
			return "{" + getTimezone() + "}";
		} else if(nominalName.startsWith("winddirection")) {
			return "{" + getWindDirection() + "}";
		} else if(nominalName.startsWith("n_nationwiningrate")) {
			return "{" + getNtileNationRate() + "}";
		} else if(nominalName.startsWith("alcount") || nominalName.startsWith("alevelcount")) {
			return "{" + getAlcount() + "}";
		} else if(nominalName.startsWith("kumiban201")) {
			return "{" + getKumiban201() + "}";
		} else if(nominalName.startsWith("femalecount")) {
			return "{" + getFemaleCount() + "}";
		} else if(nominalName.startsWith("fixedentrance")) {
			return "{" + getFixedEntrance() + "}";
		} else if(nominalName.startsWith("grade")) {
			return "{" + getGrade() + "}";
		} else if(nominalName.equals("wakulevel12")) {
			return "{" + getWakuLevel12() + "}";
		} else if(nominalName.equals("wakulevel123")) {
			return "{" + getWakuLevel123() + "}";
		} else if(nominalName.equals("mm")) {
			return "{" + getMM() + "}";
		}else if(nominalName.startsWith("pd")) {
			return "{" + getWaku() + "}";
		}else if(nominalName.equals("bettype")) {
			return "{" + getBettype() + "}";
		}else if(nominalName.equals("pd123")) {
			return "{" + getPrediction123() + "}";
		} else {
			return null;
		}
	}
	
	private String getGrade() {
		return "ip,G3,G2,G1,SG";
	}
	
	private String getFixedEntrance() {
		return "N,Y,F";
	}
	
	private String getNominalRank() {
		return "1,2,3,4,5,6";
	}
	
	public String getPrediction123() {
		return "123,124,125,126,132,134,135,136,142,143,145,146,152,153,154,156,162,163,164,165,213,214,215,216,231,234,235,236,241,243,245,246,251,253,254,256,261,263,264,265,312,314,315,316,321,324,325,326,341,342,345,346,351,352,354,356,361,362,364,365,412,413,415,416,421,423,425,426,431,432,435,436,451,452,453,456,461,462,463,465";
	}

	private String getEntry() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1485; i <= 6000; i++ ) {
			sb.append(String.valueOf(i));
			sb.append(",");
		}
		
		String result = sb.toString();
		return result.substring(0,result.length()-1);
	}
	
	private String getMotorno() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 99; i++ ) {
			sb.append(String.valueOf(i));
			sb.append(",");
		}
		
		String result = sb.toString();
		return result.substring(0,result.length()-1);
	}
	
	private String getNtileNationRate() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 100; i++ ) {
			sb.append(String.valueOf(i));
			sb.append(",");
		}
		
		String result = sb.toString();
		return result.substring(0,result.length()-1);
		
	}
	
	public String getFemaleCount() {
		return "0,1,2,3,4,5,6";
	}
	
	public String getTimezone() {
		return "N,M,S,Z";
	}
	
	public String getWindDirection() {
		return "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,else";
	}
	
	public String getSex() {
		return "M,F";
	}
	
	public String getLevel() {
		return "A1,A2,B1,B2";
	}
	
	public String getWaku() {
		return "1,2,3,4,5,6";
	}
	
	public String getBranch() {
		return "佐賀,山口,徳島,広島,三重,群馬,長崎,埼玉,東京,福岡,香川,岡山,兵庫,静岡,福井,大阪,愛知,滋賀";
	}
	
	public String getJyocd() {
		return "01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24";
	}
	
	public String getRaceno() {
		return "1,2,3,4,5,6,7,8,9,10,11,12";
	}
	
	public String getTurn() {
		return "1,2,3,4,5,6,7";
	}
	
	public String getRaceType() {
		return "東京ドリーム戦,ペア対抗特賞,からっキー特選,男予選記者特選,特別選抜Ｃ戦,わかちゃま特別,準々優勝戦,Ａ級ドリーム,ＳＰマッチ,男子準優勝戦,予選記者選抜,男子特賞,レイクス選抜戦,男子選抜戦,対抗記者特選,森下仁丹ＤＲ,住信ＳＢＩ優勝,地区バトル,夜ドキ戦一般,セミファイナル,ゴゴイチびわこ,韋駄天ドリーム,カニ坊選抜,特別選抜Ａ戦,女子選抜戦,予選サン選抜,プレモル選抜,プライド予選,ＪＬＣ特選,朝得ガァ～コ戦,さん太選抜,ダイスポＤＲ,予選,フェアリーＤＲ,ヴィーナス特選,プリンセス選抜,男子予選特選,サントリ選抜戦,若鷲選抜,Ｂ級準優勝戦,大郷選抜戦,一般ＧＷ特選,ヤング特選,闘将ドリーム戦,タッチャン選抜,大晦日特選,男子特選,地区バトル一般,うずひめＤＲ,ビーナス選抜戦,おはよう戦Ｐ,静波まつり選抜,中吉選抜,フェアリー特選,開設記念選抜,本命バトル予選,かっぱ特別戦,西スポ記者特選,ピースター特選,スピード王優勝,金刀比羅宮奉納,男・準優勝戦,大スポ選抜戦,なるちゃん選抜,めざまし戦一般,Ｂ級選抜戦,舞鶴特選,Ｕ－３４選抜戦,プレモル選抜戦,河内音頭ＤＲ,カフェタイム,ペア対抗特選,唐津特選,記者選抜,ＳＢＩ選抜,招福ドリーム,本命バトル特賞,ダイスポ選抜,女・ドリーム戦,神無月バトル,まつりだｏｎｅ,テレボートＤＲ,イケメン選抜,ナイター王選抜,アフター特賞,１ｓｔ,サッポロＤＲ,男・予選特賞,特別選抜Ａ,びわこ幕開け戦,響選抜戦,リップルＤ戦,準Ｇ・Ａ戦,特賞,ツッキー優勝戦,長月バトル,板野選抜,Ｂ級予選,鷲羽選抜,日刊ドリーム,男・予選,ファン選抜Ｄ戦,サンライズＤ,まじんＤＲ,黒潮特選,一般Ｊ７特選,昼ガチ戦予戦,一般まる特賞,新春めざまし戦,日替艇食戦,中スポ選抜戦,デイリーＤＲ,報知選抜,おはよう戦,ツッピー選抜戦,個性派王選抜戦,大吉選抜,ツッピーＤＲ戦,かぐや姫ＤＲ,スピード王特選,ランチタイム戦,お盆選抜,Ｂ級特選,Ｓヘビー級予選,ラピート特選,島崎和歌子杯,一般選抜,ダイヤモンド,ＢＢドリーム,サンスポ特選,夜ガチ戦一般,夕ドキ戦予選,発祥地ドリーム,ＵＣＣ選抜戦,女子予選,ＳＡＫＡＩＤＲ,金銀トライアル,おはよう特選,楽天選抜,おやじ特選,王将位決定戦,松茂選抜戦,松茂選抜,予選特賞戦,ダイヤモンドＤ,Ｂ級予選特賞,Ｗ準優後半戦,北九州選抜特選,予選ラリー選抜,金銀戦,ＯＢドリーム,カステラＤＲ,夕やけ特選,テレボート選抜,ＴＷドリーム,トワイライト８,江戸川女子選抜,日刊特選,いぶし銀ＤＲ,スゴ６！びわこ,西スポ特選,予選ドリーム戦,ひるトク一般戦,対抗予選特賞,カステラ選抜,スポニチＤＲ,ターゲット９,優勝戦,新スタンドＯＲ,いばら選抜,カステラ特選,澤乃井優勝戦,選抜戦Ｂ,天領ドリーム,多摩川選抜戦,対抗桐生特選,記者特選,準優勝戦,山口新聞記者特,ナイターセブン,はにたんＤＲ,１ＳＴ特賞,メガトン特賞,一般特賞戦,Ｂ級優勝戦,ニッカン選抜,神無月ドリーム"
			+ ",アシ夢ドリーム,ガァ〜コ選抜,中国醸造特選,瀬戸大橋選抜,日刊ドリーム戦,女・準優勝戦,国分寺市選抜戦,予選初夢賞,皐月選抜,はまぼう特選,ツッキーＤＲ戦,みやじマリ特選,ふく〜る戦,対抗予選,石清水選抜戦,地区ファイナル,是政女王決定戦,トーキョーベイ,一般わく枠賞,レジャチャン,順位決定戦,ナイター王優勝,フラワー特選,カリスマＤＲ,ヘビー級王決定,新緑選抜戦,男１対女５特選,サントリー優勝,板野特選,是政名人決定戦,東村山市選抜戦,カリスマ特選,男・選抜戦,１ｓｔ特賞,津ぎょうざＤＲ,あさトク予選,予選スマ選抜,東京ベイ特選,ツッピー優勝戦,住之江ファイブ,３支部ガチＤＲ,対抗ドリーム,ペラ坊特賞,神無月特選,一撃,夕やけ特賞,優出トライアル,ラリー選抜,蒼天選抜戦,澤乃井選抜戦,ペア対抗選抜,テレッペＤＲ,ニューイヤーＤ,おもしろ特賞,楽天特選,サンスポ選抜,初優勝決定戦,ゆずるドリーム,一般記者特選,準優進出戦,すみしん選抜,三重選抜ＤＲ戦,アフター５,澤乃井ファイナ,日野市選抜戦,桐生選抜戦,北島選抜,ファイナル,地区バトル特選,本命波乱戦予選,ちゃんこタイム,特別選抜戦,ひめちゃん特別,メモリアル優勝,地区対抗戦特賞,波乗りドリーム,新春特選,くらしき選抜,なるちゃんＤＲ,予選特別戦,バニビ選抜戦,静岡放送特選,松茂特選,予選特選戦,新春特別選抜戦,夕ガチ戦予選,福岡選抜,バニビ優勝戦,１ｓｔバトル,フマキラーＤＲ,モミジ特選,大阪ドリーム,やわた選抜戦,ＧＷ特選,団体・優勝戦,新春ドリーム,同期戦,北欧の風選抜戦,ソイカラＤＲ,コジポ選抜,アフタドリーム,キリン選抜戦,ティータイム,九スポ特選,戸田選抜戦,ヘビー級ＤＲ,一般記者選抜,Ｂ級一般戦,ＧＰＦ選抜,モミジドリーム,昼ドキ戦予選,個性派王優勝戦,ピースター,快速王ドリーム,レナ＆リサ選抜,日刊スポ選抜,オオムラＧＰ,東洋観光ＤＲ,下電ドリ－ム,じぶん銀行優勝,ゴゴイチ！特賞,１ｓｔ特選,ＳＯＹＳＨ特選,夕ガチ戦一般,下電ドリーム,女・予選,１ＳＴ,団体・選抜Ａ戦,個性派王優勝,予選バディ選抜,モーニング,ホットマン優勝,２ＮＤ,宮島特選,地区王優勝,おやじドリーム,うめぼー選抜,昇龍特選,夜ドキ戦予選,ＩＢＡＲＡ選抜,Ａ級予選,センプル選抜戦,予選特別,レジャチャンＤ,びわこ選抜戦,昼ドキ戦一般,大吟醸選抜戦,年忘れ選抜,超抜モーター戦,ひるトクトク,一般特別戦,モンスターＤＲ,昼ガチ戦一般,マクール選抜,飛車角戦,本命バトル特選,スポニチ選抜,地区王選抜,もみじドリーム,夜王選抜戦,ハードレース,初夢特選,是政プリンス,ｅ‐男選抜戦,おもしろ特選,おはようＤＲ,サントリー選抜,平和島選抜戦,ヤクルト特選,地区王特選一般,予選まる特賞,一般サン選抜,ジムビーム選抜,昼サンサン,マリンＤＲ,めざまし戦予選,静波まつりＤ戦,板野選抜戦"
			+ ",テレッペ特選,ＪＬＣ選抜,女・優勝戦,ヴィーナスＤＲ,サンライズ選抜,ＭＡＸ・邦丸賞,Ａ級優勝戦,ドリーム,選抜戦Ａ,優勝戦進出決定,おもしろ特選一,ピース姫選抜戦,お正月特選,Ｂランチ戦,男・予選特選,男・優勝戦,一般スマ選抜,Ｂ級ドリーム,群雄割拠選抜戦,特選,対抗記者選抜,ファーストＢ,うめぼーＤＲ,大スポドリーム,女・予選特賞,カニ坊準優勝戦,カモンＦＭ特選,６地区選抜,サントリーＤ戦,報知特選,シモデンＤＲ,最速王ドリーム,団体・ドリーム,ひるトク予選,マスターズＤＲ,個性派王特選,ジュエル７特選,男１対女５予選,デイリー選抜,こじま選抜,あさガチ,スポニチ選抜戦,選抜トライアル,江戸川男子選抜,ＧＯＬＤＤＲ,つるや選抜戦,団体・一般,男・ドリーム戦,最速王Ｄ,津ぎょうざ選抜,予選ペプシ選抜,ボーナスバトル,夜王チャレンジ,グランプリ特選,戸田特選,リップル選抜戦,スピード王選抜,ドリボート賞,モーニング予選,一般戦,ＳＯＹＳＨドリ,一般ペプシ選抜,女・予選特選,朝ガチ戦予戦,小吉選抜,記者選抜戦,広テレドリーム,ピース姫選抜,ふく～る戦,ツッピーレース,２ｎｄ特賞,一般お正月特選,是政王子決定戦,静岡新聞社特選,地区対抗戦予選,北島特選,報知選抜戦,おもしろ特別,ガチンコ,名匠特選,朝ガチ戦一般,一般桐生特選,ウエスタンＤＲ,モルツ選抜戦,一般ブルー選抜,対抗ドリーム戦,じぶん銀行選抜,ＰＲＦ選抜戦,スカパーＪＬＣ,トーナメント,蛭子選考ＤＲ,選抜Ｂ戦,つつみん選抜戦,わくわく特選,本命波乱戦特賞,地区対抗大将戦,那の津選抜,昼ガチ戦予選,女予選記者特選,おはよう特賞,新春特別選抜,初夢ドリーム,おかわり！,サンライズＤ戦,やまだ屋ＤＲ,女子特選,蛭子優勝戦,予選ブルー選抜,ウインク選抜,中国醸造ＤＲ,サンスポＤＲ,２ｎｄ,予選報知選抜,Ｂガール選抜,アサヒドリーム,平和島選抜,蒲郡ドリーム戦,てっぱん,あさトク一般戦,うずしお選抜,みやじマリＤＲ,２ｎｄ特選,★サッポロ特選,祝やわた十周年,トーター選抜,ドリームレース,ヘビ−級ＤＲ,びわこ若鮎戦,団体・予選,ドリーム戦,うめぴー選抜,Ｇ・Ａ決定戦,ツーコ選抜戦,朝得ガァ〜コ戦,優勝,北島選抜戦,２ｎｄステージ,午後の一撃,ルーキー選抜戦,平和島劇場２年,楽天ドリーム,新春選抜戦,予選まじん選抜,予選選抜,地区王選抜戦,蒲郡ドリーム,プリンセス特選,びわこ新春戦,みくにあさガチ,Ａ級準優勝戦,ＦＴＥＣ選抜戦,ガァ～コ選抜,Ｗ準優勝戦前半,Ａ級一般戦,２ッキーレース,イーバンク特選,Ａ級特選,発祥地選抜,地区バトル特賞,予選トコタン賞,クラリス選抜,アフタ記者特選,マリン特選,予選記者特選,ファン選抜戦,選抜,若武者王優勝戦,選抜Ａ,ＳＴ野郎選抜,是政選抜戦,坂上忍選考ＤＲ,予選進入固定,Ａ級選抜戦,シーボー特選,兄弟船ＤＲ戦,準優勝進出戦,特別選抜Ｂ,若葉ドリーム,戸田特賞,ナビゲータ選抜,一般特別Ｂ戦,ＷドリームＢ戦,団体・選抜Ｂ戦,ビナちゃん選抜,うずしお選抜戦,しじみ選抜,あさイチ,なでしこ選抜戦,招福選抜,レディスＤ,ラブがまＤＲ,ＳＰＡボレ女杯,地元記者選抜特,男子予選特賞,桐生特選,予選特選,準決勝,ツックン選抜戦,Ａランチ戦,Ｗターキー選抜,予選特別選抜,みくにあさトク,ＳＪドリーム戦,匠選抜戦"
			+ ",５ールドレース,つつみん選抜,くろまつＤ,広テレ選抜,ドリボートＤＲ,Ｗ準優前半戦,一般シーボー選,波乗り王決定戦,フェニックスＦ,若武者王特選,ＳＰマッチＴＲ,ニッカン選抜戦,桐生ドリーム戦,ＤＲリターン,たなみんＤ戦,新涼選抜戦,バディー選抜,一般まじん選抜,唐津選抜,快速王Ｄ,はまゆうＤ,オールフリー戦,クラウンＤＲ,なるちゃん特選,特選戦,一般,１ＳＴ記者選抜,ソイカラ特選,朝ガチ戦予選,男子優勝戦,やわたドリーム,Ｗ準優勝戦後半,岡山支部選抜,サッポロ特選,芦屋釜ドリーム,モーニング一般,準王将位戦,みくにあさズバ,１ｓｔステージ,名人特選,おもしろ選抜,マンスリー選抜,準優トライアル,一般特賞,男子予選,ラブがま特選,夕ドキ戦一般,バニビ選抜,復活戦,すなっち選抜,女子予選特選,フェアリー戦,新鋭特選,マクール特選,予選日刊選抜,うな二朗戦,伊勢新聞社選抜,すなっち特選,ゼットン選抜戦,特別選抜Ｂ戦,夜の九スポ選抜,クラリス選抜戦,うずひめ特選,正月選抜,地区対抗戦一般,永島ドリーム,レディスＤＲ,大スポ選抜,Ｂボーイ選抜,ビーナスＤＲ,準優勝,選抜Ａ戦,リーグ覇者戦,一般戦進入固定,お盆特選,ウインビー選抜,達人選抜,かっぱくん特別,ＪＮＢ選抜,ＹＥＧ選抜戦,ニッカンＤＲ,うずしお特選,ｅ‐女選抜戦,サンスポ選抜戦,へビー級ＤＲ,西スポ選抜戦,地区王優勝戦,東京ベイ選抜戦,まつえ選抜,デイリー選抜戦,みくにあさイチ,ガァ～コ選抜戦,プリンセスＤＲ,予選特賞,イヌナキンＤＲ,スカパー！ＤＲ,女子ドリーム戦,テレボート特選,どすこいタイム,ツッキーレース,昼得クラリス戦,東洋観光特選,ヤクルトＤＲ,本命波乱戦特選,特賞戦,小平市選抜戦,ニッカン特選,一般特別Ａ戦,住信ＳＢＩ選抜,女子優勝戦,ペア対抗ＤＲ,四市組合選抜戦,おはよう戦Ｒ,決勝,クイーン特選,女子予選特賞,ＪＬＣドリーム,メガトン特選,ベイドリーム,やまだ屋特選,モンタドリーム,ＢＴＳ名張特賞,蛭子選抜戦,ランチタイム,広テレ特選,ルーキー選抜,びなん選抜,新鋭ドリーム,王将特選,★サッポロＤＲ,ペア対抗予選,フマキラー特選,一撃レース,報知ドリーム,モルツ特選,メモリアル特選,フェニックスＢ,シーモ戦,モンタ特選,シニアヤング戦,テレッペ選抜,ＷドリームＡ戦,若武者王選抜戦,予選特別Ｂ戦,スポニチ特選,ナイター王特選,まるがめ特選,モーニング特賞,静波まつり優勝,男１対女５特賞,名匠ドリーム,ファン選抜,たなみんＤＲ戦,おはよう戦Ｚ,選抜戦,デイリー特選,夜ガチ戦予選,対抗予選特選,ツッピー５ＬＤ,選抜Ｂ,昇龍ドリーム,一般お盆特選,ツッキー選抜戦,たなみんＤＲ,あしや選抜,ＢＢ選抜,男子ドリーム戦,東京ベイ選抜,アシ夢選抜,龍翔選抜,モンタ選抜,レナリサ選抜戦,家康ドリーム戦,もみじ特選,ファイナル選抜,ベイドリーム戦,予選特別Ａ戦,桐生選抜,準々王将位戦,王将ドリーム,一般特選,メモリアル選抜,東京ドリーム,トライアル,朝イチ特賞,マリンドリーム,サンセット特別,若松夜王選抜戦,西スポドリーム,超速ドリーム戦,ＭＢＰ名張特賞,モンタＤＲ,江戸川選抜,地区王特選,一般報知選抜,うずうず特選,まるがめ選抜戦,地区対抗戦特選,女子特賞,こひめ特別,午後いちボート,おはよう選抜戦,おはよう準優勝,女・選抜戦,ニャンまげＤＲ,女子準優勝戦,アフター特選,ビューティ選抜,Ａ級予選特賞,三国選抜"
			;
	}
	
	public String getMM() {
		return "01,02,03,04,05,06,07,08,09,10,11,12";
	}
	
	public String getRaceTypeSimple() {
		//return "選抜,特,準優勝戦,優勝戦,予選特,予選,一般,else";
		return "1,2,3,4,5,6,7,else";
	}
	
	public String getAlcount() {
		return "0,1,2,3,4,5,6";
	}
	
	public String getWakuLevel12() {
		return "A1-A1,A1-A2,A1-B1,A1-B2,A2-A1,A2-A2,A2-B1,A2-B2,B1-A1,B1-A2,B1-B1,B1-B2,B2-A1,B2-A2,B2-B1,B2-B2";
	}

	public String getWakuLevel123() {
		return "A1-A1-A1,A1-A1-A2,A1-A1-B1,A1-A1-B2,A1-A2-A1,A1-A2-A2,A1-A2-B1,A1-A2-B2,A1-B1-A1,A1-B1-A2,A1-B1-B1,A1-B1-B2,A1-B2-A1,A1-B2-A2,A1-B2-B1,A1-B2-B2"
		 + ",A2-A1-A1,A2-A1-A2,A2-A1-B1,A2-A1-B2,A2-A2-A1,A2-A2-A2,A2-A2-B1,A2-A2-B2,A2-B1-A1,A2-B1-A2,A2-B1-B1,A2-B1-B2,A2-B2-A1,A2-B2-A2,A2-B2-B1,A2-B2-B2"
		 + ",B1-A1-A1,B1-A1-A2,B1-A1-B1,B1-A1-B2,B1-A2-A1,B1-A2-A2,B1-A2-B1,B1-A2-B2,B1-B1-A1,B1-B1-A2,B1-B1-B1,B1-B1-B2,B1-B2-A1,B1-B2-A2,B1-B2-B1,B1-B2-B2"
		 + ",B2-A1-A1,B2-A1-A2,B2-A1-B1,B2-A1-B2,B2-A2-A1,B2-A2-A2,B2-A2-B1,B2-A2-B2,B2-B1-A1,B2-B1-A2,B2-B1-B1,B2-B1-B2,B2-B2-A1,B2-B2-A2,B2-B2-B1,B2-B2-B2";
	}
	
	public String getKumiban201() {
		return "12,13,14,21,31,41";
	}
	
	public String getBettype() {
		return "1T,2T,3T,2F,3F";
	}
	
	public String getBork() {
		return "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20";
	}
	
	public String getRank() {
		int[] arr = {1,2,3,4,5,6};
    	StringBuilder sb = new StringBuilder();
    	doPermutation(arr, 0, sb);
    	String result = sb.toString();
		return result.substring(0,result.length()-1);
	}
	
	public String getRange(int start, int end, int interval) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i <= end; i+=interval ) {
			sb.append(String.valueOf(i));
			sb.append(",");
		}
		sb.append("else");
		
		return sb.toString();
	}
	
	private void doPermutation(int[] arr, int startIdx, StringBuilder sb){
        int length = arr.length;
        if(startIdx == length-1){
            for(int n: arr) {
            	sb.append(n);
            	//System.out.print(n + "");
            }
            sb.append(",");
            //System.out.println();
            return;
        }

        for(int i=startIdx; i<length; i++){
            swap(arr, startIdx, i);
            doPermutation(arr, startIdx+1, sb);
            swap(arr, startIdx, i);
        }
    }

    private void swap(int[] arr, int n1, int n2){
        int temp = arr[n1];
        arr[n1] = arr[n2];
        arr[n2] = temp;
    }
	
    public static void main(String[] args) {
    	int[] arr = {1,2,3,4,5,6};
    	NominalManager m = new NominalManager();
    	StringBuilder sb = new StringBuilder();
    	m.doPermutation(arr, 0, sb);
    	System.out.println(sb.toString());
	}
}

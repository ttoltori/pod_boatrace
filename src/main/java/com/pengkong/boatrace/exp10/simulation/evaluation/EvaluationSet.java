package com.pengkong.boatrace.exp10.simulation.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.pengkong.boatrace.exp10.property.MLPropertyUtil;

/**
 * bettype, kumiban単位のEvaluation集合クラス.
 * @author ttolt
 *
 */
public class EvaluationSet {
	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();
	
	List<Evaluation> listEval = new ArrayList<>();

	public EvaluationSet() {
	}

	public EvaluationSet(List<Evaluation> listEval, String grade) {
		super();
		this.listEval = new ArrayList<>();
		for (Evaluation eval : listEval) {
			// 対象gradeでない場合スキップ
			if (!eval.get("grades").contains(grade)) {
				continue;
			}
			this.listEval.add(eval);
		}
	}

	public EvaluationSet(List<Evaluation> listEval) {
		super();
		this.listEval = listEval;
	}

	public void addAll(Collection<Evaluation> collection) {
		listEval.addAll(collection);
	}
	
	/** 保持しているEvaluationの中で重複しないモデルNOのリストをソートして取得する。
	 * @param field 比較対象のfield名
	 */
	public List<String> getUniqueList(String field) {
		SortedSet<String> set = new TreeSet<>();
		for (Evaluation ev : listEval) {
			String selected = ev.get(field);
			if (selected != null && !set.contains(selected)) {
				set.add(selected);
			}
		}
		return new ArrayList<>(set);
	}

	/** 保持しているEvaluationの中で重複しないモデルNOのリストをソートして取得する。
	 * @param selectorField 比較対象のfield名
	 */
	public List<String> getUniqueList(String...fields) {
		SortedSet<String> set = new TreeSet<>();
		for (Evaluation ev : listEval) {
			StringBuilder sb = new StringBuilder();
			for (String field : fields) {
				sb.append(ev.get(field));
				sb.append("_");
			}

			String selected = sb.substring(0, sb.length() - 1);
			if (selected != null && !set.contains(selected)) {
				set.add(selected);
			}
		}
		return new ArrayList<>(set);
	}

	/**
	 * 指定field名の取得値がもっとも高いEvaluationを取得する。
	 * @param selectorField 比較対象のfield名
	 * @return データ未存在の場合はNULL
	 */
	public Evaluation selectMostHigh(String selectorField) {
		if (listEval.size() <= 0) {
			return null;
		}
		listEval.sort(new EvaluationComparator(selectorField));

		return listEval.get(0);
	}

	public List<Evaluation> sortDescending(String selectorField) {
		if (listEval.size() <= 0) {
			return null;
		}

		listEval.sort(new EvaluationComparator(selectorField));
		
		return listEval;
	}
	
	/** EvaluationのComparatorクラス。*/
	private class EvaluationComparator implements Comparator<Evaluation> {
		private String fieldName;
		/**
		 * constructor. fieldNameで取得される値はdoubleへ変換可能な値であること。
		 * @param fieldName
		 */
		public EvaluationComparator(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public int compare(Evaluation o1, Evaluation o2) {
			// sort descending !!!
			try {
				return Double.compare(Double.valueOf(o2.get(fieldName)), Double.valueOf(o1.get(fieldName)));
			} catch (Exception e) {
				return 0;
			}
		}
	}
}

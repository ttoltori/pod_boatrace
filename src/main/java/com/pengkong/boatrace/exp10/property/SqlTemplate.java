package com.pengkong.boatrace.exp10.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pengkong.boatrace.common.enums.Delimeter;
import com.pengkong.common.FileUtil;
import com.pengkong.common.StringUtil;

/**
 * ファイルからsqlを提供する.
 * 一回取得したsqlはmapにキャッシュする。
 * 
 * properties: dir_sql, files_sql
 * 
 * @author ttolt
 *
 */
public class SqlTemplate {
	Logger logger = LoggerFactory.getLogger(SqlTemplate.class);

	/** 実験プロパティ */
	MLPropertyUtil prop = MLPropertyUtil.getInstance();

	/** key=arff_sql_rank  value=sql文 */ 
	HashMap<String, String> mapSql = new HashMap<>();
	
	private static class InstanceHolder {
		private static final SqlTemplate INSTANCE = new SqlTemplate();
	}
	
	public static SqlTemplate getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public SqlTemplate() {
	}

	public String get(String sqlId) throws Exception {
		if (mapSql.containsKey(sqlId)) {
			return mapSql.get(sqlId);
		}
		
		return findSql(sqlId);
	}
	
	/**
	 * プロパティから取得したファイル一覧を順次探してsql文を返却する
	 * @param sqlId sql文のid
	 * @return sql文
	 * @throws Exception
	 */
	private String findSql(String sqlId) throws Exception {
		String dirSql = prop.getString("dir_sql");
		String[] files = prop.getString("files_sql").split(Delimeter.COMMA.getValue());
		
		StringBuilder sb = new StringBuilder();
		// file loop
		for (String file : files) {
			List<String> linesOfFile = FileUtil.readFileByLineArr(dirSql + file, "UTF-8", true, "--");
			// lines of file content loop
			List<String> listLine = getListSqlLines(linesOfFile, sqlId);
			
			if (listLine.size() == 0) // next file
				continue;
			
			// replace partial sql
			for (String line : listLine) {
				if (line.contains("{{")) {
					String partialSqlId = StringUtil.getStringBetween(line, "{{", "}}");
					line = replacePartilaSql(linesOfFile, line, partialSqlId);
				}
				
				sb.append(line); 
				sb.append("\n");
			}
			break;
		}
		
		String sql = sb.toString();
		if (sql.length() > 0) {
			mapSql.put(sqlId, sql);
		}
		
		return sql;
	}
	
	/**
	 * sql文の部分SQLを指定部分SQLを置換して返却する
	 * @param linesOfFile 全体sql文
	 * @param line sql文の１行
	 * @param partialSqlId 部分SQLのID
	 * 
	 * @return 部分SQL置換したSQL文
	 */
	private String replacePartilaSql(List<String> linesOfFile, String line, String partialSqlId) {
		String partialSql = getSqlLine(linesOfFile, partialSqlId); 
		
		return line.replace("{{" + partialSqlId + "}}", partialSql);
	}
	
	/**
     * BEGIN～ENDの間のSQL文を返却する 例）BEGIN arff_nopattern SQL文 END
	 * @param linesOfFile 全体SQL文
	 * @param sqlId SQL文のID
	 * @return sql文
	 */
	private String getSqlLine(List<String> linesOfFile, String sqlId) {
		StringBuilder sb = new StringBuilder();
		boolean isSqlFound = false;
		for (String line : linesOfFile) {
			if (line.startsWith("BEGIN")) {
				String[] token = line.split(Delimeter.SPACE.getValue());
				if (sqlId.equals(token[token.length-1].trim())) {
					isSqlFound = true;
					continue;
				}
			}
			
			if (isSqlFound) {
				if (!line.startsWith("END")) {
					sb.append(line); sb.append(" ");
					continue;
				} else {
					break;
				}
			}
		}
		
		return sb.toString();
	}
	
	/**
     * BEGIN～ENDの間のSQL文を返却する 例）BEGIN arff_nopattern SQL文 END
	 * @param linesOfFile 全体SQL文
	 * @param sqlId SQL文のID
	 * @return sql文
	 */
	private List<String> getListSqlLines(List<String> linesOfFile, String sqlId) {
		List<String> listLine = new ArrayList<>();
		boolean isSqlFound = false;
		for (String line : linesOfFile) {
			if (line.startsWith("BEGIN")) {
				String[] token = line.split(Delimeter.SPACE.getValue());
				if (sqlId.equals(token[token.length-1].trim())) {
					isSqlFound = true;
					continue;
				}
			}

			if (isSqlFound) {
				if (!line.startsWith("END")) {
					listLine.add(line);
					continue;
				} else {
					break;
				}
			}
		}

		return listLine;
	}
	
	public static void main(String[] args) {
		try {
			//MLPropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/expr10.properties");
			MLPropertyUtil.getInstance().addFile("C:/Dev/workspace/Oxygen/pod_boatrace_online/properties/online.properties");
			System.out.println(SqlTemplate.getInstance().get("s-online") );
			//System.out.println(SqlTemplate.getInstance().get("result_1") );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

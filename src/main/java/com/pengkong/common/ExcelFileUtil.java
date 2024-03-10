package com.pengkong.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pengkong.boatrace.common.enums.Delimeter;

/**
 * Excelファイルアクセスクラス.
 * 
 * @author ttolt
 *
 */
public class ExcelFileUtil {
	public static List<String> read(String filepath, String sheetName, String delimeter)
			throws FileNotFoundException, IOException {
		return read(filepath, sheetName, delimeter, true, "#");

	}

	/**
	 * excelファイルを読み込む。
	 * 
	 * @param filepath                         ファイルパス
	 * @param sheetName                        シート名
	 * @param delimeter                        cell間の区分文字
	 * @param useAsTsv tsvファイルと同等扱いする。(true=最初cellがblankの時読み込みを中止する、全て文字列として読み込む)
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static List<String> read(String filepath, String sheetName, String delimeter,
			boolean useAsTsv, String comment) throws FileNotFoundException, IOException {
		List<String> result = new ArrayList<>();

		File inputFile = new File(filepath);
		FileInputStream fis = new FileInputStream(inputFile);
		Workbook workbook = null;

		String ext = FilenameUtils.getExtension(inputFile.toString());

		if (ext.equalsIgnoreCase("xlsx")) {
			workbook = new XSSFWorkbook(fis);
		} else if (ext.equalsIgnoreCase("xls")) {
			workbook = new HSSFWorkbook(fis);
		}

		// Get first sheet from the workbook

		// int numberOfSheets = workbook.getNumberOfSheets(); //全sheetループはしない
		Row row;
		Cell cell;
		// Iterate through each rows from first sheet

		Sheet sheet = workbook.getSheet(sheetName);
		Iterator<Row> rowIterator = sheet.iterator();

		boolean isEndOfData = false;
		while (rowIterator.hasNext()) {
			StringBuilder sb = new StringBuilder();

			row = rowIterator.next();
			// For each row, iterate through each columns
			Iterator<Cell> cellIterator = row.cellIterator();
			int colCount = 0;
			while (cellIterator.hasNext()) {
				cell = cellIterator.next();
				// tsv扱いの場合
				if (useAsTsv) {
					// 最初カラムがblankの時、読み込みを中止する
					if (colCount == 0 && cell.getCellType().equals(CellType.BLANK)) {
						isEndOfData = true;
						break;
					}
					
					CellType cellType = cell.getCellType();
					
					if (cellType.equals(CellType.NUMERIC) || cellType.equals(CellType.FORMULA)) {
						double value = cell.getNumericCellValue();
						// 小数点以下が0なら捨てる
						if (value == (int)value) {
							sb.append((int)value + delimeter);
						} else {
							sb.append(String.valueOf(value) + delimeter);
						}
					} else {
						String value = cell.getStringCellValue();
						// コメント行はskip
						if (colCount == 0 && value.startsWith(comment)) {
							break;
						}
						
						sb.append(value + delimeter);
					}
				} else {
					switch (cell.getCellType()) {
					case BOOLEAN:
						sb.append(cell.getBooleanCellValue() + delimeter);

						break;
					case NUMERIC:
					case FORMULA:	
						sb.append(cell.getNumericCellValue() + delimeter);

						break;
					case STRING:
						sb.append(cell.getStringCellValue() + delimeter);
						break;

					case BLANK:
						sb.append("" + delimeter);
						break;
					default:
						sb.append(cell + delimeter);

					}
				}
				colCount++;
			}
			if (isEndOfData) {
				break;
			}

			// comment行で読み飛ばした場合
			if (sb.length() <= 0) {
				continue;
			}

			// 行末のdelimeterは削除する
			sb.deleteCharAt(sb.length()-1);
			
			result.add(sb.toString());
		}

		return result;
	}

	public static void main(String[] args) {
		try {
			List<String> lines = ExcelFileUtil.read(
					//"C:/Dev/workspace/Oxygen/pod_boatrace/document/expr10/result_config.xlsx", "simul",
					//"C:/Dev/workspace/Oxygen/pod_boatrace/document/expr10/실험10.xlsx", "実験",
					"C:/Dev/workspace/Oxygen/pod_boatrace/document/expr10/result_config.xlsx", "selected",
					Delimeter.TAB.getValue());
			System.out.println(lines.size());

			List<String> lines2 = FileUtil
					.readFileByLineArr("C:/Dev/workspace/Oxygen/pod_boatrace/properties/expr10/result_config.tsv");
			System.out.println(lines2.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

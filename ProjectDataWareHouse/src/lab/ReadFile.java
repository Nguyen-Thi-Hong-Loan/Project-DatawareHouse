package lab;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ReadFile {
	List<List<Object>> informations;
	String dir;

	public List<List<Object>> readData(String dir) {
		informations = new ArrayList<>();
		try {

			List<Object> details;
			InputStream input = new FileInputStream(new File(dir));

			Workbook wr = getWorkbook(input, dir);
			Sheet sheet = wr.getSheetAt(0);

			Iterator<Row> rowIt = sheet.iterator();

			while (rowIt.hasNext()) {
				Row row = rowIt.next();
				Iterator<Cell> cellIt = row.iterator();
				details = new ArrayList<>();
				while (cellIt.hasNext()) {
					Cell cell = cellIt.next();

					switch (cell.getCellType()) {
					case STRING:
						details.add(cell.getStringCellValue());
						break;
					case BOOLEAN:
						details.add(cell.getBooleanCellValue());
						break;
					case NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							details.add(
									cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
						} else

							details.add((int) Math.round(cell.getNumericCellValue()));
						break;

					case FORMULA:
						Workbook workbook = cell.getSheet().getWorkbook();
						FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
						details.add(evaluator.evaluate(cell).getNumberValue());
						break;
					case _NONE:
						details.add("noneeeeeeeee ");
					case BLANK:
						details.add("blankkkkkk");
					case ERROR:
						details.add(cell.getErrorCellValue());
					default:

					}
				}
				informations.add(details);
//				for (Object st : details) {
//					System.out.print(st + "\t");
//					System.out.println();
//				}

			}
			wr.close();
			input.close();

		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return informations;
	}

	private static Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
		Workbook workbook = null;
		if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}

		return workbook;
	}

	public static void main(String[] args) {
		new ReadFile().readData("text\\DataFeedSpecification_17130110.xlsx");

	}
}

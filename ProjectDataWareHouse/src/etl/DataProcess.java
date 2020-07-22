package etl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import connection.DBConnection;
import dao.ControlDB;

public class DataProcess {
	static final String NUMBER_REGEX = "^[0-9]+$";
	static final String DATE_FORMAT = "yyyy-MM-dd";
	static final String ACTIVE_DATE = "31-12-2013";
	private ControlDB cdb;
	private String config_db_name;
	private String target_db_name;
	private String table_name;

	public DataProcess() {
		cdb = new ControlDB(this.config_db_name, this.table_name, this.target_db_name);
	}

	// Phương thức đọc những giá trị có trong file (value), cách nhau bởi dấu
	// phân cách (delim).
	private String readLines(String value, String delim) {
		String values = "";
		StringTokenizer stoken = new StringTokenizer(value, delim);
		// if (stoken.countTokens() > 0) {
		// stoken.nextToken();
		// }
		int countToken = stoken.countTokens();
		String lines = "(";
		for (int j = 0; j < countToken; j++) {
			String token = stoken.nextToken();
			if (Pattern.matches(NUMBER_REGEX, token)) {
				lines += (j == countToken - 1) ? token.trim() + ")," : token.trim() + ",";
			} else {
				lines += (j == countToken - 1) ? "'" + token.trim() + "')," : "'" + token.trim() + "',";
			}
			values += lines;
			lines = "";
		}
		return values;
	}

	// Phương thức đọ dữ liệu trong file .txt:
	public String readValuesTXT(File s_file, int count_field) {
		if (!s_file.exists()) {
			return null;
		}
		String values = "";
		String delim = "|"; // hoặc \t
		try {
			// Đọc một dòng dữ liệu có trong file:
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(s_file), "utf8"));
			String line = bReader.readLine();
			if (line.indexOf("\t") != -1) {
				delim = "\t";
			}
			// Kiểm tra xem tổng số field trong file có đúng format hay không
			// (11 trường)
//			if (new StringTokenizer(line, delim).countTokens() != count_field) {
//				bReader.close();
//				return null;
//			}
			// STT|Mã sinh viên|Họ lót|Tên|...-> line.split(delim)[0]="STT"
			// không phải số nên là header -> bỏ qua line
			// Kiểm tra xem có phần header hay không
			if (Pattern.matches(NUMBER_REGEX, line.split(delim)[0])) {
				values += readLines(line + delim, delim);
			}
			while ((line = bReader.readLine()) != null) {
				// line = 1|17130005|Đào Thị Kim|Anh|15-08-1999|DH17DTB|Công
				// nghệ thông tin
				// b|0123456789|17130005st@hcmuaf.edu.vn|Bến Tre|abc
				// line + " " + delim = 1|17130005|Đào Thị
				// Kim|Anh|15-08-1999|DH17DTB|Công nghệ
				// thông tin b|0123456789|17130005st@hcmuaf.edu.vn|Bến Tre|abc |
				// Nếu có field 11 thì dư khoảng trắng lên readLines() có
				// trim(), còn 10 field
				// thì fix lỗi out index
				values += readLines(line + " " + delim, delim);
			}
			bReader.close();
			return values.substring(0, values.length() - 1);

		} catch (NoSuchElementException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Ghi dữ liệu từ datastaging và datawarehouse:
	public void writeDataToWareHouse(ResultSet rs) {
		try {
			while (rs.next()) {
				String mssv = rs.getString("mssv");
				String ho = rs.getString("firstname");
				String ten = rs.getString("lastname");
				String ngay_sinh = rs.getString("dob");
				String ma_lop = rs.getString("classid");
				String ten_lop = rs.getString("classname");
				String sdt = rs.getString("sdt");
				String email = rs.getString("email");
				String que_quan = rs.getString("address");
				String note = rs.getString("note");
				String value = "('" + mssv + "','" + ho + "','" + ten + "','" + ngay_sinh + "','" + ma_lop + "','"
						+ ten_lop + "','" + sdt + "','" + email + "','" + que_quan + "','" + note + "')";
				// insert into student values('18130002','Lưu
				// Văn','An','2020-01-01','DH18DTC','CÔNG NGHỆ THÔNG
				// TIN','0879234555','18130002@st.hcmuaf.edu.vn','Vũng
				// Tàu','N/A')
				// ('18130002','Lưu Văn','An','2020-01-01','DH18DTC','CÔNG NGHỆ
				// THÔNG TIN','0879234555','18130002@st.hcmuaf.edu.vn','Vũng
				// Tàu','N/A)
				cdb.insertValuesToWareHouse(value);

			}
			cdb.truncateTable("database_staging", "student");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// try {
			// rs.close();
			// } catch (SQLException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

	// Hàm main này để test xem các phương thức ở lớp này có đúng không:
	public static void main(String[] args) {
		ControlDB dp = new ControlDB();
		ResultSet allRecored = dp.selectAllField("database_staging", "student");
		DataProcess dppp = new DataProcess();
		dppp.writeDataToWareHouse(allRecored);
	}

	// Phương thức đọc dữ liệu trong file .xlsx:
	public String readValuesXLSX(File s_file, int countField) {
		String values = "";
		String value = "";
		String delim = "|";
		try {
			FileInputStream fileIn = new FileInputStream(s_file);
			XSSFWorkbook workBook = new XSSFWorkbook(fileIn);
			XSSFSheet sheet = workBook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();
			// Kiểm tra xem có phần header hay không, nếu không có phần header
			// Gọi rows.next, nếu có header thì vị trí dòng dữ liệu là 1.
			// Nếu kiểm tra mà không có header thì phải set lại cái row bắt đầu
			// ở vị trí 0, hổng ấy là bị sót dữ liệu dòng 1 nha.
			if (rows.next().cellIterator().next().getCellType().equals(CellType.NUMERIC)) {
				rows = sheet.iterator();
			}
			while (rows.hasNext()) {
				Row row = rows.next();
				// Kiểm tra coi cái số trường ở trong file excel có đúng với
				// số trường có trong cái bảng mình tạo sẵn ở trong table
				// staging không
//				if (row.getLastCellNum() < countField - 1 || row.getLastCellNum() > countField) {
//					workBook.close();
//					return null;
//				}
				// Bắt đầu lấy giá trị trong các ô ra:
				// Iterator<Cell> cells = row.cellIterator();
				for (int i = 0; i < countField; i++) {
					// Cell cell = cells.next();
					if (i == countField - 1) {
						value += DataProcess.ACTIVE_DATE;
					}
					Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					CellType cellType = cell.getCellType();
					switch (cellType) {
					case NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
							value += dateFormat.format(cell.getDateCellValue()) + delim;
						} else {
							value += (long) cell.getNumericCellValue() + delim;
						}
						break;
					case STRING:
						value += cell.getStringCellValue() + delim;
						break;
					case FORMULA:
						switch (cell.getCachedFormulaResultType()) {
						case NUMERIC:
							value += (long) cell.getNumericCellValue() + delim;
							break;
						case STRING:
							value += cell.getStringCellValue() + delim;
							break;
						default:
							value += " " + delim;
							break;
						}
						break;
					case BLANK:
					default:
						value += " " + delim;
						break;
					}
				}
				if (row.getLastCellNum() == countField) {
					value += "|";
				}
				values += readLines(value, delim);
				value = "";
			}
			workBook.close();
			fileIn.close();
			return values.substring(0, values.length() - 1);
		} catch (Exception e) {
			return null;
		}
	}

	// Ghi dữ liệu vô table ở trong database staging
	public boolean writeDataToBD(String column_list, String target_table, String values) throws ClassNotFoundException {
		if (cdb.insertValues(column_list, values, target_table))
			return true;
		return false;
	}

	public void setConfig_db_name(String config_db_name) {
		this.config_db_name = config_db_name;
	}

	public void setTarget_db_name(String target_db_name) {
		this.target_db_name = target_db_name;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public ControlDB getCdb() {
		return cdb;
	}

	public void setCdb(ControlDB cdb) {
		this.cdb = cdb;
	}

}

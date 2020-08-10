package etl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
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
		// Cắt giá trị dựa trên delim
		StringTokenizer stoken = new StringTokenizer(value, delim);
		// Đếm số token cắt được
		int countToken = stoken.countTokens();
		// Cho các giá trị mình đọc được khi đọc file thành về cùng một dạng
		// vd: (1,"17130172","Do Kim Phuong")
		String lines = "(";
		for (int j = 0; j < countToken; j++) {
			String token = stoken.nextToken();
			// Nếu là dữ liệu số thì không bỏ vào ngoặc kép
			if (Pattern.matches(NUMBER_REGEX, token)) {
				lines += (j == countToken - 1) ? token.trim() + ")," : token.trim() + ",";
			} else {
				// Dữ liệu chuỗi thì bỏ vào ngoặc kép
				lines += (j == countToken - 1) ? "'" + token.trim() + "')," : "'" + token.trim() + "',";
			}
			values += lines;
			lines = "";
		}
		return values;
	}

	// Phương thức đọc dữ liệu trong file .txt:
	public String readValuesTXT(File s_file, int count_field) {
		// Kiểm tra xem file có tồn tại hay không
		if (!s_file.exists()) {
			return null;
		}
		String values = "";
		String delim = "|"; // hoặc \t
		try {
			// Đọc một dòng dữ liệu có trong file:
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(s_file), "utf8"));
			String line = bReader.readLine();
			// Nếu delim là dấu tab thì set lại dấu | thành dấu tab
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
			// Lấy được giá trị với định dạng (...),(...)..., chỗ value.length-1 là bỏ dấu ,
			// bị dư ở cuối
			return values.substring(0, values.length() - 1);

		} catch (NoSuchElementException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Phương thức đọc dữ liệu trong file .xlsx:
	public String readValuesXLSX(File s_file, int countField) {
		String values = "";
		String value = "";
		String delim = "|";
		try {
			// File excel của mình
			FileInputStream fileIn = new FileInputStream(s_file);
			// Lấy file excel
			XSSFWorkbook workBook = new XSSFWorkbook(fileIn);
			// Lấy trang tính trong file excel
			XSSFSheet sheet = workBook.getSheetAt(0);
			// Lấy ra các dòng
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
					// Lấy các ô, lấy kiểu này để fix trường hợp ô trống
					Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					// Kiểu của ô
					CellType cellType = cell.getCellType();
					switch (cellType) {
					// Ô kiểu số
					case NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
							value += dateFormat.format(cell.getDateCellValue()) + delim;
						} else {
							value += (long) cell.getNumericCellValue() + delim;
						}
						break;
					// Ô kiểu chuỗi
					case STRING:
						value += cell.getStringCellValue() + delim;
						break;
					// Ô kiểu công thức
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
				// Đưa giá trị vào định dạng readlines
				values += readLines(value, delim);
				value = "";
			}
			workBook.close();
			fileIn.close();
			// Lấy được giá trị với định dạng (...),(...)..., chỗ value.length-1 là bỏ dấu ,
			// bị dư ở cuối
			return values.substring(0, values.length() - 1);
		} catch (Exception e) {
			return null;
		}
	}

	// Ghi dữ liệu vô table ở trong database staging
	public boolean writeDataToBD(String column_list, String target_table, String values) throws SQLException {
		if (cdb.insertValues(column_list, values, target_table))
			return true;
		return false;
	}

	// Ghi dữ liệu từ datastaging và datawarehouse:
	public void writeDataToWareHouse(ResultSet rs, String target_table, int idlog) {
		try {
			if (rs == null)
				return;
			if (target_table.equals("sinhvien")) {
				while (rs.next()) {
					try {
						String sql = "{call INSERT_SINHVIEN (?,?,?,?,?,?,?,?,?,?,?)}";
						Connection con = DBConnection.getConnection("database_warehouse");
						CallableStatement cst = con.prepareCall(sql);
						cst.setString(1, rs.getString("mssv"));
						cst.setString(2, rs.getString("firstname"));
						cst.setString(3, rs.getString("lastname"));
						cst.setString(4, rs.getString("dob"));
						cst.setString(5, rs.getString("classid"));
						cst.setString(6, rs.getString("classname"));
						cst.setString(7, rs.getString("sdt"));
						cst.setString(8, rs.getString("email"));
						cst.setString(9, rs.getString("address"));
						cst.setString(10, rs.getString("note"));
						cst.setInt(11, idlog);
						cst.executeUpdate(); // insert,delete,update
					} catch (SQLException e) {
						continue;
					}
				}
			} else if (target_table.equals("monhoc")) {
				while (rs.next()) {
					try {
						// IN STT_IN INT, IN MAMH VARCHAR ( 255 ),IN TENMH
						// VARCHAR ( 255 ),IN TINCHI TINYINT,IN KHOAQL VARCHAR(255),IN KHOASD
						// VARCHAR(255), IN ID_LOG INT
						// chết, lộn @@, nãy Hồng truyền file name là đúng r á
						String sql = "{call INSERT_MONHOC (?,?,?,?,?,?,?)}";
						Connection con = DBConnection.getConnection("database_warehouse");
						CallableStatement cst = con.prepareCall(sql);
						cst.setInt(1, rs.getInt("stt"));
						cst.setString(2, rs.getString("mamh")); // MẤY TÊN CỌT NÀY BÀ PHẢI GỌI ĐÚNG NHƯ BÊN TRONG
																// DATABASE
						cst.setString(3, rs.getString("tenmh"));
						cst.setInt(4, rs.getInt("tinchi"));
						cst.setString(5, rs.getString("khoaquanly"));
						cst.setString(6, rs.getString("khoasudung"));
						cst.setInt(7, idlog);
						cst.executeUpdate(); // insert,delete,update
					} catch (SQLException e) {
						continue;
					}
				}
			} else if (target_table.equals("lophoc")) {
				while (rs.next()) {
					try {
						// IN LOPHOCID VARCHAR ( 255 ), IN MAMH_IN VARCHAR ( 255 ), IN
						// NAMHOC_IN INT, IN ID_LOG INT
						String sql = "{call INSERT_LOPHOC (?,?,?,?)}";
						Connection con = DBConnection.getConnection("database_warehouse");
						CallableStatement cst = con.prepareCall(sql);
						cst.setString(1, rs.getString("malophoc"));
						cst.setString(2, rs.getString("mamh"));
						cst.setString(3, rs.getString("namhoc"));
						cst.setInt(4, idlog);
						cst.executeUpdate(); // insert,delete,update
					} catch (SQLException e) {
						continue;
					}
				}

			} else if (target_table.equals("dangky")) {
				while (rs.next()) {
//					INSERT_DANGKY(IN MA_DANGKY VARCHAR ( 255 ),IN MA_SINHVIEN
//					VARCHAR ( 255 ),IN MA_LOPHOC VARCHAR(255),IN NGAYDK VARCHAR(255), IN ID_LOG INT)
					try {
						String sql = "{call INSERT_DANGKY (?,?,?,?,?)}";
						Connection con = DBConnection.getConnection("database_warehouse");
						CallableStatement cst = con.prepareCall(sql);
						cst.setString(1, rs.getString("madk"));
						cst.setString(2, rs.getString("masv"));
						cst.setString(3, rs.getString("malophoc"));
						cst.setString(4, rs.getString("thoigian"));
						cst.setInt(5, idlog);
						cst.executeUpdate(); // insert,delete,update
					} catch (SQLException e) {
						continue;
					}
				}

			}
			cdb.truncateTable("database_staging", target_table);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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

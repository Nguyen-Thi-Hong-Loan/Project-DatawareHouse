package etl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import control.Config;
import dao.ControlDB;
import log.Log;
import modal.Download;
import modal.SendMail;
import modal.WriteBug;

public class DataStaging {
	// static final String EXT_TEXT = ".txt";
	// static final String EXT_CSV = ".csv";
	// static final String EXT_EXCEL = ".xlsx";
	private int config_id;
	private String state;

	public int getConfig_id() {
		return config_id;
	}

	public void setConfig_id(int config_id) {
		this.config_id = config_id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

//Hàm main để chạy task schedule:
	public void mainStaging(int id_config)
			throws AddressException, MessagingException, ClassNotFoundException, SQLException {
		DataStaging dw = new DataStaging();
		dw.setConfig_id(id_config);
		dw.setState("ER");
		DataProcess dp = new DataProcess();
		ControlDB cdb = new ControlDB();
		cdb.setConfig_db_name("controldb");
		cdb.setTarget_db_name("database_staging");
		cdb.setTable_name("config");
		dp.setCdb(cdb);
		dw.ExtractToDB(dp);

	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		DataStaging ds = new DataStaging();
		Download dow = new Download();
		try {
			// 1 sinhvien 2: monhoc 4: lophoc 3: dangky
			dow.mainSCP(1);
			ds.mainStaging(1);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ExtractToDB(DataProcess dp) throws ClassNotFoundException, SQLException {
		Config configuration = dp.getCdb().loadAllConfs(this.config_id);
		// Lấy các trường trong một dòng config ra:
		// Lấy bảng trong database_staging, vd:student
		String target_table = configuration.getTargetTable();
		// Lấy ra thư lục chứa file
		String import_dir = configuration.getDirSou();
		// Lấy dấu phân cách
		String delim = configuration.getDelimeterSou();
		// Lấy ra danh sách các trường
		String column_list = configuration.getFieldName();
		System.out.println(target_table);
		System.out.println(import_dir);

		// Lấy các trường có trong dòng log đầu tiên có state=ER;
		Log log = dp.getCdb().getLogsWithStatus(this.state, this.config_id);
		int idlog = log.getIdLog();
		System.out.println(idlog);
		// Lấy file_name từ trong config ra
		String file_name = log.getFileName();
		// Ráp với importDir đề được cái đường dẫn tới file
		String sourceFile = import_dir + File.separator + file_name;
		// Đếm số trường trong filedName ở trong bảng config
		StringTokenizer str = new StringTokenizer(column_list, delim);
		System.out.println(sourceFile);
		File file = new File(sourceFile);
		if (file.exists()) {
			// Nếu log có resule là OK (thật ra cái này không cần cũng
			// được)
			if (log.getResult().equals("OK")) {
				String values = "";
				// Nếu file là .txt thì đọc file .txt
				if (file.getPath().endsWith(".txt")) {
					values = dp.readValuesTXT(file, str.countTokens());
					// Nếu file là .xlsx thì đọc file .xlsx
				} else if (file.getPath().endsWith(".xlsx")) {
					values = dp.readValuesXLSX(file, str.countTokens());
				} else {
					System.out.println("Tam thoi bo qua");

				}
				System.out.println(values);
				// Nếu đọc được giá trị rồi
				if (values != null) {
					String file_status;
					String result;
					// time
					String timestamp = getCurrentTime();
					// count line
					String stagin_load_count = "";
					try {
						stagin_load_count = countLines(file) + "";
					} catch (InvalidFormatException | org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
						e.printStackTrace();
					}

					String target_dir;
					// thì mình ghi dữ liệu vô bảng
					// nếu mình ghi được dữ liệu vô bảng
					try {
						if (dp.writeDataToBD(column_list, target_table, values)) {
							// Move to warehouse
							// Chon tat ca cac dong trong table target_table(db_staging) -> luu vao doi tuong ResultSet
							ResultSet allRecored = ControlDB.selectAllField("database_staging", target_table);
							// Load data tu staging sang warehouse dung` Procedure
							dp.writeDataToWareHouse(allRecored, target_table, idlog);

							file_status = "SU";
							result = "COMPLETE";
							// update cái log lại, chuyển file đã extract
							// xong
							// vào thư mục success
							dp.getCdb().updateLogAfterLoadToStaging(file_status, result, timestamp, file_name);
							target_dir = configuration.getSuccessDir();
							moveFile(target_dir, file);
							// ;

						} else {
//							 Nếu mà bị lỗi thì update log là state=Not TR
//							 và
//							 result=FAIL và ghi file vào thư mục error
							file_status = "Not TR";
							result = "FAIL";
							dp.getCdb().updateLogAfterLoadToStaging(file_status, result, timestamp, file_name);
							target_dir = configuration.getErrorDir();
							 if (moveFile(target_dir, file))
							 ;
//							WriteBug wb = new WriteBug();
//							wb.writeBug("Load file to staging not success!", 1);
//							new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE_BUG);
						}
					} catch (SQLException e) {
					}

				}
			}

		} else {
			System.out.println("Path not exists!!!");
			return;
		}

	}

	// Phương thức lấy ra thời gian hiện tạo để ghi vào log:
	public String getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	// Phương thức chuyển file vào các thư mục (success, error):
	private boolean moveFile(String target_dir, File file) {
		try {
			BufferedInputStream bReader = new BufferedInputStream(new FileInputStream(file));
			BufferedOutputStream bWriter = new BufferedOutputStream(
					new FileOutputStream(target_dir + File.separator + file.getName()));
			byte[] buff = new byte[1024 * 10];
			int data = 0;
			while ((data = bReader.read(buff)) != -1) {
				bWriter.write(buff, 0, data);
			}
			bReader.close();
			bWriter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			file.delete();
		}
	}

	// Đếm số dòng trong file excel:
	private int countLines(File file)
			throws InvalidFormatException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
		int result = 0;
		XSSFWorkbook workBooks = null;
		try {
			if (file.getPath().endsWith(".txt")) {
				BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line;
				while ((line = bReader.readLine()) != null) {
					if (!line.trim().isEmpty()) {
						result++;
					}
				}
				bReader.close();
			} else if (file.getPath().endsWith(".xlsx")) {
				workBooks = new XSSFWorkbook(file);
				XSSFSheet sheet = workBooks.getSheetAt(0);
				Iterator<Row> rows = sheet.iterator();
				rows.next();
				while (rows.hasNext()) {
					rows.next();
					result++;
				}
				return result;
			}

		} catch (IOException | org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
			e.printStackTrace();
		} finally {
			if (workBooks != null) {
				try {
					workBooks.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}

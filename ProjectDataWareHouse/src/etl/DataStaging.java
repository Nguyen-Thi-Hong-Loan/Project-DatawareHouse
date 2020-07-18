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

import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import control.Config;
import dao.ControlDB;
import log.Log;

public class DataStaging {
	static final String EXT_TEXT = ".txt";
	static final String EXT_CSV = ".csv";
	static final String EXT_EXCEL = ".xlsx";
	private String config_name;
	private String state;

	public String getConfig_name() {
		return config_name;
	}

	public void setConfig_name(String config_name) {
		this.config_name = config_name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		DataStaging dw = new DataStaging();
		dw.setConfig_name("f_sinhvien");
		dw.setState("ER");
		DataProcess dp = new DataProcess();
		ControlDB cdb = new ControlDB();
		cdb.setConfig_db_name("controldb");
		cdb.setTarget_db_name("database_staging");
		cdb.setTable_name("config");
		dp.setCdb(cdb);
		dw.ExtractToDB(dp);
	}

	public void ExtractToDB(DataProcess dp) throws ClassNotFoundException, SQLException {
		List<Config> lstConf = dp.getCdb().loadAllConfs(this.config_name);
		for (Config configuration : lstConf) {
			String extention = "";
			String target_table = configuration.getTargetTable();
			String import_dir = configuration.getImportDir();
			String delim = configuration.getDelimeterSou();
			String column_list = configuration.getFieldName();
			String variabless = configuration.getVariabless();
			System.out.println(target_table);
			System.out.println(import_dir);
			Log log = dp.getCdb().getLogsWithStatus(this.state);
			String file_name = log.getFileName();
			String sourceFile = import_dir + File.separator + file_name;
			StringTokenizer str = new StringTokenizer(column_list, delim);
			System.out.println(sourceFile);
			File file = new File(sourceFile);
			extention = file.getPath().endsWith(".xlsx") ? EXT_EXCEL
					: file.getPath().endsWith(".txt") ? EXT_TEXT : EXT_CSV;
			if (file.exists()) {
				if (log.getResult().equals("OK")) {
					String values = "";
					if (extention.equals(".txt")) {
						values = dp.readValuesTXT(file, str.countTokens());
						extention = ".txt";
					 } else if (extention.equals(".xlsx")) {
					 values = dp.readValuesXLSX(file,str.countTokens());
					 extention = ".xlsx";
					 }
					System.out.println(values);
					if (values != null) {
						String table = "log";
						String file_status;
						String result;
						int config_id = configuration.getIdConf();
						// time
						String timestamp = getCurrentTime();
						// count line
						String stagin_load_count = "";
						try {
							stagin_load_count = countLines(file, extention) + "";
						} catch (InvalidFormatException
								| org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
							e.printStackTrace();
						}
						//
						String target_dir;

						if (dp.writeDataToBD(column_list, target_table, values)) {
							//Move to warehouse
							//Chon tat ca cac dong trong table student(db_staging) -> luu vao doi tuong ResultSet
							ResultSet allRecored = ControlDB.selectAllField("database_staging", "student");
							dp.writeDataToWareHouse(allRecored);
							file_status = "TR";
							result = "OK";
							dp.getCdb().updateLogAfterLoadToStaging(file_status, result, timestamp, file_name);
							target_dir = configuration.getSuccessDir();
//							 if (moveFile(target_dir, file));

						} else {
							file_status = "Not TR";
							result = "FAIL";
							dp.getCdb().updateLogAfterLoadToStaging(file_status, result, timestamp, file_name);
							target_dir = configuration.getErrorDir();
//							if (moveFile(target_dir, file));
						}
					}
				}

			} else {
				System.out.println("Path not exists!!!");
				return;
			}

		}

	}

	// Lay thoi gian hien tai:
	public String getCurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	// Chuyen file da load thanh cong vao thu muc success:
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

	// Dem so dong cua file do:
	private int countLines(File file, String extention)
			throws InvalidFormatException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
		int result = 0;
		XSSFWorkbook workBooks = null;
		try {
			if (extention.indexOf(".txt") != -1) {
				BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line;
				while ((line = bReader.readLine()) != null) {
					if (!line.trim().isEmpty()) {
						result++;
					}
				}
				bReader.close();
			} else if (extention.indexOf(".xlsx") != -1) {
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

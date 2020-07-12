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

import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import control.Config;
import log.Log;
import dao.ControlDB;

public class DataStaging {
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
		dw.setConfig_name("f_txt");
		dw.setState("ER");
		;
		DataProcess dp = new DataProcess();
		ControlDB cdb = new ControlDB();
		cdb.setConfig_db_name("dbcontrol");
		cdb.setTarget_db_name("database_staging");
		cdb.setTable_name("config");
		dp.setCdb(cdb);
		dw.ExtractToDB(dp);
	}

	public void ExtractToDB(DataProcess dp) throws ClassNotFoundException, SQLException {
		List<Config> lstConf = dp.getCdb().loadAllConfs(this.config_name);
		for (Config configuration : lstConf) {
			String target_table = configuration.getTargetTable();
			String file_type = configuration.getFileType();
			String import_dir = configuration.getImportDir();
			String delim = configuration.getDelimeterSou();
			String column_list = configuration.getFieldName();
			String variabless = configuration.getVariabless();
			System.out.println(target_table);
			System.out.println(import_dir);
			if (!dp.getCdb().tableExist(target_table)) {
				System.out.println(variabless);
				dp.getCdb().createTable(target_table, variabless, column_list);
			}
			File imp_dir = new File(import_dir);
			Log log = dp.getCdb().getLogsWithStatus(this.state);
			String file_name = log.getFileName();
			String sourceFile = import_dir + File.separator + File.separator + file_name;
			System.out.println(sourceFile);
			File file = new File(sourceFile);
			if (file.exists()) {
				String extention = "";
			
				if (log.getResult().equals("OK")) {
					System.out.println(file.getName());
					if (file.getName().indexOf(file_type) != -1) {
						System.out.println(7);
						String values = "";
						if (file_type.equals(".txt")) {
							values = dp.readValuesTXT(file, delim);
							extention = ".txt";
						} else if (file_type.equals(".xlsx")) {
							values = dp.readValuesXLSX(file);
							extention = ".xlsx";
						}
						if (values != null) {
							String table = "log";
							String file_status;
							int config_id = configuration.getIdConf();
							// time
							DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
							LocalDateTime now = LocalDateTime.now();
							String timestamp = dtf.format(now);
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
								file_status = "SU";
								dp.getCdb().insertLog(table, file_status, config_id, timestamp, stagin_load_count,
										file_name);
								target_dir = configuration.getSuccessDir();
								if (moveFile(target_dir, file))
									;

							} else {
								file_status = "ERR";
								dp.getCdb().insertLog(table, file_status, config_id, timestamp, stagin_load_count,
										file_name);
								target_dir = configuration.getErrorDir();
								if (moveFile(target_dir, file))
									;
							}
						}
					}
				}

			} else {
				System.out.println("Path not exists!!!");
				return;
			}

		}

	}

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

package dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.DBConnection;
import control.Config;
import control.MyFile;
import log.Log;

public class ControlDB {
	private String config_db_name;
	private String target_db_name;
	private String table_name;
	private PreparedStatement pst = null;
	private ResultSet rs = null;
	private String sql;

	public ControlDB(String db_name, String table_name, String target_db_name) {
		this.config_db_name = db_name;
		this.table_name = table_name;
		this.target_db_name = target_db_name;
	}

	public ControlDB() {
	}

	// Sua:
	public ControlDB(String target_db_name) {
		this.target_db_name = target_db_name;
	}

	public String getConfig_db_name() {
		return config_db_name;
	}

	public void setConfig_db_name(String config_db_name) {
		this.config_db_name = config_db_name;
	}

	public String getTarget_db_name() {
		return target_db_name;
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

	// Phương thức lấy tất cả các thuộc tính có trong bảng config (lấy tất cả
	// các dòng config) lấy theo condition là configName (f_sinhvien hay
	// f_monhoc)
	public List<Config> loadAllConfs(String condition) throws SQLException {
		List<Config> listConfig = new ArrayList<Config>();
		Connection conn = DBConnection.getConnection("controldb");
		String selectConfig = "select * from config where configName=?";
		PreparedStatement ps = conn.prepareStatement(selectConfig);
		ps.setString(1, condition);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Config conf = new Config();
			conf.setIdConf(rs.getInt("idConfig"));
			conf.setConfigName(rs.getString("configName"));
			conf.setServerSou(rs.getString("serverSou"));
			conf.setPort(rs.getInt("port"));
			conf.setUserSou(rs.getString("userSou"));
			conf.setPassSou(rs.getString("passSou"));
			conf.setDirSou(rs.getString("directorySou"));
			conf.setFieldName(rs.getString("fieldName"));
			conf.setDelimeterSou(rs.getString("delimeterSou"));
			conf.setFormatSou(rs.getString("formatSou"));
			conf.setServerDes(rs.getString("serverDes"));
			conf.setDBNameDes(rs.getString("DBNameDes"));
			conf.setUseDes(rs.getString("userDes"));
			conf.setPassDes(rs.getString("passDes"));
			conf.setTargetTable(rs.getString("targetTable"));
			conf.setFileType(rs.getString("fileType"));
			conf.setImportDir(rs.getString("importDir"));
			conf.setSuccessDir(rs.getString("successDir"));
			conf.setErrorDir(rs.getString("errorDir"));
			conf.setVariabless(rs.getString("variabless"));
			listConfig.add(conf);
		}
		return listConfig;
	}

	// Phương thức lấy một dòng log đầu tiên trong table log có state = ER
	public Log getLogsWithStatus(String condition) throws SQLException {
		Log log = new Log();
		Connection conn = DBConnection.getConnection("controldb");
		String selectLog = "select * from log where state=?";
		PreparedStatement ps = conn.prepareStatement(selectLog);
		ps.setString(1, condition);
		ResultSet rs = ps.executeQuery();
		rs.last();
		if (rs.getRow() >= 1) {
			rs.first();
			log.setIdLog(rs.getInt("idlog"));
			log.setIdConfig(rs.getInt("idConfig"));
			log.setState(rs.getString("state"));
			log.setResult(rs.getString("result"));
			log.setNumColumn(rs.getInt("numColumn"));
			log.setFileName(rs.getString("fileName"));
		}
		return log;
	}

	// Phương thức chèn giá trị vào bảng có trong database staging, giá trị có
	// được từ quá trình đọc file (file .txt hoặc file .xlsx):
	public boolean insertValues(String fieldName, String values, String targetTable) throws ClassNotFoundException {
		sql = "INSERT INTO " + targetTable + "(" + fieldName + ") VALUES " + values;
		System.out.println(sql);
		try {
			pst = DBConnection.getConnection(this.target_db_name).prepareStatement(sql);
			pst.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (pst != null)
					pst.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	// Phương thức chèn dữ liệu vào log (tạm thời bước 2 không dùng đến phương
	// thức này):
	public boolean insertLog(String table, String file_status, int config_id, String timestamp,
			String stagin_load_count, String file_name) throws ClassNotFoundException {
		sql = "INSERT INTO " + table + "(dataFileName,idConfig,state,numColumn,dateUserInsert) value (?,?,?,?,?)";
		try {
			pst = DBConnection.getConnection(this.config_db_name).prepareStatement(sql);
			pst.setString(1, file_name);
			pst.setInt(2, config_id);
			pst.setString(3, file_status);
			pst.setInt(4, Integer.parseInt(stagin_load_count));
			pst.setString(5, timestamp);
			pst.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (pst != null)
					pst.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	// Phương thức update lại log sau khi đã extract file từ local lên
	// datastaging thành công, cập nhật lại state=TR, result=OK,
	// dateLoadToStaging=getCurrentTime();
	public boolean updateLogAfterLoadToStaging(String status, String result, String fileTimeStamp, String fileName) {
		Connection connection;
		String sql = "UPDATE log SET state=?, result=?, dateLoadToStaging=? WHERE fileName=?";
		try {
			connection = DBConnection.getConnection("controldb");
			PreparedStatement ps1 = connection.prepareStatement(sql);
			ps1.setString(1, status);
			ps1.setString(2, result);
			ps1.setString(3, fileTimeStamp);
			ps1.setString(4, fileName);
			ps1.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Phương thức loadInFile() load file vào trong table ở datastaging ( viết
	// ra chạy được vậy thôi chứ chưa có cơ hội dùng đến):
	public int loadInFile(String sourceFile, String targetTable, String delimeter) throws SQLException {
		sql = "LOAD DATA LOCAL INFILE '" + sourceFile + "' INTO TABLE " + targetTable + "\r\n"
				+ "FIELDS TERMINATED BY '" + delimeter + "' \r\n" + "ENCLOSED BY '\"' \r\n"
				+ "LINES TERMINATED BY '\r\n'";
		Connection conn = DBConnection.getConnection(this.target_db_name);
		PreparedStatement pst = conn.prepareStatement(sql);
		System.out.println("LOAD DATA LOCAL INFILE '" + sourceFile + "' INTO TABLE " + targetTable + "\r\n"
				+ "FIELDS TERMINATED BY '" + delimeter + "' \r\n" + "LINES TERMINATED BY '\\n'" + " IGNORE 1 ROWS");
		return pst.executeUpdate();

	}

	// Hàm main này để test các phương thức trên chạy ổn hay chưa:
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		ControlDB cb = new ControlDB("database_staging");
		Log log = cb.getLogsWithStatus("ER");
		System.out.println(log.toString());
	}

}

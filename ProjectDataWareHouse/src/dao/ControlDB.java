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

	// Phuong thuc lay cac thuoc tinh co trong bang config:
	public List<Config> loadAllConfs(String condition) throws SQLException {
		List<Config> listConfig = new ArrayList<Config>();
		Connection conn = DBConnection.getConnection("dbcontrol");
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

	// Phuong thuc lay log:
	public Log getLogsWithStatus(String condition) throws SQLException {
		// List<Log> listLog = new ArrayList<Log>();
		Log log = new Log();
		Connection conn = DBConnection.getConnection("dbcontrol");
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
			log.setNumColumn(rs.getInt("numColumn"));
			log.setFileName(rs.getString("fileName"));
			log.setDataFileName(rs.getString("dataFileName"));
			log.setResult(rs.getString("result"));
		}
		return log;
	}

	// Kiem tra bang co ton tai hay chua:
	public boolean tableExist(String table_name) throws ClassNotFoundException {
		try {
			DatabaseMetaData dbm = DBConnection.getConnection(this.target_db_name).getMetaData();
			ResultSet tables = dbm.getTables(null, null, table_name, null);
			try {
				if (tables.next()) {
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	// Chen du lieu vao bang tring database staging:
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

	// Chen du lieu vao log:
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

	public boolean updateLog(int configID, String fileName, String fileType, String status, String fileTimeStamp) {
		Connection connection;
		try {
			connection = DBConnection.getConnection("dbcontrol");
			PreparedStatement ps1 = connection.prepareStatement("UPDATE data_file SET active=0 WHERE file_name=?");
			ps1.setString(1, fileName);
			ps1.executeUpdate();
			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO data_file (config_id, file_name, file_type, status, file_timestamp, active) value (?,?,?,?,?,1)");
			ps.setInt(1, configID);
			ps.setString(2, fileName);
			ps.setString(3, fileName.substring(fileName.indexOf('.') + 1));
			ps.setString(4, status);
			ps.setString(5, fileTimeStamp);
			ps.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Tao bang:
	public boolean createTable(String table_name, String variables, String column_list) throws ClassNotFoundException {
		sql = "CREATE TABLE " + table_name + " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,";
		String[] vari = variables.split(",");
		String[] col = column_list.split(",");
		for (int i = 0; i < vari.length; i++) {
			sql += col[i] + " " + vari[i] + " NOT NULL,";
		}
		sql = sql.substring(0, sql.length() - 1) + ")";
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

	// Sua:
	// Phuong thuc loadInFile() load file vao trong table:
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

	// Sua:
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		ControlDB cb = new ControlDB("database_staging");
		Log log = cb.getLogsWithStatus("ER");
		System.out.println(log.toString());
	}

}

package control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import connection.DBConnection;

public class Config {
	// Các trường của Loan
	private int idConf;
	private String configName;// Trường này là tên của config ứng với kiểu file
								// vd:f_txt.
	private String serverSou;
	private int port;
	private String userSou;
	private String passSou;
	private String dirSou;
	private String fieldName;
	private String delimeterSou;
	private String formatSou;
	private String serverDes;
	private String DBNameDes;
	private String useDes;
	private String passDes;
	// Trường này trở đi là có trong config của Phượng
	private String targetTable;
	private String fileType;
	private String importDir;
	private String successDir;
	private String errorDir;
	private String variabless;

	public Config() {

	}

	public Config(String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM config WHERE configName=?";
		Connection conn;
		try {
			conn = DBConnection.getConnection("dbcontrol");
			pst = conn.prepareStatement(sql);
			pst.setString(1, condition);
			rs = pst.executeQuery();
			while (rs.next()) {
				idConf = rs.getInt("idConfig");
				this.configName = condition;
				serverSou = rs.getString("serverSou");
				port = rs.getInt("port");
				userSou = rs.getString("userSou");
				passSou = rs.getString("passSou");
				dirSou = rs.getString("directorySou");
				fieldName = rs.getString("fieldName");
				delimeterSou = rs.getString("delimeterSou");
				formatSou = rs.getString("formatSou");
				serverDes = rs.getString("serverDes");
				DBNameDes = rs.getString("DBNameDes");
				useDes = rs.getString("userDes");
				passDes = rs.getString("passDes");
				targetTable = rs.getString("targetTable");
				fileType = rs.getString("fileType");
				importDir = rs.getString("importDir");
				successDir = rs.getString("successDir");
				errorDir = rs.getString("errorDir");
				variabless = rs.getString("variabless");
			}

		} catch (Exception e) {
			e.printStackTrace();
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

	public int getIdConf() {
		return idConf;
	}

	public String getConfigName() {
		return configName;
	}

	public String getServerSou() {
		return serverSou;
	}

	public int getPort() {
		return port;
	}

	public String getUserSou() {
		return userSou;
	}

	public String getPassSou() {
		return passSou;
	}

	public String getDirSou() {
		return dirSou;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getDelimeterSou() {
		return delimeterSou;
	}

	public String getFormatSou() {
		return formatSou;
	}

	public String getServerDes() {
		return serverDes;
	}

	public String getDBNameDes() {
		return DBNameDes;
	}

	public String getUseDes() {
		return useDes;
	}

	public String getPassDes() {
		return passDes;
	}

	public String getTargetTable() {
		return targetTable;
	}

	public String getFileType() {
		return fileType;
	}

	public String getImportDir() {
		return importDir;
	}

	public String getSuccessDir() {
		return successDir;
	}

	public String getErrorDir() {
		return errorDir;
	}

	public String getVariabless() {
		return variabless;
	}

	public void setIdConf(int idConf) {
		this.idConf = idConf;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public void setServerSou(String serverSou) {
		this.serverSou = serverSou;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUserSou(String userSou) {
		this.userSou = userSou;
	}

	public void setPassSou(String passSou) {
		this.passSou = passSou;
	}

	public void setDirSou(String dirSou) {
		this.dirSou = dirSou;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setDelimeterSou(String delimeterSou) {
		this.delimeterSou = delimeterSou;
	}

	public void setFormatSou(String formatSou) {
		this.formatSou = formatSou;
	}

	public void setServerDes(String serverDes) {
		this.serverDes = serverDes;
	}

	public void setDBNameDes(String dBNameDes) {
		DBNameDes = dBNameDes;
	}

	public void setUseDes(String useDes) {
		this.useDes = useDes;
	}

	public void setPassDes(String passDes) {
		this.passDes = passDes;
	}

	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setImportDir(String importDir) {
		this.importDir = importDir;
	}

	public void setSuccessDir(String successDir) {
		this.successDir = successDir;
	}

	public void setErrorDir(String errorDir) {
		this.errorDir = errorDir;
	}

	public void setVariabless(String variabless) {
		this.variabless = variabless;
	}

	@Override
	public String toString() {
		return "Config [idConf=" + idConf + ", configName=" + configName + ", serverSou=" + serverSou + ", port=" + port
				+ ", userSou=" + userSou + ", passSou=" + passSou + ", dirSou=" + dirSou + ", fieldName=" + fieldName
				+ ", delimeterSou=" + delimeterSou + ", formatSou=" + formatSou + ", serverDes=" + serverDes
				+ ", DBNameDes=" + DBNameDes + ", useDes=" + useDes + ", passDes=" + passDes + ", targetTable="
				+ targetTable + ", fileType=" + fileType + ", importDir=" + importDir + ", successDir=" + successDir
				+ ", errorDir=" + errorDir + ", variabless=" + variabless + "]";
	}


}

package log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.DBConnection;

public class Log {
	// Các trường của Loan
	private int idLog;
	private int idConfig;
	private String state;
	private int numColumn;
	private String fileName;
	private String dataFileName;
	private Date dateUserInsertLog;
	private int active;
	private String result;

	public Log() {
		// TODO Auto-generated constructor stub
	}

	
	public Log(int idLog, int idConfig, String state, int numColumn, String fileName, String dataFileName, int active, String result) {
		super();
		this.idLog = idLog;
		this.idConfig = idConfig;
		this.state = state;
		this.numColumn = numColumn;
		this.fileName = fileName;
		this.dataFileName = dataFileName;
		this.active = active;
		this.result = result;
	}


	public List<Log> getLogsWithStatus(String condition) {
		List<Log> list = new ArrayList<>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM log where state = ?";
		Connection conn;
		try {
			conn = DBConnection.getConnection("dbcontrol");
			pst = conn.prepareStatement(sql);
			pst.setString(1, condition);
			rs = pst.executeQuery();
			while (rs.next()) {
				list.add(new Log(rs.getInt("idlog"), rs.getInt("idConfig"), rs.getString("state"),
						rs.getInt("numColumn"), rs.getString("fileName"), rs.getString("dataFileName"),
						rs.getInt("active"), rs.getString("result")));
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
		return list;
	}

	public boolean insertLog(int configID, String fileName, String fileType, String status, String fileTimeStamp) {
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

//	public static void main(String[] args) {
////		 List<Log> list = getLogsWithStatus("ER");
////		 for (Log log : list) {
////		 System.out.println(log.toString());
////		 }
////		 if (log.getFileWithStatus("ER")!=null) {
////		 System.out.println(log.getFileWithStatus("ER").toString());
////		 } else {
////		 System.out.println("No file status like that");
////		 }
//
//	}


	public Log(int idLog, int idConfig, String state, int numColumn, String fileName, String dataFileName,
			Date dateUserInsertLog, int active, String result) {
		super();
		this.idLog = idLog;
		this.idConfig = idConfig;
		this.state = state;
		this.numColumn = numColumn;
		this.fileName = fileName;
		this.dataFileName = dataFileName;
		this.dateUserInsertLog = dateUserInsertLog;
		this.active = active;
		this.result = result;
	}


	public int getIdLog() {
		return idLog;
	}


	public int getIdConfig() {
		return idConfig;
	}


	public String getState() {
		return state;
	}


	public int getNumColumn() {
		return numColumn;
	}


	public String getFileName() {
		return fileName;
	}


	public String getDataFileName() {
		return dataFileName;
	}


	public Date getDateUserInsertLog() {
		return dateUserInsertLog;
	}


	public int getActive() {
		return active;
	}


	public String getResult() {
		return result;
	}


	public void setIdLog(int idLog) {
		this.idLog = idLog;
	}


	public void setIdConfig(int idConfig) {
		this.idConfig = idConfig;
	}


	public void setState(String state) {
		this.state = state;
	}


	public void setNumColumn(int numColumn) {
		this.numColumn = numColumn;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}


	public void setDateUserInsertLog(Date dateUserInsertLog) {
		this.dateUserInsertLog = dateUserInsertLog;
	}


	public void setActive(int active) {
		this.active = active;
	}


	public void setResult(String result) {
		this.result = result;
	}


	@Override
	public String toString() {
		return "Log [idLog=" + idLog + ", idConfig=" + idConfig + ", state=" + state + ", numColumn=" + numColumn
				+ ", fileName=" + fileName + ", dataFileName=" + dataFileName + ", dateUserInsertLog="
				+ dateUserInsertLog + ", active=" + active + ", result=" + result + "]";
	}

	
}

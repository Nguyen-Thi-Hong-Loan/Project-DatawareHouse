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
	private String result;
	private int numColumn;
	private String fileName;
	private Date dateUserInsertLog;
	private Date dateLoadToStaging;

	public Log() {

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

	public String getResult() {
		return result;
	}

	public int getNumColumn() {
		return numColumn;
	}

	public String getFileName() {
		return fileName;
	}

	public Date getDateUserInsertLog() {
		return dateUserInsertLog;
	}

	public Date getDateLoadToStaging() {
		return dateLoadToStaging;
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

	public void setResult(String result) {
		this.result = result;
	}

	public void setNumColumn(int numColumn) {
		this.numColumn = numColumn;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setDateUserInsertLog(Date dateUserInsertLog) {
		this.dateUserInsertLog = dateUserInsertLog;
	}

	public void setDateLoadToStaging(Date dateLoadToStaging) {
		this.dateLoadToStaging = dateLoadToStaging;
	}

	@Override
	public String toString() {
		return "Log [idLog=" + idLog + ", idConfig=" + idConfig + ", state=" + state + ", result=" + result
				+ ", numColumn=" + numColumn + ", fileName=" + fileName + ", dateUserInsertLog=" + dateUserInsertLog
				+ ", dateLoadToStaging=" + dateLoadToStaging + "]";
	}

	

}

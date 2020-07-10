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

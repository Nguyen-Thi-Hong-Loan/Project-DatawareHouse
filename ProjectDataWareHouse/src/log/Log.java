package log;

import java.sql.Date;

public class Log {
	private int idLog;
	private int idConfig;
	private String state;
	private int numColumn;
	private String fileName;
	private Date dateUserInsertLog;

	public int getIdLog() {
		return idLog;
	}

	public void setIdLog(int idLog) {
		this.idLog = idLog;
	}

	public int getIdConfig() {
		return idConfig;
	}

	public void setIdConfig(int idConfig) {
		this.idConfig = idConfig;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getNumColumn() {
		return numColumn;
	}

	public void setNumColumn(int numColumn) {
		this.numColumn = numColumn;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getDateUserInsertLog() {
		return dateUserInsertLog;
	}

	public void setDateUserInsertLog(Date dateUserInsertLog) {
		this.dateUserInsertLog = dateUserInsertLog;
	}

}

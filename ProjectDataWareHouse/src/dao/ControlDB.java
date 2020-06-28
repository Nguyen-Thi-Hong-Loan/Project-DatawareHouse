package dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import connection.DBConnection;

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

	public boolean insertLog(String table, String file_status, int config_id, String timestamp,
			String stagin_load_count, String file_name) throws ClassNotFoundException {
		sql = "INSERT INTO " + table
				+ "(dataFileName,idConfig,state,numColumn,dateUserInsert) value (?,?,?,?,?)";
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
			PreparedStatement ps = connection.prepareStatement("INSERT INTO data_file (config_id, file_name, file_type, status, file_timestamp, active) value (?,?,?,?,?,1)");
			ps.setInt(1, configID);
			ps.setString(2, fileName);
			ps.setString(3, fileName.substring(fileName.indexOf('.')+1));
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

	public boolean createTable(String table_name, String variables, String column_list) throws ClassNotFoundException {
		sql = "CREATE TABLE "+table_name+" (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,";
		String[] vari = variables.split(",");
		String[] col = column_list.split(",");
		for(int i =0;i<vari.length;i++) {
			sql+=col[i]+" "+vari[i]+ " NOT NULL,";
		}
		sql = sql.substring(0,sql.length()-1)+")";
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
}

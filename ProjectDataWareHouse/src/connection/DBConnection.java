package connection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import control.Config;
import log.Log;
import modal.SendMail;
import modal.WriteBug;

public class DBConnection {

	@SuppressWarnings("unused")
	public static Connection getConnection(String db_Name) {
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/" + db_Name;
		String user = "root";
		String password = "1234567890@";
		try {
			if (con == null || con.isClosed()) {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(url, user, password);
				return con;

			} else {
				return con;
			}
		} catch (SQLException | ClassNotFoundException e) {
			return null;
		}
	}

	public static Connection getConSQL(String dbName) {
		try {

			String hostName = "localhost";
			String userName = "sa";
			String password = "1234567890@";
			// dang ky driver
			Connection conn;
			String connectionURL = "jdbc:sqlserver://" + hostName + ";databaseName=" + dbName;

			conn = DriverManager.getConnection(connectionURL, userName, password);
			System.out.println("ket not thanh cong");
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void main(String[] args) {
		System.out.println(DBConnection.getConSQL("controldb"));
	}
}

package connection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnection {
	// Phương thức tạo kết nối với databse:
	@SuppressWarnings("unused")
	public static Connection getConnection(String db_Name) {
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/" + db_Name;
		String user = "root";
		String password = "";
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
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionURL = "jdbc:sqlserver://" + hostName + ";databaseName=" + dbName;
			conn = DriverManager.getConnection(connectionURL, userName, password);
			System.out.println("ket not sql server thanh cong ");
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

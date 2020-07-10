package connection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import control.Config;
//import control.Config;
import log.Log;

public abstract class DBConnection {

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
	public static void main(String[] args) throws SQLException {
		PreparedStatement pst = DBConnection.getConnection("dbcontrol").prepareStatement("select * from config");
		ResultSet rs = pst.executeQuery();
		// int i=rs.
		rs.last();
		System.out.println(rs.getRow());
		rs.beforeFirst();
		while (rs.next()) {
			System.out.println(rs.getString(3));
			System.out.println("loi");
		}
	}
}

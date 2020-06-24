package lab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MyDBConnection {
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://localhost/information";

	public static final String USER = "root";
	public static final String PASS = "1234567890@";

	public static Connection connection() {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/information", "root", "1234567890@");
			System.out.println("success");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	public void thucThiSQL(String sql) throws SQLException {
		Connection connect = connection();
		PreparedStatement psmt = connect.prepareStatement(sql);
		psmt.executeUpdate(sql);
	}

	public int chonDuLieu(String sql) throws SQLException {
		Connection connect = connection();
		Statement stmt = connect.createStatement();
		int rs = stmt.executeUpdate(sql);
		return rs;
	}

	public PreparedStatement dungStatement(String sql) throws SQLException {
		return connection().prepareStatement(sql);
	}

	public static void main(String[] args) {
		System.out.println(new MyDBConnection().connection());
	}
}

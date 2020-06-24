package connection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import control.Config;

public class MySQLConnection extends DBConnection {

	public static final String URL_CONTROL = "jdbc:mysql://localhost/CONTROL";
	public static final String URL_STAGING = "jdbc:mysql://localhost/STAGING";
	public static final String URL_WAREHOUSE = "jdbc:mysql://localhost/WAREHOUSE";
	public static final String URL_DATAMART = "jdbc:mysql://localhost/DATAMART";

	public MySQLConnection(String url, String user, String pass) {
		super(url, user, pass);
	}

	public MySQLConnection() {
		url = URL_CONTROL;
	}

	public MySQLConnection(String url) {
		this.url = url;
	}

	public static void main(String[] args) throws SQLException {
		new MySQLConnection("jdbc:mysql://localhost/controldb", "root", "1234567890@").writeLogs();
		
//
//		for (Config config : conf) {
//			System.out.println(config.toString());
//
//		}

	}
}

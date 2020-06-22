package lab;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Queries {
	public void createTable() throws SQLException {
		List<Object> title = new ReadFile().readData("text\\DataFeedSpecification_17130110.xlsx").get(0);
		List<String> data = getData(new ReadFile().readData("text\\DataFeedSpecification_17130110.xlsx").get(1));

		MyDBConnection connect = new MyDBConnection();
		String query = "create table information.INFORMATION (";
		query += title.get(0).toString() + " " + data.get(0) + " unique auto_increment primary key, ";
		for (int i = 1; i < title.size() - 1; i++) {
			Object t1 = title.get(i).toString();
			t1 = title.get(i).toString().replaceAll("\\s", "_");
			System.out.println(t1);
			query += t1 + " " + data.get(i) + " , \n";

		}
		query += title.get(title.size() - 1).toString() + " " + data.get(title.size() - 1) + ") ";
		System.out.print(query);

		connect.chonDuLieu(query);
	}

	public List<String> getData(List<Object> infor) {
		List<String> result = new ArrayList<>();
		for (Object ob : infor) {
			if (ob.getClass().equals(Integer.class)) {
				result.add("int(6) not null");
			} else if (ob.getClass().equals(String.class)) {
				result.add("varchar(30) not null");
			} else if (ob.getClass().equals(LocalDate.class)) {
				result.add("date not null");

			} else if (ob.getClass().equals(Double.class)) {
				result.add("double not null");
			} else
				result.add(("varchar(30)"));
		}

		return result;
	}
	public void loadData(List<List<Object>> data) {

	}

	public static void main(String[] args) throws SQLException {
		new Queries().createTable();
	}
}

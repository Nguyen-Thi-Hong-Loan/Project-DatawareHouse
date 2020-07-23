package test;

import java.sql.SQLException;
import java.util.Scanner;

import dao.ControlDB;
import etl.DataProcess;
import etl.DataStaging;
import modal.Download;

public class TestDW {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
//		//Bước 1: Load từ server về local:
//		Download download = new Download();
		Scanner sc = new Scanner(System.in);
		System.out.println("Nhập id config cần download: ");
		int id = sc.nextInt();
//		System.out.println(download.saveDataFromFTPToLocal(id));
//		System.out.println("done");
		//Bước 2: Load từ local vào database staging:
		DataStaging dw = new DataStaging();
		dw.setConfig_id(id);
		dw.setState("ER");
		DataProcess dp = new DataProcess();
		ControlDB cdb = new ControlDB();
		cdb.setConfig_db_name("controldb");
		cdb.setTarget_db_name("database_staging");
		cdb.setTable_name("config");
		dp.setCdb(cdb);
		dw.ExtractToDB(dp);

	}
}

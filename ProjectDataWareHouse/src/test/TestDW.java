package test;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;

import dao.ControlDB;
import etl.DataProcess;
import etl.DataStaging;
import modal.Download;

public class TestDW extends TimerTask {
	static String[] listIdConfig = null;

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// Bước 1: Load từ server về local:
		listIdConfig = args.clone();
		TimerTask tasknew = new TestDW();
		Timer timer = new Timer();

		// scheduling the task at interval
		timer.schedule(tasknew, 0, 3000);


	}

	@Override
	public void run() {
		Download scpObject = new Download();
		DataStaging dw = new DataStaging();
		for (int i = 0; i < listIdConfig.length; i++) {
			try {
				int id = Integer.parseInt(listIdConfig[i]);
				scpObject.mainSCP(id);
				dw.mainStaging(id);
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}
}

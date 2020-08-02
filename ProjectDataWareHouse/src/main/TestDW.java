package main;

import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;

import etl.DataStaging;
import modal.Download;
import modal.SendMail;
import modal.WriteBug;

public class TestDW extends TimerTask {
	static String[] listIdConfig = null;

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// Bước 1: Load từ server về local:
		listIdConfig = args.clone();
		TimerTask tasknew = new TestDW();
		Timer timer = new Timer();

		// scheduling the task at interval
		timer.schedule(tasknew, 0, 10000);

	}

	@Override
	public void run() {
		System.out.println("Run my job: " + new Date());
		Download scpObject = new Download();
		DataStaging dw = new DataStaging();
		for (int i = 0; i < listIdConfig.length; i++) {

			try {
				int id = Integer.parseInt(listIdConfig[i]);
				System.out.println("id:  " + id);
				
				scpObject.mainSCP(id);
				dw.mainStaging(id);

			} catch (MessagingException e) {
				haveABug(e + "");
			} catch (ClassNotFoundException e) {
				haveABug(e + "");
			} catch (SQLException e) {
				haveABug(e + "");
			}

		}

	}

	public void haveABug(String erorr) {
		WriteBug wb = new WriteBug();
		wb.writeBug(erorr);
		new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);

	}
}

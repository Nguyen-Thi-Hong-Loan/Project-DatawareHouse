package main;

import java.sql.SQLException;
import java.util.Date;

import javax.mail.MessagingException;

import etl.DataStaging;
import modal.Download;
import modal.SendMail;
import modal.WriteBug;

public class TestDW {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// Bước 1: Load từ server về local:
		String[] listIdConfig = args.clone();

		System.out.println("Run my job: " + new Date());
		Download scpObject = new Download();
		DataStaging dw = new DataStaging();
		for (int i = 0; i < listIdConfig.length; i++) {

			try {
				int id = Integer.parseInt(listIdConfig[i]);
				System.out.println("id:  " + id);

				// run b.1
				scpObject.mainSCP(id);

				System.out.println("Run B2");
				// run b.2
				dw.mainStaging(id);

			} catch (MessagingException e) {
				haveABug(e + "", 1);
			} catch (ClassNotFoundException e) {
				haveABug(e + "", 1);
			} catch (SQLException e) {
				haveABug(e + "", 1);
			}

		}

	}

	public static void haveABug(String erorr, int i) {
		WriteBug wb = new WriteBug();
		wb.writeBug(erorr, i);
		if (i == 1) {
			new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE_BUG);
		} else
			new SendMail().sendMail("SUCCESSFUL", "NOTICE", wb.FILE_SUCCESS);
	}
}

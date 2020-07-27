package test;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import modal.Download;

public class TestDW extends TimerTask {

//		Download download = new Download();
//		Scanner sc = new Scanner(System.in);
//		System.out.println("Nhập id config cần download: ");
//		int id = sc.nextInt();
//		System.out.println(download.saveDataFromFTPToLocal(id));
//		System.out.println("done");

	static String[] listIdConfig = null;

	public static void main(String args[]) throws AddressException, MessagingException {
		listIdConfig = args.clone();
		TimerTask tasknew = new TestDW();
		Timer timer = new Timer();

		// scheduling the task at interval
		timer.schedule(tasknew, 0, 30000);
	}

	@Override
	public void run() {
		Download scpObject = new Download();
		for (int i = 0; i < listIdConfig.length; i++) {
			try {
				int id = Integer.parseInt(listIdConfig[i]);
				scpObject.mainSCP(id);
			} catch (MessagingException e) {
				e.printStackTrace();
			}

		}

	}
}

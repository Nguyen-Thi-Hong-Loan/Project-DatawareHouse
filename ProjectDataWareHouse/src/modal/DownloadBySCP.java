package modal;

import java.sql.SQLException;
import java.util.List;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;

import connection.MySQLConnection;
import control.Config;

public class DownloadBySCP {
	List<Config> listConf;

	static {
		try {
			System.loadLibrary("chilkat");
		} catch (UnsatisfiedLinkError e) {
			// viet bug vao file va send mail
			WriteBug wb = new WriteBug();
			wb.writeBug(e + "");
			new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);

			System.exit(1);
		}
	}

	public DownloadBySCP() {
		try {
			listConf = new MySQLConnection().loadAllConfs();
		} catch (SQLException e) {
			// viet bug vao file va send mail
			WriteBug wb = new WriteBug();
			wb.writeBug(e + "");
			new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);

		}
	}

	public void download(String hostName, int port, String userName, String pass, String remotePath, String localPath) {

		CkSsh ssh = new CkSsh();

		// unclock .........
		CkGlobal ck = new CkGlobal();
		ck.UnlockBundle("Hi");

		// Connect to an SSH server:
		for (Config config : listConf) {

			boolean success = ssh.Connect(config.getServerSou(), config.getPort());
			if (!success) {
				// viet bug vao file va send mail
				WriteBug wb = new WriteBug();
				wb.writeBug(ssh.lastErrorText() + "");
				new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);
				return;
			}

			// Wait a max of 3 seconds when reading responses..
			ssh.put_IdleTimeoutMs(3000);

			// Authenticate using login/password:
			success = ssh.AuthenticatePw(config.getUserSou(), config.getPassSou());
			if (!success) {

				// viet bug vao file va send mail
				WriteBug wb = new WriteBug();
				wb.writeBug(ssh.lastErrorText() + "");
				new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);

				return;
			}
			CkScp scp = new CkScp();
			success = scp.UseSsh(ssh);
			if (!success) {
				// viet bug vao file va send mail
				WriteBug wb = new WriteBug();
				wb.writeBug(ssh.lastErrorText() + "");
				new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);

				return;
			}
			// download directory
			scp.put_SyncMustMatch(config.getFormatSou());
			System.out.println(config.getFormatSou());

//		String remotePath = "/volume1/ECEP/song.nguyen/DW_2020/data";
//		String localPath = "E:\\Tai_Lieu\\HK2-----3\\DatawareHouse\\FILE";

			success = scp.SyncTreeDownload(remotePath, localPath, 2, false);
			if (!success) {
				WriteBug wb = new WriteBug();
				wb.writeBug(ssh.lastErrorText() + "");
				new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);

				return;
			}

			System.out.println("SCP download file success.");
		}
		ssh.Disconnect();

	}

}

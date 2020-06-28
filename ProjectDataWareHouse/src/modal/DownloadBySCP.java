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
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}

	public DownloadBySCP() {
		try {
			listConf = new MySQLConnection("jdbc:mysql://localhost/controldb", "root", "1234567890@").loadAllConfs();
			System.out.println(listConf.size());
		} catch (SQLException e) {
			e.printStackTrace();
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
				System.out.println(ssh.lastErrorText());
				return;
			}

			// Wait a max of 3 seconds when reading responses..
			ssh.put_IdleTimeoutMs(3000);

			// Authenticate using login/password:
			success = ssh.AuthenticatePw(config.getUserSou(), config.getPassSou());
			if (!success) {
				System.out.println(ssh.lastErrorText());
				return;
			}
			CkScp scp = new CkScp();
			success = scp.UseSsh(ssh);
			if (!success) {
				System.out.println(ssh.lastErrorText());
				return;
			}
			// download directory
			scp.put_SyncMustMatch(config.getFormatSou());
			System.out.println(config.getFormatSou());
//		String remotePath = "/volume1/ECEP/song.nguyen/DW_2020/data";
//		String localPath = "E:\\Tai_Lieu\\HK2-----3\\DatawareHouse\\FILE";

			success = scp.SyncTreeDownload(remotePath, localPath, 2, false);
			if (!success) {
				System.out.println(ssh.lastErrorText());
				return;
			}

			System.out.println("SCP download file success.");
		}
		ssh.Disconnect();

	}

	public static void main(String[] args) {
		new DownloadBySCP().download("drive.ecepvn.org", 2227, "guest_access", "123456",
				"/volume1/ECEP/song.nguyen/DW_2020/data", "E:\\Tai_Lieu\\HK2-----3\\DatawareHouse\\FILE");

	}
}

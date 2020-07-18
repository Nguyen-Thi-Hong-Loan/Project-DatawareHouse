package modal;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;

import connection.DBConnection;
import control.Config;

public class Download {
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

	public boolean download(String local_download_dir, String remoteFilePath, String serverAddress, int port,
			String username, String password, String format) {
		CkSsh ssh = new CkSsh();
		System.out.println("kkkoooooooooooooo");

		// unclock .........
		CkGlobal ck = new CkGlobal();
		ck.UnlockBundle("Hi");

		// Connect to an SSH server:
		boolean success = ssh.Connect(serverAddress, port);
		if (!success) {
			// viet bug vao file va send mail
			WriteBug wb = new WriteBug();
			wb.writeBug(ssh.lastErrorText() + "");
			new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);
			return false;
		}

		// Wait a max of 3 seconds when reading responses..
		ssh.put_IdleTimeoutMs(3000);

		// Authenticate using login/password:
		success = ssh.AuthenticatePw(username, password);
		if (!success) {

			// viet bug vao file va send mail
			WriteBug wb = new WriteBug();
			wb.writeBug(ssh.lastErrorText() + "");
			new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);

			return false;
		}
		CkScp scp = new CkScp();
		success = scp.UseSsh(ssh);
		if (!success) {
			// viet bug vao file va send mail
			WriteBug wb = new WriteBug();
			wb.writeBug(ssh.lastErrorText() + "");
			new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);

			return false;
		}
		scp.put_SyncMustMatch(format);
		System.out.println(format);

		// String remotePath = "/volume1/ECEP/song.nguyen/DW_2020/data";
		// String localPath = "E:\\Tai_Lieu\\HK2-----3\\DatawareHouse\\FILE";

		success = scp.SyncTreeDownload(remoteFilePath, local_download_dir, 2, false);
		if (!success) {
			WriteBug wb = new WriteBug();
			wb.writeBug(ssh.lastErrorText() + "");
			new SendMail().sendMail("We have a bug", "NOTICE", wb.FILE);

			return false;
		}

		System.out.println("SCP download file success.");
		ssh.Disconnect();
		return true;

	}

	public static void saveDataFromFTPToLocal() {

		Connection conn = null;
		PreparedStatement pr = null;
		try {
			conn = DBConnection.getConnection("controldb");
			String sql = "select * from config;";
			pr = conn.prepareStatement(sql);
			ResultSet rs = pr.executeQuery();

			while (rs.next()) {
				Config conf = new Config();
				conf.setIdConf(rs.getInt("idConfig"));
				conf.setServerSou(rs.getString("serverSou"));
				conf.setPort(rs.getInt("port"));
				conf.setUserSou(rs.getString("userSou"));
				conf.setPassSou(rs.getString("passSou"));
				conf.setDirSou(rs.getString("directorySou"));
				conf.setFormatSou(rs.getString("formatSou"));
				conf.setServerDes(rs.getString("serverDes"));
				conf.setUseDes(rs.getString("userDes"));
				conf.setPassDes(rs.getString("passDes"));

				boolean download = new Download().download(conf.getDirSou(), "/volume1/ECEP/song.nguyen/DW_2020/data",
						conf.getServerSou(), conf.getPort(), conf.getUserSou(), conf.getPassSou(), conf.getFormatSou());
				if (download) {

					List<String> lsFile = readLsFile(conf.getDirSou());
					for (String fName : lsFile) {

						String newSql = "insert into log ( idConfig, state,result, numColumn, fileName, dateUserInsert) "
								+ "values (?,?,?,?,?,?)";
						pr = conn.prepareStatement(newSql);

						int idConfig = conf.getIdConf();
						String state = "ER";
						String result = "OK";
						int numColumn = 0;

						pr.setInt(1, idConfig);
						pr.setString(2, state);
						pr.setString(3, result);
						pr.setInt(4, numColumn);
						pr.setString(5, fName);
						pr.setTimestamp(6, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

						pr.executeUpdate();
						System.out.println("ok");
					}
				} else {
					System.out.println("nooooooooooo");

				}

			}

		} catch (Exception e) {
		}

	}

	public static List<String> readLsFile(String directory) {
		List<String> lsFile = new ArrayList<String>();
		File file = new File(directory);
		if (file.isDirectory()) {
			File[] ls_F = file.listFiles();
			for (File f : ls_F) {
				lsFile.add(f.getName());
			}
		}

		return lsFile;

	}

	// đếm đc file .txt mà thôi

	public static int countLine(String file) {
		int count = 0;
		// Scanner console = new Scanner(System.in);
		//
		// System.out.println("File to be read: ");
		// String inputFile = console.next();

		File f = new File(file);
		try {
			Scanner in = new Scanner(f);

			int words = 0;
			int lines = 0;
			int chars = 0;
			while (in.hasNextLine()) {
				lines++;
				String line = in.nextLine();
				for (int i = 0; i < line.length(); i++) {
					if (line.charAt(i) != ' ' && line.charAt(i) != '\n')
						chars++;
				}
				words += new StringTokenizer(line, " ,;:.").countTokens();
			}
			System.out.println(words + ", " + lines + ", " + chars);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return count;
	}

	// public static int countFExcel(String file) {
	// Workbook workbook = getWorkbook(file);
	//
	// // Create sheet
	// Sheet sh = workbook.createSheet("Books"); // Create sheet with sheet name
	//
	// int count = sh.getRow(0).getPhysicalNumberOfCells();
	// return count;
	// }

	public static void main(String[] args) {
		Download.saveDataFromFTPToLocal();
		// Download.countLine("F:\\Tai_Lieu\\HK2-----3\\DatawareHouse\\FILE\\sinhvien_chieu_nhom4.txt");

		// List<String> l = new
		// Download().readLsFile("F:\\Tai_Lieu\\HK2-----3\\DatawareHouse\\FILE");
		// for (String string : l) {
		// System.out.println(string);
		//
		// }
	}

}

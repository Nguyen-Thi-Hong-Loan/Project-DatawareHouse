<<<<<<< HEAD
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
=======
package modal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;

import connection.DBConnection;
import control.Config;

public class Download {
	static {
		try {
			System.load("E:\\Solfware\\chilkat-9.5.0-jdk8-x64\\chilkat-9.5.0-jdk8-x64\\chilkat.dll");
		} catch (UnsatisfiedLinkError e) {

			// viet bug vao file va send mail
			haveANotice(e + "", 1);
			System.exit(1);
		}
	}

	public boolean download(String local_download_dir, String remoteFilePath, String serverAddress, int port,
			String username, String password, String format) {
		CkSsh ssh = new CkSsh();

		// unclock .........
		CkGlobal ck = new CkGlobal();
		ck.UnlockBundle("Hi");

		// Connect to an SSH server with  server address and port
		boolean success = ssh.Connect(serverAddress, port);
		
		//check success 
		if (!success) {
			// viet bug vao file va send mail
			haveANotice(ssh.lastErrorText() + "", 1);
			return false;
		}

		// Wait a max of 3 seconds when reading responses..
		ssh.put_IdleTimeoutMs(3000);

		// Authenticate using login/password:
		success = ssh.AuthenticatePw(username, password);
		if (!success) {
			// viet bug vao file va send mail
			haveANotice(ssh.lastErrorText() + "", 1);
			return false;
		}

		// Once the SSH object is connected and authenticated, we use it
		// in our SCP object.
		CkScp scp = new CkScp();
		success = scp.UseSsh(ssh);
		if (!success) {
			// viet bug vao file va send mail

			haveANotice(ssh.lastErrorText() + "", 1);
			return false;
		}

		// Download synchronization modes:
		// mode=0: Download all files
		// mode=1: Download all files that do not exist on the local filesystem.
		// mode=2: Download newer or non-existant files.
		// mode=3: Download only newer files.
		// If a file does not already exist on the local filesystem, it is not
		// downloaded from the server.
		// mode=5: Download only missing files or files with size differences.
		// mode=6: Same as mode 5, but also download newer files.

		scp.put_SyncMustMatch(format);

		success = scp.SyncTreeDownload(remoteFilePath, local_download_dir, 2, false);
		if (!success) {
			haveANotice(ssh.lastErrorText() + "", 1);
			return false;
		}

		ssh.Disconnect();
		System.out.println("SCP download file success.");
		return true;

	}

	public boolean saveDataFromFTPToLocal(int id) {

		Connection conn = null;
		CallableStatement cstm = null;

		try {
			conn = DBConnection.getConSQL("controldb");
			// khoi tao loi goi thuc thi thu tuc
			String sql = "{call sp_loadConfig (?)}";
			cstm = conn.prepareCall(sql);
			
			// Set parameter values
			cstm.setInt(1, id);
			ResultSet rs = cstm.executeQuery();
			
			if (rs.next()) {
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
				conf.setFileType(rs.getString("fileType"));

				boolean download = new Download().download(conf.getDirSou(), "/volume1/ECEP/song.nguyen/DW_2020/data",
						conf.getServerSou(), conf.getPort(), conf.getUserSou(), conf.getPassSou(), conf.getFormatSou());

				//kiem tra download co thanh cong ko de viet log
				if (download) {
					//lay ra ds lisst file trong local
					List<String> lsFile = readLsFile(conf.getDirSou());
					for (String fName : lsFile) {
						String fileName = conf.getDirSou() + "\\" + "\\" + fName;
						int numColumn = (fileName.endsWith(".txt")) ? countLineTxt(fileName)
								: (fileName.endsWith(".xlsx") ? numColumn = countFExcel(fileName) : 0);
						//viet vao file log
						loadLog(fName, numColumn, id, "controldb");

					}
					//hoan thanh xong thi gui mail thanh cong
					haveANotice("SUCCESS", 2);
				}

			}

		} catch (Exception e) {
			haveANotice(e + "", 1);
			return false;
		} finally {
			try {
				cstm.close();
				conn.close();
			} catch (SQLException e) {
				haveANotice(e + "", 1);
			}
		}
		return true;

	}

	//thuc hien viet log
	public static void loadLog(String name, int num, int idCf, String db) {
		try {
			Connection conn = DBConnection.getConSQL(db);
			String sql = "{call sp_insertLog (?,?,?)}";
			CallableStatement cstm = conn.prepareCall(sql);
			
			// Set parameter values
			cstm.setString(1, name);
			cstm.setInt(2, num);
			cstm.setInt(3, idCf);
			cstm.execute();
			System.out.println("ghi log thanh cong");
			cstm.close();
			conn.close();
		} catch (Exception e) {
			haveANotice(e + "", 1);
		}
	}

	// read a list file
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

	
	//thong bao loi va gui mail
	public static void haveANotice(String mess, int i) {
		String sendMess = "TIME: " + new Date() + "\n" + mess;

		if (i == 1) {
			new SendMail().sendMail("We have a bug", "NOTICE", sendMess);
		} else
			new SendMail().sendMail("SUCCESSFUL", "NOTICE", sendMess);
	}

	// count row in file excel
	public static int countFExcel(String file) {
		int count = 0;
		try {
			// Creating a Workbook from an Excel file (.xls or .xlsx)
			Workbook workbook = WorkbookFactory.create(new File(file));

			// Getting the Sheet at index zero
			Sheet sheet = workbook.getSheetAt(0);

			// get all row in file
			Iterator<Row> rowIterator = sheet.rowIterator();

			while (rowIterator.hasNext()) {
				// get next row
				Row row = rowIterator.next();
				count++;
			}
			// Closing the workbook
			workbook.close();

		} catch (EncryptedDocumentException e) {
			haveANotice(e + "", 1);
		} catch (IOException e) {
			haveANotice(e + "", 1);
		}
		return count;
	}

	//count line in file .txt
	public static int countLineTxt(String s_file) {
		try {

			File file = new File(s_file);

			// kt file co ton tai hay k
			if (file.exists()) {

				FileReader fr = new FileReader(file);
				LineNumberReader lnr = new LineNumberReader(fr);

				int linenumber = 0;

				while (lnr.readLine() != null) {
					linenumber++;
				}

				System.out.println("Total number of lines : " + linenumber);

				lnr.close();
				return linenumber;

			} else {
				System.out.println("File does not exists!");
			}

		} catch (IOException e) {
			haveANotice(e + "", 1);
		}
		return 0;
	}

	//main chay download file
	public void mainSCP(int id_config) throws AddressException, MessagingException {
		Download download = new Download();
		download.saveDataFromFTPToLocal(id_config);

	}

}
>>>>>>> eeb9bf5a9dabfd838e26887241b0e0c7175f885e

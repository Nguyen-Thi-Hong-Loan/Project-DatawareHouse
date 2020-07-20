package modal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

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
//			System.loadLibrary("chilkat");
			System.load("E:\\Solfware\\chilkat-9.5.0-jdk8-x64\\chilkat.dll");
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
				conf.setFileType(rs.getString("fileType"));

				boolean download = new Download().download(conf.getDirSou(), "/volume1/ECEP/song.nguyen/DW_2020/data",
						conf.getServerSou(), conf.getPort(), conf.getUserSou(), conf.getPassSou(), conf.getFormatSou());
				if (download) {

					List<String> lsFile = readLsFile(conf.getDirSou());
					for (String fName : lsFile) {

						String fileName = conf.getDirSou() + "\\" + "\\" + fName;
						System.out.println("file:     ----   " + fileName);
						int numColumn = (fileName.endsWith(".txt")) ? countLineTxt(fileName)
								: (fileName.endsWith(".xlsx") ? numColumn = countFExcel(fileName) : 0);

						loadLog(fName, numColumn, id, "controldb");

					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				cstm.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;

	}

	public static void loadLog(String name, int num, int idCf, String db) {
		try {
			Connection conn = DBConnection.getConSQL(db);
			String sql = "{call sp_insertLog (?,?,?)}";
			CallableStatement cstm = conn.prepareCall(sql);
			// Set parameter values
			cstm.setString(1, name);
			cstm.setInt(2, num);
			cstm.setInt(3, idCf);
			System.out.println(cstm.execute());
			
			System.out.println("ghi log thanh cong");

			cstm.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

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
			e.printStackTrace();
		}
		return 0;
	}

}

package modal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class WriteBug {
	public static final String FILE_BUG = "C:\\Users\\Krystal\\Documents\\Data_Warehouse\\FILE_LOG\\BUG\\bug.txt";
	public static final String FILE_SUCCESS = "C:\\Users\\Krystal\\Documents\\Data_Warehouse\\FILE_LOG\\SUCCESS\\success.txt";

	public void writeBug(String mess, int i) {
		try {
			
			// Getting the Path object
			Path path = Paths.get(FILE_BUG);
			Path pathsucc = Paths.get(FILE_BUG);
			// Creating a BufferedWriter object
			BufferedWriter write = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			BufferedWriter writesucc = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			if (i == 1) {
				// Appending the UTF-8 String to the file
				write.append("NOTICE: " + mess + "\nTIME: " + new Date());
				// Flushing data to the file
				write.flush();
				System.out.println("==========WARRING=========ERROR======== HAVE_A_BUG===========");
			}
			else {
				// Appending the UTF-8 String to the file
				writesucc.append("SUCCESS: " + mess + "\nTIME: " + new Date());
				// Flushing data to the file
				writesucc.flush();
				System.out.println("==========SUCCESS=========SUSCESS========COMPLETE===========");
			
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

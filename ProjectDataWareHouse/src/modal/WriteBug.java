<<<<<<< HEAD
package modal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WriteBug {
	public static final String FILE = "text/bug.txt";

	public void writeBug(String bug) {
		try {
			// Getting the Path object
			Path path = Paths.get(FILE);
			// Creating a BufferedWriter object
			BufferedWriter write = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			// Appending the UTF-8 String to the file
			write.append("NOTICE: " + bug + "\nTIME: " + java.util.Calendar.getInstance().getTime());
			// Flushing data to the file
			write.flush();
			System.out.println("Data entered into the file");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
=======
package modal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WriteBug {
	public static final String FILE = "text/bug.txt";

	public void writeBug(String bug) {
		try {
			// Getting the Path object
			Path path = Paths.get(FILE);
			// Creating a BufferedWriter object
			BufferedWriter write = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			// Appending the UTF-8 String to the file
			write.append("NOTICE: " + bug + "\nTIME: " + java.util.Calendar.getInstance().getTime());
			// Flushing data to the file
			write.flush();
			System.out.println("Data entered into the file");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
>>>>>>> eeb9bf5a9dabfd838e26887241b0e0c7175f885e

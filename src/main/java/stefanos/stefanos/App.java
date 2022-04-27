package stefanos.stefanos;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Apache log parser!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Apache log parser!");
		try {
			// Connect to the database and initialize table:
			DatabaseHelper databaseHelper = new DatabaseHelper();

			// Parse Apache log line by line:
			try {
				
				ApacheLogParser apacheLogParser = new ApacheLogParser();
				Matcher matcher;
				
				// Open file:
				FileInputStream fileInputStream = new FileInputStream(
						"C:\\Users\\Stefanos\\Downloads\\NASA_access_log_Aug95\\small"); //NASA_access_log_Aug95
				Scanner sc = new Scanner(fileInputStream);
				// Read line by line:
				int id = 0;
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					
					matcher = apacheLogParser.parseLine(line);
					apacheLogParser.printLastMatch();

					// Insert log line information in the database.
					databaseHelper.insertLine(id, matcher);
					id++;
				}
				// Close the file:
				sc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Close the database and delete the temporary database file:
			databaseHelper.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}

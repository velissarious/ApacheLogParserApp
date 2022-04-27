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
	private static final int BATCH_NUMBER = 8000;

	public static void main(String[] args) {
		System.out.println("Apache log parser!");
		try {
			// Connect to the database and initialize table:
			DatabaseHelper databaseHelper = new DatabaseHelper();

			// Parse Apache log line by line:
			try {

				ApacheLogParser apacheLogParser = new ApacheLogParser();
				Matcher matcher;

				long startTime = System.currentTimeMillis(); // Timing code.
				// Open file:
				FileInputStream fileInputStream = new FileInputStream(
						"C:\\Users\\Stefanos\\Downloads\\NASA_access_log_Aug95\\big"); // NASA_access_log_Aug95
				Scanner sc = new Scanner(fileInputStream);
				// Read line by line:
				int id = 0;
				int lineCount = 0;
				while (sc.hasNextLine()) {
					String line = sc.nextLine();

					matcher = apacheLogParser.parseLine(lineCount, line);

					if (apacheLogParser.found()) {
						// Prepare log lines to insert into the database:
						databaseHelper.insertLine(id, matcher);
						id++;
					}

					// Insert a number of lines at a time:
					if (id % BATCH_NUMBER == 0) {
						databaseHelper.commitLines();
					}

					lineCount++;
				}
				// Insert all remaining lines in the database:
				databaseHelper.commitLines();
				// Close the file:
				sc.close();

				long endTime = System.currentTimeMillis(); // Timing code.

				System.out.println("Insert to database duration: " + (endTime - startTime) + " ms");
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

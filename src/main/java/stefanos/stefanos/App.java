package stefanos.stefanos;

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
			String ApacheLogSample = "123.45.67.89 - - [27/Oct/2000:09:27:09 -0400] \"GET /java/javaResources.html "
					+ "HTTP/1.0\" 200 10450";

			System.out.println("Apache log input line: " + ApacheLogSample);

			ApacheLogParser apacheLogParser = new ApacheLogParser();
			Matcher matcher = apacheLogParser.parseLine(ApacheLogSample);
			apacheLogParser.printLastMatch();

			// Insert log line information in the database.
			databaseHelper.insertLine(matcher);

			databaseHelper.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}

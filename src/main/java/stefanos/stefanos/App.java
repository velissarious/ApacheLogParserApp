package stefanos.stefanos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * Apache log parser!
 *
 */
public class App {
	private static final int BATCH_NUMBER = 9000;
	private static final String DEFAULT_REPORT_NAME = "report.txt";

	public static void main(String[] args) {
		long startTime;
		long endTime;

		System.out.println("Apache log parser!");
		try {
			// Connect to the database and initialize table:
			DatabaseHelper databaseHelper = new DatabaseHelper();

			// Parse Apache log line by line:
			try {

				ApacheLogParser apacheLogParser = new ApacheLogParser();
				Matcher matcher;

				startTime = System.currentTimeMillis(); // Timing code.
				// Open Apache log file:
				FileReader fileReader = new FileReader("C:\\Users\\Stefanos\\Downloads\\NASA_access_log_Aug95\\big");
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				// Read Apache log file line by line:
				int current_batch_number = 0;
				int lineCount = 0;
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					matcher = apacheLogParser.parseLine(lineCount, line);

					if (apacheLogParser.found()) {
						// Prepare log lines to insert into the database:
						databaseHelper.insertLine(matcher);
						current_batch_number++;
					}

					// Insert a number of lines at a time:
					if (current_batch_number == BATCH_NUMBER) {
						databaseHelper.commitLines();
						current_batch_number = 0;
					}

					lineCount++;
				}
				// Insert all remaining lines in the database:
				databaseHelper.commitLines();
				// Close the file:
				bufferedReader.close();

				endTime = System.currentTimeMillis(); // Timing code.
				System.out.println("Insert to database duration: " + (endTime - startTime) + " ms");

				// Generate the report:
				String reportName = DEFAULT_REPORT_NAME;
				String report = "";
				report += databaseHelper.getTop10RequestedPagesAndRequestNumber();
				report += databaseHelper.getSuccessfulRequestsPercentage();
				report += databaseHelper.getUnsuccessfulRequestsPercentage();
				report += databaseHelper.getTop10UnsuccessfulPageRequests();
				report += databaseHelper.getTop10HostsAndRequestsNumber();
				report += databaseHelper.getTop5PagesOfTop10Hosts();				
				System.out.println(report);
				
				// Write the report:
				FileWriter reportFile = new FileWriter(reportName);
				BufferedWriter bufferedWriter = new BufferedWriter(reportFile);
				bufferedWriter.write(report);
				bufferedWriter.close();
				
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

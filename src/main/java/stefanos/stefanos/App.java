package stefanos.stefanos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Apache Log Parser App!
 *
 */
public class App {
	private static final int BATCH_NUMBER = 9000;
	private static final String DEFAULT_REPORT_NAME = "report.txt";
	private static final String DEFAULT_INPUT_FILE_NAME = "access_log_Aug95";

	@Parameter(names = { "-v", "-verbose" }, description = "Level of verbosity")
	private static Boolean verbose = false;

	@Parameter(names = { "-o", "-option" }, description = "Run only a single option")
	private static Integer option = 0;

	@Parameter(names = { "-r", "-report" }, description = "Specify report file name")
	private static String reportName = DEFAULT_REPORT_NAME;

	@Parameter(names = { "-i", "-input" }, description = "Specify input file name")
	private static String inputFileName = DEFAULT_INPUT_FILE_NAME;

	// Parse the options (flags) and other command line arguments:
	public static void main(String... argv) {
		App main = new App();
		try {
			JCommander.newBuilder().addObject(main).build().parse(argv);
		} catch (ParameterException exc) {
			// @formatter:off
			System.out.println("Invalid option provided, valid options:\n" 
					+ "-v or -verbose,\n" 
					+ "-o [number 1,2,3,4,5 or 7] or -option [number 1,2,3,4,5 or 7],\n"
					+ "-r [filename] or -report  [filename],\n" 
					+ "-i [filename] or -input [filename]\n");
			// @formatter:on
			System.exit(1);
		}
		App.run();
	}

	public static void run() {
		if (Boolean.TRUE.equals(verbose))
			System.out.println("Apache log parser!");

		if (option == 6 || option > 7 || option < 0) {
			System.err.println("Incorrect value for the -o flag, correct values are\n" + "0. All of the items bellow\n"
					+ "1. Top 10 requested pages and the number of requests made for each\n"
					+ "2. Percentage of successful requests (anything in the 200s and 300s range)\n"
					+ "3. Percentage of unsuccessful requests (anything that is not in the 200s or 300s range)\n"
					+ "4. Top 10 unsuccessful page requests \n"
					+ "5. The top 10 hosts making the most requests, displaying the IP address and number of requests made.\n"
					+ "and 7. For each of the top 10 hosts, show the top 5 pages requested and the number of requests for each page\n");
			System.exit(1);
		}

		try {
			// Connect to the database and initialize table:
			DatabaseHelper databaseHelper = new DatabaseHelper();

			// Parse Apache log line by line:
			try {

				ApacheLogParser apacheLogParser = new ApacheLogParser();
				Matcher matcher;

				long startTime = System.currentTimeMillis(); // Timing code.
				// Open Apache log file:
				FileReader fileReader = new FileReader(inputFileName);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				// Read Apache log file line by line:
				int currentBatchNumber = 0;
				int lineCount = 0;
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					matcher = apacheLogParser.parseLine(lineCount, line);

					if (apacheLogParser.found()) {
						// Prepare log lines to insert into the database:
						databaseHelper.insertLine(matcher);
						currentBatchNumber++;
					}

					// Insert a number of lines at a time:
					if (currentBatchNumber == BATCH_NUMBER) {
						databaseHelper.commitLines();
						currentBatchNumber = 0;
					}

					lineCount++;
				}
				// Insert all remaining lines in the database:
				databaseHelper.commitLines();
				// Close the file:
				bufferedReader.close();

				long endTime = System.currentTimeMillis(); // Timing code.
				if (Boolean.TRUE.equals(verbose))
					System.out.println("Insert to database duration: " + (endTime - startTime) + " ms");

				// Generate the report:
				String report = "";
				if (option == 0 || option == 1)
					report += databaseHelper.getTop10RequestedPagesAndRequestNumber();
				if (option == 0 || option == 2)
					report += databaseHelper.getSuccessfulRequestsPercentage();
				if (option == 0 || option == 3)
					report += databaseHelper.getUnsuccessfulRequestsPercentage();
				if (option == 0 || option == 4)
					report += databaseHelper.getTop10UnsuccessfulPageRequests();
				if (option == 0 || option == 5)
					report += databaseHelper.getTop10HostsAndRequestsNumber();
				if (option == 0 || option == 7)
					report += databaseHelper.getTop5PagesOfTop10Hosts();
				if (Boolean.TRUE.equals(verbose))
					System.out.println(report);

				// Write the report:
				FileWriter reportFile = new FileWriter(reportName);
				BufferedWriter bufferedWriter = new BufferedWriter(reportFile);
				bufferedWriter.write(report);
				bufferedWriter.close();

			} catch (IOException e) {
				System.err.println("Please specify a correct input file using the -i option.");
			}

			// Close the database and delete the temporary database file:
			databaseHelper.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}

package stefanos.stefanos;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.junit.Test;

public class DatabaseHelperTest {

	@Test
	public void insertShouldWork() throws SQLException, ClassNotFoundException {
		final String logLine = "uplherc.upl.com - - [01/Aug/1995:00:00:08 -0400] \"GET /images/MOSAIC-logosmall.gif HTTP/1.0\" 304 0";

		ApacheLogParser apacheLogParser = new ApacheLogParser();
		Matcher matcher = apacheLogParser.parseLine(0, logLine);
		DatabaseHelper databaseHelper = new DatabaseHelper();
		databaseHelper.insertLine(matcher);
		databaseHelper.commitLines();
		String result = databaseHelper.getTop10HostsAndRequestsNumber();
		String expectedResult = "5. The top 10 hosts making the most requests, displaying the IP address and number of requests made.\n"
				+ "----------------------------------------------------------------------------------------------------\n"
				+ "uplherc.upl.com 1\n\n";
		databaseHelper.close();
		assertEquals(result, expectedResult);
	}

	@Test
	public void testPercentages() throws SQLException, ClassNotFoundException {
		ApacheLogParser apacheLogParser = new ApacheLogParser();
		DatabaseHelper databaseHelper = new DatabaseHelper();
		String successfulResult = "";
		String unsuccessfulResult = "";
		List<String> log = generateLog();
		for (String logLine : log) {
			Matcher matcher = apacheLogParser.parseLine(0, logLine);
			databaseHelper.insertLine(matcher);
			databaseHelper.commitLines();
		}
		successfulResult = databaseHelper.getSuccessfulRequestsPercentage();
		unsuccessfulResult = databaseHelper.getUnsuccessfulRequestsPercentage();
		databaseHelper.close();
		assertTrue(successfulResult.contains("66.6666"));
		assertTrue(unsuccessfulResult.contains("33.3333"));
	}

	@Test
	public void testTop10RequestedPagesAndRequestNumber() throws SQLException, ClassNotFoundException {
		ApacheLogParser apacheLogParser = new ApacheLogParser();
		DatabaseHelper databaseHelper = new DatabaseHelper();
		String results = "";
		List<String> log = generateLog();
		for (String logLine : log) {
			Matcher matcher = apacheLogParser.parseLine(0, logLine);
			databaseHelper.insertLine(matcher);
			databaseHelper.commitLines();
		}
		results = databaseHelper.getTop10RequestedPagesAndRequestNumber();
		databaseHelper.close();
		// The lines should be 10 for the top 10 and two for the title and an empty
		// newline:
		assertEquals(10 + 3, results.lines().count());
		// The correct pages should be contained 0-9:
		for (int page = 0; page < 9; page++) {
			assertTrue(results.contains(page + ".html " + (60 - (3 * page))));
		}
	}

	@Test
	public void testTop10UnsuccessfulRequests() throws SQLException, ClassNotFoundException {
		ApacheLogParser apacheLogParser = new ApacheLogParser();
		DatabaseHelper databaseHelper = new DatabaseHelper();
		String results = "";
		List<String> log = generateLog();
		for (String logLine : log) {
			Matcher matcher = apacheLogParser.parseLine(0, logLine);
			databaseHelper.insertLine(matcher);
			databaseHelper.commitLines();
		}
		results = databaseHelper.getTop10UnsuccessfulPageRequests();
		databaseHelper.close();
		// The lines should be 10 for the top 10 and two for the title and an empty
		// newline:
		assertEquals(10 + 3, results.lines().count());
		// The correct pages should be contained 0-9:
		for (int page = 0; page < 9; page++) {
			assertTrue(results.contains(page + ".html"));
		}
	}

	@Test
	public void testTop10HostsAndRequestsNumber() throws SQLException, ClassNotFoundException {
		ApacheLogParser apacheLogParser = new ApacheLogParser();
		DatabaseHelper databaseHelper = new DatabaseHelper();
		String results = "";
		List<String> log = generateLog();
		for (String logLine : log) {
			Matcher matcher = apacheLogParser.parseLine(0, logLine);
			databaseHelper.insertLine(matcher);
			databaseHelper.commitLines();
		}
		results = databaseHelper.getTop10HostsAndRequestsNumber();
		databaseHelper.close();
		// The lines should be 10 for the top 10 and two for the title and an empty
		// newline:
		assertEquals(10 + 3, results.lines().count());
		// The correct hosts should be contained 0-9:
		for (int page = 0; page < 9; page++) {
			assertTrue(results.contains(page + ".host.com " + (40 - 2 * page)));
		}
	}

	@Test
	public void testTop5PagesOfTop10Hosts() throws SQLException, ClassNotFoundException {
		ApacheLogParser apacheLogParser = new ApacheLogParser();
		DatabaseHelper databaseHelper = new DatabaseHelper();
		String results = "";
		List<String> log = generateLog();
		// Modify example to make pages 0-4 most visited for each host:
		List<String> additinalLog = generateLog(20, 5, false);
		log.addAll(additinalLog);
		for (String logLine : log) {
			Matcher matcher = apacheLogParser.parseLine(0, logLine);
			databaseHelper.insertLine(matcher);
			databaseHelper.commitLines();
		}
		results = databaseHelper.getTop5PagesOfTop10Hosts();
		databaseHelper.close();
		// The lines should be 5 for the top 5 and two for the title and an empty
		// newline:
		System.out.println(results);
		// Lines should be 5 for pages the 1 for host
		// There 10 hosts
		// and two lines for the title:
		assertEquals(((5 + 1) + 1) * 10 + 2, results.lines().count());
		// The correct hosts should be contained 0-9:
		for (int page = 0; page < 4; page++) {
			assertTrue(results.contains(page + ".html "));
		}
	}

	private List<String> generateLog() {
		return generateLog(20, 20, true);
	}

	private List<String> generateLog(int hosts, int pages, boolean equal) {
		int multiplier = 0;
		if (equal)
			multiplier = 1;
		List<String> logLines = new ArrayList<String>();
		String logLine;
		int totalRequests = 0;
		for (int hostNumber = 0; hostNumber < hosts; hostNumber++) {
			for (int pageNumber = 0; pageNumber < pages - hostNumber * multiplier; pageNumber++) {
				logLine = "successful." + hostNumber + ".host.com - - [08/Aug/1995:22:18:13 -0400] \"GET /page"
						+ pageNumber + ".html HTTP/1.0\" 200 0";
				logLines.add(logLine);
				logLine = "successful." + hostNumber + ".host.com - - [08/Aug/1995:22:18:13 -0400] \"GET /page"
						+ pageNumber + ".html HTTP/1.0\" 300 0";
				logLines.add(logLine);
				logLine = "unsuccessful." + hostNumber + ".host.com - - [08/Aug/1995:22:18:13 -0400] \"GET /page"
						+ pageNumber + ".html HTTP/1.0\" 500 0";
				logLines.add(logLine);
				totalRequests += 3;
			}
		}
		System.out.println("Total requests: " + totalRequests);
		System.out.println("Total successful requests: " + 2 * totalRequests / 3 + ", " + 2.0 / 3 * 100 + "%");
		System.out.println("Total unsuccessful requests: " + totalRequests / 3 + ", " + 1.0 / 3 * 100 + "%");
		return logLines;
	}

}

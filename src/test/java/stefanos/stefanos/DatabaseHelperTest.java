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
		for(String logLine : log) {
			Matcher matcher = apacheLogParser.parseLine(0, logLine);
			databaseHelper.insertLine(matcher);
			databaseHelper.commitLines();
			successfulResult = databaseHelper.getSuccessfulRequestsPercentage();
			unsuccessfulResult = databaseHelper.getUnsuccessfulRequestsPercentage();			
		}
		databaseHelper.close();
		assertTrue(successfulResult.contains("66.6666"));
		assertTrue(unsuccessfulResult.contains("33.3333"));
	}
	
	@Test
	public void testFeatures() {
		List<String> log = generateLog();
		for(String logLine : log) {
			System.out.println(logLine+"\n");
		}
	}

	private List<String> generateLog() {
		List<String> logLines = new ArrayList<String>();
		String logLine;
		int totalRequests = 0;
		for (int hostNumber = 0; hostNumber < 20; hostNumber++) {
			for (int pageNumber = 0; pageNumber < 20 - hostNumber; pageNumber++) {
				logLine = "successful." + hostNumber
						+ ".host.com - - [08/Aug/1995:22:18:13 -0400] \"GET /successful/page" + pageNumber
						+ ".html HTTP/1.0\" 200 0";
				logLines.add(logLine);
				logLine = "successful." + hostNumber
						+ ".host.com - - [08/Aug/1995:22:18:13 -0400] \"GET /successful/page" + pageNumber
						+ ".html HTTP/1.0\" 300 0";
				logLines.add(logLine);
				logLine = "unsuccessful." + hostNumber
						+ ".host.com - - [08/Aug/1995:22:18:13 -0400] \"GET /successful/page" + pageNumber
						+ ".html HTTP/1.0\" 500 0";
				logLines.add(logLine);
				totalRequests += 3;
			}
		}
		System.out.println("Total requests: "+totalRequests);
		System.out.println("Total successful requests: "+2*totalRequests/3+", "+2.0/3*100+"%" );
		System.out.println("Total unsuccessful requests: "+totalRequests/3+", "+1.0/3*100+"%" );
		return logLines;
	}

}

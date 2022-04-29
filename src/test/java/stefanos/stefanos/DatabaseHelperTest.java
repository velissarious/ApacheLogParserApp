package stefanos.stefanos;

import static org.junit.Assert.*;

import java.sql.SQLException;
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

}

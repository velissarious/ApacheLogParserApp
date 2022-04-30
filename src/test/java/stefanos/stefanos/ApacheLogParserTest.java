package stefanos.stefanos;

import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;

import org.junit.Test;

public class ApacheLogParserTest {
	@Test
	public void correctMatchShouldBeFound() {
		String logLine = "uplherc.upl.com - - [01/Aug/1995:00:00:08 -0400] \"GET /images/MOSAIC-logosmall.gif HTTP/1.0\" 304 0";

		ApacheLogParser apacheLogParser = new ApacheLogParser();
		apacheLogParser.parseLine(0, logLine);
		boolean found = apacheLogParser.found();

		assertEquals(true, found);
	}

	@Test
	public void incorrectMatchShouldNotBeFound() {
		String logLine = "uplherc.upl.com - g- broken0400]";

		ApacheLogParser apacheLogParser = new ApacheLogParser();
		apacheLogParser.parseLine(0, logLine);
		boolean found = apacheLogParser.found();

		assertEquals(false, found);
	}

	@Test
	public void matchedValuesShouldBeCorrect() {
		String logLine = "uplherc.upl.com - - [01/Aug/1995:00:00:08 -0400] \"GET /images/MOSAIC-logosmall.gif HTTP/1.0\" 304 0";

		ApacheLogParser apacheLogParser = new ApacheLogParser();
		Matcher matcher = apacheLogParser.parseLine(0, logLine);

		assertEquals(matcher.group(1), "uplherc.upl.com");
		assertEquals(matcher.group(2), "-");
		assertEquals(matcher.group(3), "-");
		assertEquals(matcher.group(4), "01/Aug/1995:00:00:08 -0400");
		assertEquals(matcher.group(5), "GET /images/MOSAIC-logosmall.gif HTTP/1.0");
		assertEquals(matcher.group(6), "304");
		assertEquals(matcher.group(7), "0");
	}

}

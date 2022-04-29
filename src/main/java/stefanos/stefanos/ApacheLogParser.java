package stefanos.stefanos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApacheLogParser {

	private static final String regex = "^([\\S.]+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+)";
	Pattern pattern;
	Matcher matcher;
	boolean found = false;

	public ApacheLogParser() {
		this.pattern = Pattern.compile(regex);
	}

	public Matcher parseLine(int lineNumber, String ApacheLogSample) {
		this.matcher = pattern.matcher(ApacheLogSample);
		if (!matcher.find()) {
			/*
			 * 8. The log file contains malformed entries; for each malformed line, display
			 * an error message and the line number.
			 */
			System.err.println("Malformed log entry in line " + (lineNumber + 1) + " !");
			found = false;
		} else {
			found = true;
		}
		return matcher;
	}

	public boolean found() {
		return this.found;
	}
}

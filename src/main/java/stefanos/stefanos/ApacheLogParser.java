package stefanos.stefanos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApacheLogParser {

	private static final String regex = "^([\\d.]+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+) \"([^\"]+)\" \"(.+?)\"";
	Pattern pattern;
	Matcher matcher; 

	public ApacheLogParser() {
		this.pattern = Pattern.compile(regex);
	}

	public Matcher parseLine(String ApacheLogSample) {
		this.matcher = pattern.matcher(ApacheLogSample);
		if(!matcher.find()) {
			System.out.println("Malformed log entry!");
		}
		return matcher;
	}
	
	public void printLastMatch() {
		System.out.println("\nIP Address: " + matcher.group(1));
		System.out.println("Available: " + matcher.group(2));
		System.out.println("UserID: " + matcher.group(3));
		System.out.println("Time: " + matcher.group(4));
		System.out.println("Request: " + matcher.group(5));
		System.out.println("Status: " + matcher.group(6));
		System.out.println("Size: " + matcher.group(7));
	}

}

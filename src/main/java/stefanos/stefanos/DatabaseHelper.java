package stefanos.stefanos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;

public class DatabaseHelper {

	final static String DATABASE_NAME = "test.db";
	private Connection connection;
	private Statement statement;

	public DatabaseHelper() throws SQLException, ClassNotFoundException {
		// Create SQLite database:
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
		System.out.println("Opened database successfully");

		// Create main table in database:
		statement = connection.createStatement();
		// @formatter:off
		String query = "CREATE TABLE logs " 
				+ "(id INT PRIMARY KEY NOT NULL,"
				+ " ip INT,"
				+ " available TEXT, "					
				+ " userid INT, "
				+ " time TEXT, " 
				+ " request TEXT,"
				+ " status TEXT,"
				+ " size INT )"; // Size in bytes.
		// @formatter:on
		statement.executeUpdate(query);
		statement.close();
	}

	public void insertLine(Matcher matcher) throws SQLException {
		// @formatter:off
		String sql = "INSERT INTO logs (id, ip, available, userid, time, request, status, size) " + 
		"VALUES (1, "
				+"\""+ matcher.group(1) + "\" , " 
				+"\""+ matcher.group(2) + "\", " 
				+"\""+ matcher.group(3) + "\", " 
				+"\""+ matcher.group(4) + "\", " 
				+"\""+ matcher.group(5) + "\", "
				+"\""+ matcher.group(6) + "\", "
				+"\""+ matcher.group(7) + "\" );";
		// @formatter:on
		statement.executeUpdate(sql);
	};

	public void close() throws SQLException {
		// Close connection to database:
		connection.close();
		// Cleanup - delete temporary database file:
		File myObj = new File(DATABASE_NAME);
		myObj.delete();
	}
}

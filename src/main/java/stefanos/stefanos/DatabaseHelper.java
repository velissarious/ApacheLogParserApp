package stefanos.stefanos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;

public class DatabaseHelper {

	final static String DATABASE_NAME = "test.db";
	private Connection connection;
	private PreparedStatement preparedStatemenet;

	public DatabaseHelper() throws SQLException, ClassNotFoundException {
		// Create SQLite database:
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
		System.out.println("Opened database successfully");

		// Create main table in database:
		Statement statement = connection.createStatement();
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

		// Prepare insert statement:
		query = "INSERT INTO logs (id, ip, available, userid, time, request, status, size) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		preparedStatemenet = connection.prepareStatement(query);
		connection.setAutoCommit(false);
	}

	public void insertLine(int id, Matcher matcher) throws SQLException {
		preparedStatemenet.setInt(1, id);
		preparedStatemenet.setString(2, matcher.group(1));
		preparedStatemenet.setString(3, matcher.group(2));
		preparedStatemenet.setString(4, matcher.group(3));
		preparedStatemenet.setString(5, matcher.group(4));
		preparedStatemenet.setString(6, matcher.group(5));
		preparedStatemenet.setString(7, matcher.group(6));
		preparedStatemenet.setString(8, matcher.group(7));
		preparedStatemenet.addBatch();
	};

	public void commitLines() throws SQLException {
		preparedStatemenet.executeBatch();
		connection.commit();
		connection.setAutoCommit(false);
	}

	public void close() throws SQLException {
		// Close connection to database:
		connection.close();
		// Cleanup - delete temporary database file:
		File myObj = new File(DATABASE_NAME);
		myObj.delete();
	}
}

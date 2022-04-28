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
				+ "( ip INT,"
				+ " request TEXT,"
				+ " status TEXT)";
		// @formatter:on
		statement.executeUpdate(query);
		statement.close();

		// Prepare insert statement:
		query = "INSERT INTO logs (ip, request, status) "
				+ "VALUES (?, ?, ?);";
		preparedStatemenet = connection.prepareStatement(query);
		connection.setAutoCommit(false);
	}

	public void insertLine(Matcher matcher) throws SQLException {
		preparedStatemenet.setString(1, matcher.group(1));
		preparedStatemenet.setString(2, matcher.group(5));
		preparedStatemenet.setString(3, matcher.group(6));
		preparedStatemenet.addBatch();
	};

	public void commitLines() throws SQLException {
		preparedStatemenet.executeBatch();
		connection.commit();
		connection.setAutoCommit(false);
	}

	public void close() throws SQLException {
		// Close the statement:
		preparedStatemenet.close();
		// Close connection to database:
		connection.close();
		// Cleanup - delete temporary database file:
		File myObj = new File(DATABASE_NAME);
		myObj.delete();
	}
}

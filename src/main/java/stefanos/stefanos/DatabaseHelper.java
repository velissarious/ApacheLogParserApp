package stefanos.stefanos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class DatabaseHelper {

	private Connection connection;
	private PreparedStatement preparedStatemenet;

	public DatabaseHelper() throws SQLException, ClassNotFoundException {
		// Create SQLite database:
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:file:memorydb.db?mode=memory&cache=shared");

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
		query = "INSERT INTO logs (ip, request, status) " + "VALUES (?, ?, ?);";
		preparedStatemenet = connection.prepareStatement(query);
		connection.setAutoCommit(false);
	}

	public void insertLine(Matcher matcher) throws SQLException {
		preparedStatemenet.setString(1, matcher.group(1));
		preparedStatemenet.setString(2, matcher.group(5));
		preparedStatemenet.setString(3, matcher.group(6));
		preparedStatemenet.addBatch();
	}

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
	}

	/* 1. Top 10 requested pages and the number of requests made for each */
	public String getTop10RequestedPagesAndRequestNumber() throws SQLException {
		String reportPart = "1. Top 10 requested pages and the number of requests made for each\n"
				+ "-------------------------------------------------------------------\n";
		Statement statement = connection.createStatement();
		// @formatter:off
		String query = "SELECT request, COUNT(*) AS count " 
				+ " FROM "
				+ " logs "
				+ " WHERE request LIKE '%.htm%' "
				+ " GROUP BY "
				+ " request "
				+ " ORDER BY count DESC "
				+ " LIMIT 10; ";
		// @formatter:on		
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) {
			// @formatter:off
			reportPart += 
					resultSet.getString("request").split(" ")[1] 
					+ " " 
					+ resultSet.getString("count") 
					+ "\n";
			// @formatter:on
		}
		statement.close();
		return reportPart + "\n";
	}

	/* 2. Percentage of successful requests (anything in the 200s and 300s range) */
	public String getSuccessfulRequestsPercentage() throws SQLException {
		String reportPart = "2. Percentage of successful requests (anything in the 200s and 300s range)\n"
				+ "--------------------------------------------------------------------------\n";
		Statement statement = connection.createStatement();
		// @formatter:off
		String query = "SELECT CAST((SELECT " 
				+ " COUNT(*) "
				+ " FROM "
				+ " logs "
				+ " WHERE status LIKE '2%' OR status LIKE '3%') AS float) "
				+ " / "
				+ " CAST((SELECT "
				+ " COUNT(*) "
				+ " FROM "
				+ " logs) AS float) * 100 "
				+ " As 'Success Percentage'; ";
		// @formatter:on		
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) {
			// @formatter:off
			reportPart += 
					resultSet.getString("Success Percentage") 
					+ " % \n";
			// @formatter:on
		}
		statement.close();
		return reportPart + "\n";
	}

	/*
	 * 3. Percentage of unsuccessful requests (anything that is not in the 200s or
	 * 300s range)
	 */
	public String getUnsuccessfulRequestsPercentage() throws SQLException {
		String reportPart = "3. Percentage of unsuccessful requests (anything that is not in the 200s or 300s range)\n"
				+ "---------------------------------------------------------------------------------------\n";
		Statement statement = connection.createStatement();
		// @formatter:off
		String query = "SELECT CAST((SELECT " 
				+ " COUNT(*) "
				+ " FROM "
				+ " logs "
				+ " WHERE status NOT LIKE '2%' AND status NOT LIKE '3%') AS float) "
				+ " / "
				+ " CAST((SELECT "
				+ " COUNT(*) "
				+ " FROM "
				+ " logs) AS float) * 100 "
				+ " As 'Failure Percentage'; ";
		// @formatter:on		
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) {
			// @formatter:off
			reportPart += 
					resultSet.getString("Failure Percentage") 
					+ " % \n";
			// @formatter:on
		}
		statement.close();
		return reportPart + "\n";
	}

	/* 4. Top 10 unsuccessful page requests */
	public String getTop10UnsuccessfulPageRequests() throws SQLException {
		String reportPart = "4. Top 10 unsuccessful page requests\n" + "------------------------------------\n";
		Statement statement = connection.createStatement();
		// @formatter:off
		String query = "SELECT request, COUNT(*) AS count " 
				+ " FROM "
				+ " logs "
				+ " logs "
				+ " WHERE request LIKE '%.htm%' "
				+ " AND status NOT LIKE '2%' and status "
				+ " NOT LIKE '3%' "
				+ " GROUP BY "
				+ " request "
				+ " ORDER BY count DESC "
				+ " LIMIT 10; ";
		// @formatter:on		
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) {
			// @formatter:off
			reportPart += 
					resultSet.getString("request").split(" ")[1]
					+ "\n";
			// @formatter:on
		}
		statement.close();
		return reportPart + "\n";
	}

	/*
	 * 5. The top 10 hosts making the most requests, displaying the IP address and
	 * number of requests made.
	 */
	public String getTop10HostsAndRequestsNumber() throws SQLException {
		String reportPart = "5. The top 10 hosts making the most requests, displaying the IP address and number of"
				+ " requests made.\n"
				+ "----------------------------------------------------------------------------------------------------\n";
		Statement statement = connection.createStatement();
		// @formatter:off
		String query = "SELECT ip, COUNT(*) AS count " 
				+ " FROM "
				+ " logs "
				+ " GROUP BY "
				+ " ip "
				+ " ORDER BY count DESC "
				+ " LIMIT 10; ";
		// @formatter:on		
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) {
			// @formatter:off
			reportPart += 
					resultSet.getString("ip") 
					+ " " 
					+ resultSet.getString("count") 
					+ "\n";
			// @formatter:on
		}
		statement.close();
		return reportPart + "\n";
	}

	/*
	 * 7. For each of the top 10 hosts, show the top 5 pages requested and the
	 * number of requests for each page
	 */
	public String getTop5PagesOfTop10Hosts() throws SQLException {
		String reportPart = "7. For each of the top 10 hosts, show the top 5 pages requested and the number of requests for each page requests made.\n"
				+ "-----------------------------------------------------------------------------------------------------------------------\n";

		// Get top 10 hosts:
		ArrayList<String> top10Hosts = new ArrayList<String>();
		Statement statement = connection.createStatement();
		// @formatter:off
		String query = "SELECT ip, COUNT(*) AS count " 
				+ " FROM "
				+ " logs "
				+ " GROUP BY "
				+ " ip "
				+ " ORDER BY count DESC "
				+ " LIMIT 10; ";
		// @formatter:on	
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) {
			top10Hosts.add(resultSet.getString("ip"));
		}
		statement.close();

		// Find top 5 pages for each host:
		for (String host : top10Hosts) {
			reportPart += "Host: " + host + "\n";
			statement = connection.createStatement();
			// @formatter:off
			query = "SELECT request, COUNT(request) AS Count " 
					+ " FROM "
					+ " logs "
					+ " WHERE "
					+ " ip = \""+host+"\" "
					+ " AND "
					+ " request LIKE '%.htm%' "
					+ " GROUP BY request"
					+ " ORDER BY count DESC "
					+ " LIMIT 5; ";
			// @formatter:on
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				// @formatter:off
				reportPart += resultSet.getString("request").split(" ")[1]
						+ " "
						+ resultSet.getString("count")
						+ "\n";
				// @formatter:on
			}
			reportPart += "\n";
			statement.close();
		}

		return reportPart;
	}

}

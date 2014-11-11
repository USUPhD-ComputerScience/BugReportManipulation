package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class MySQLConnector {
	private Connection mConnect = null;
	private Statement mStatement = null;
	public String mDatabase = "";

	public MySQLConnector(String user, String password, String database) {
		// TODO Auto-generated constructor stub
		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// jdbc:mysql://hostname/ databaseName
			mConnect = DriverManager.getConnection("jdbc:mysql://localhost/"
					+ database, user, password);
			// statements allow to issue SQL queries to the database
			mStatement = mConnect.createStatement();
			mDatabase = database;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// if you dont have any condition, just pass null into it
	public ResultSet select(String table, String fields[], String condition)
			throws SQLException {
		String field = fields[0];
		for (int i = 1; i < fields.length; i++) {
			field = field + "," + fields[i];
		}
		if (condition == null)
			return mStatement.executeQuery("select " + field + " from  "
					+ table);
		else
			return mStatement.executeQuery("select " + field + " from  "
					+ table + " WHERE " + condition);
	}

	// values: an array of values, must be in the same order as in the table
	// all values are String
	public int insert(String table, String values[]) throws SQLException {
		// preparedStatements can use variables and are more efficient
		String dumbValues = "";
		for (int i = 0; i < values.length; i++) {
			dumbValues += ",?";
		}
		PreparedStatement preparedStatement = mConnect
				.prepareStatement("insert into  " + mDatabase + "." + table
						+ " values (default" + dumbValues + ")");
		// parameters start with 1

		for (int i = 0; i < values.length; i++) {
			try {
				int intValue = Integer.parseInt(values[i]);
				preparedStatement.setInt(i + 1, intValue);
			} catch (NumberFormatException e) {
				preparedStatement.setString(i + 1, values[i]);
			}
		}
		preparedStatement.executeUpdate();
		int id = -1;
		ResultSet rs = preparedStatement.getGeneratedKeys();
		if (rs.next()) {
			id = rs.getInt(1);
		}
		preparedStatement.close();
		return id; // only return the first
	}

	private void writeMetaData(ResultSet resultSet) throws SQLException {
		// now get some metadata from the database
		System.out.println("The columns in the table are: ");
		System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
		for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
			System.out.println("Column " + i + " "
					+ resultSet.getMetaData().getColumnName(i));
		}
	}

	private void writeResultSet(ResultSet resultSet) throws SQLException {
		// resultSet is initialised before the first data set
		while (resultSet.next()) {
			// it is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g., resultSet.getSTring(2);
			String user = resultSet.getString("myuser");
			String website = resultSet.getString("webpage");
			String summary = resultSet.getString("summary");
			Date date = resultSet.getDate("datum");
			String comment = resultSet.getString("comments");
			System.out.println("User: " + user);
			System.out.println("Website: " + website);
			System.out.println("Summary: " + summary);
			System.out.println("Date: " + date);
			System.out.println("Comment: " + comment);
		}
	}

	// you need to close all three to make sure
	public void close() {
		try {
			if (mStatement != null)
				mStatement.close();
			if (mConnect != null)
				mConnect.close();
		} catch (Exception e) {
			// don't throw now as it might leave following closables in
			// undefined state
		}
	}

}

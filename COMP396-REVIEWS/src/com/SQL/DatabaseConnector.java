package com.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Database connector. Main purpose is to connect the application to the
 * embedded SQLite database.
 * 
 * Summer 2014
 * 
 * @author Maxim Gorshkov
 * 
 */
public class DatabaseConnector {

	private String dbURL = "jdbc:sqlite:./db/ALIGNED.db";
	private Connection conn;
	private Statement stmt;

	/**
	 * Constructor.
	 */
	public DatabaseConnector() {
		makeConnection();
	}

	/**
	 * Establish connection to the database.
	 */
	public void makeConnection() {

		try {
			Class.forName("org.sqlite.JDBC");

			conn = DriverManager.getConnection(dbURL);

			if (conn != null) {

				stmt = conn.createStatement();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns a result set for a given query. If not possible, throws a
	 * SQLException.
	 * 
	 * @param query
	 *            - String representing query to execute.
	 * @return ResultSet - If possible, returns ResultSet.
	 */
	public ResultSet execute(String query) {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("Select * from Users");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	/**
	 * Close the connection to avoid memory leaking.
	 */
	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks for all users that are currently in the database.
	 * 
	 * @return String[] of users.
	 */
	public String[] getUsers() {
		ResultSet rs = null;
		String[] s = null;
		ArrayList<String> rsList = new ArrayList<String>();

		try {
			rs = stmt.executeQuery("Select * from Users");
			while (rs.next()) {
				rsList.add(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		s = new String[rsList.size()];
		for (int i = 0; i < s.length; i++) {
			s[i] = rsList.get(i);
		}
		return s;
	}

	/**
	 * Get parameters related to all projects for a specific user.
	 * 
	 * @param user
	 *            - Specific user to check for.
	 * @return ArrayList<String[]> - Returns the project name, and last updated.
	 */
	public ArrayList<String[]> getProjects(String user) {
		ArrayList<String[]> toReturn = new ArrayList<String[]>();
		ResultSet rs = null;

		try {
			rs = stmt.executeQuery("Select * from Projects WHERE UserFK='"
					+ user + "'");
			while (rs.next()) {
				String[] temp = new String[3];
				temp[0] = rs.getString(2);
				temp[1] = rs.getString(3);
				temp[2] = "< DELETE >";
				
				toReturn.add(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toReturn;

	}
	
	/**
	 * Get the impact factor of a specific Journal.
	 * 
	 * @param user - Specific user to check for.
	 * @return double - Impact factor of the paper.
	 */
	public double getImpactFactor(String s){
		s = s.toUpperCase();
		Double d = -1.0;
		ResultSet rs = null;
		
		try {
			rs = stmt.executeQuery("Select ImpactFactor FROM ImpactFactor WHERE JournalName = '"+s+"'");
			while (rs.next()) {
				d = rs.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return d;
	}
	
	/**
	 * Add individual impact factors to the database.
	 * @param s - Short form of the journal name.
	 * @param d - Impact factor.
	 * @return boolean - whether the addition was successful.
	 */
	public boolean addImpactFactors(String s, Double d){
		try {
			stmt.execute("INSERT INTO ImpactFactor VALUES (null,'" + s
					+ "',"+d+")");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Add a project for a single user.
	 * 
	 * @param projName
	 *            - New project name.
	 * @param user
	 *            - Which user the project was created for.
	 * @return boolean - whether the addition was successful.
	 */
	public boolean addProject(String projName, String user) {
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		try {
			stmt.execute("INSERT INTO Projects VALUES (null,'" + projName
					+ "','" + date + "','" + user + "')");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Remove a project for a single user.
	 * 
	 * @param projName - Project name to delete.
	 * @param user - Which user the project was deleted from.
	 * @return boolean - whether the deletion was successful.
	 */
	public boolean deleteProject(String projName, String user) {
		
		try {
			stmt.execute("DELETE from Projects WHERE ProjectName = '" + projName + "' AND UserFK = '"+user+"'");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

//	public static void main(String[] args) {
//		DatabaseConnector a = new DatabaseConnector();
//		System.out.println(a.getImpactFactor("TRIALS"));
//	}
}

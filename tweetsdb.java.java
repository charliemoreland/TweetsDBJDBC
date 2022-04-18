package coms363;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.swing.*;
import javax.swing.border.LineBorder;
/*
 * Author: ComS 363 Teaching Staff
 * @Author Charlie Moreland
 * Examples of static queries, parameterized queries, and 
 * transactions
 * You can use this example to build your queries upon
 * 
 */
public class JDBCTransactionTester {
	public static String[] loginDialog() {
		// asking for a username and password to access the database.
		
		String result[] = new String[2];
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();

		cs.fill = GridBagConstraints.HORIZONTAL;

		JLabel lbUsername = new JLabel("Username: ");
		cs.gridx = 0;
		cs.gridy = 0;
		cs.gridwidth = 1;
		panel.add(lbUsername, cs);

		JTextField tfUsername = new JTextField(20);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		panel.add(tfUsername, cs);

		JLabel lbPassword = new JLabel("Password: ");
		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		panel.add(lbPassword, cs);

		JPasswordField pfPassword = new JPasswordField(20);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 2;
		panel.add(pfPassword, cs);
		panel.setBorder(new LineBorder(Color.GRAY));

		String[] options = new String[] { "OK", "Cancel" };
		int ioption = JOptionPane.showOptionDialog(null, panel, "Login", JOptionPane.OK_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		
		// store the username in the first slot.
		// store the password in the second slot.
		
		if (ioption == 0) // pressing OK button
		{
			result[0] = tfUsername.getText();
			result[1] = new String(pfPassword.getPassword());
		}
		return result;
	}

	
/**
 * 
 * @param conn db connection
 * @param username username to make the tweet as 
 * @param text of the tweet
 * @param day the tweet is being made 
 * @param month the tweet is being made 
 * @param year the tweet is being made 
 * @param retweetct the number of retweets the tweet has recieved
 */
	private static void insertTweet(Connection conn, String username, String text, int day, int month, int year, int retweetct) {
		//checking that username is not null since it is a primary key
		if (conn==null || username==null) throw new NullPointerException();
		try {
			
			conn.setAutoCommit(false);
			// full protection against interference from other transaction
			// prevent dirty read
			// prevent unrepeatable reads
			// prevent phantom reads
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE );
			
			
			//finding the max id 
			Statement stmt = conn.createStatement();
			ResultSet rs;
			//putting it into a long since tid is a big integer
			long id=0;
			
			// get the maximum id from the food table
			rs = stmt.executeQuery("select max(tid) from project.tweets");
			while (rs.next()) {
				// 1 indicates the position of the returned result we want to get
				id = rs.getLong(1);
			}
			rs.close();
			stmt.close();
			// once done, close the DBMS resources
			
			//statement to insert a new tweet into the tweets table
			PreparedStatement inststmt = conn.prepareStatement(
	                " insert into project.tweets (tid,post_day,post_month,post_year,texts,retweetCt, user_screen_name) values(?,?,?,?,?,?,?) ");
			//incrementing the tid by 1 so that it is now the next biggest tweet
			id+=1;
			//setting all of the ? to there respective values as seen in the tweet schema
			inststmt.setLong(1, id);
			inststmt.setString(7, username);
			inststmt.setInt(2, day);
			inststmt.setInt(3, month);
			inststmt.setInt(4, year);
			inststmt.setString(5, text);
			inststmt.setInt(6, retweetct);
			
			// tell DBMS to insert the tweet into the table
			int rowcount = inststmt.executeUpdate();
			
			// show how many rows are impacted, should be one row if 
			// successful
			// if not successful, SQLException occurs.
			System.out.println("Number of rows updated:" + rowcount);
			inststmt.close();
			
			// Tell DBMS to make sure all the changes you made from 
			// the prior commit is saved to the database
			conn.commit();
			
			// Reset the autocommit to commit per SQL statement
			conn.setAutoCommit(true);
			
		} catch (SQLException e) {
		}
	}
	
	
	
	/**
	 * 
	 * @param conn db connection
	 * @param uname username to be deleted from the database in all relevant columns
	 */
	private static void deleteUser(Connection conn, String uname) {
		//making sure that the user is not null since it is a primary key of user
		if (conn==null || uname==null) throw new NullPointerException();
		try {
			conn.setAutoCommit(false);
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		//creating a callable statement to interact with the stored procedure deleteUser()
		CallableStatement cstmt = conn.prepareCall("{call deleteUser(?)}");
		//setting the username to the given name
		cstmt.setString(1, uname);
		//executing the row delete
					 cstmt.executeUpdate();
					System.out.println(uname +" was sucessfully deleted");
					cstmt.close();
					
					// Tell DBMS to make sure all the changes you made from 
					// the prior commit is saved to the database
					conn.commit();
					
					// Reset the autocommit to commit per SQL statement
					conn.setAutoCommit(true);
		} catch (SQLException e) {}
	}
	
	/**
	 * 
	 * @param conn connection to db
	 * @param hname hastage name
	 * @param month
	 * @param year
	 * @param state
	 */
private static void checkHastag(Connection conn, String hname, int month, int year, String state) {
	if (conn==null || hname==null || state==null) throw new NullPointerException();
	try {	
		conn.setAutoCommit(false);
		// full protection against interference from other transaction
		// prevent dirty read
		// prevent unrepeatable reads
		// prevent phantom reads
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE );
	
		//creating a callable statement for the findUserPosting Hashtag stored procedure
		CallableStatement cstmt = conn.prepareCall("{call findUserPostingHashtag(?,?,?,?)}");
		//setting all of the ? to their respective variables
		cstmt.setString(1, hname);
		cstmt.setInt(2, month);
		cstmt.setInt(3, year);
		cstmt.setString(4, state);
		
		// ResultSet is used to store the data returned by DBMS when issuing a static query
		ResultSet rs;
		
		// ResultSetMetaData is used to find meta data about the data returned
		ResultSetMetaData rsMetaData;
		String toShow;
		
		// Send the SQL query to the DBMS
		rs = cstmt.executeQuery();
		
		// get information about the returned result.
		rsMetaData = rs.getMetaData();
		toShow = "";
		
		// iterate through each item in the returned result
		while (rs.next()) {
			// concatenate the columns in each row
			for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
			
				toShow += rs.getString(i + 1) + ", ";
			}
			toShow += "\n";
		}
		// show the dialog box with the returned result by DBMS
		JOptionPane.showMessageDialog(null, toShow);
		rs.close();
		
		cstmt.close();
		
		// Tell DBMS to make sure all the changes you made from 
		// the prior commit is saved to the database
		conn.commit();
		
		// Reset the autocommit to commit per SQL statement
		conn.setAutoCommit(true);
		
	} catch (SQLException e) {
	}

}


	public static void main(String[] args) {
		// useSSL=false means plain text allowed
		//String dbServer = "jdbc:mysql://localhost:3306/fooddb?useSSL=false";
		// useSSL=true; data are encrypted when sending between DBMS and 
		// this program
		
		String dbServer = "jdbc:mysql://127.0.0.1:3306/project?useSSL=true";
		String userName = "";
		String password = "";

		String result[] = loginDialog();
		userName = result[0];
		password = result[1];

		Connection conn=null;
		if (result[0]==null || result[1]==null) {
			System.out.println("Terminating: No username nor password is given");
			return;
		}
		try {
			// load JDBC driver
			// must be in the try-catch-block
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(dbServer, userName, password);
			
			//changed that option to fit the new three options and the 4th being a quit
			String option = "";
			String instruction = "Enter a: Insert a new tweet into the database" + "\n"
					+ "Enter b: Delete a user from the database based on screen name"
					+ "\n" + "Enter c: Find all tweets with a given hastag form a certain month year and state." + "\n"
					+ "Enter e: Quit Program";

			//after entering in the respective option the neccessary data is taken in and then the method is called for that repective option
			while (true) {
				option = JOptionPane.showInputDialog(instruction);
				if (option.equals("a")) {
					String username =JOptionPane.showInputDialog("Enter the name of the user making the tweet:");
					String text =JOptionPane.showInputDialog("Enter the text of the tweet:");
					int day = Integer.parseInt(JOptionPane.showInputDialog("Enter the day of the tweet:"));
					int month = Integer.parseInt(JOptionPane.showInputDialog("Enter the month of the tweet:"));
					int year = Integer.parseInt(JOptionPane.showInputDialog("Enter the year of the tweet:"));
					int retweetct = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of the retweets the tweet has:"));
					insertTweet(conn, username, text, day, month, year, retweetct);
				} else if (option.equals("b")) {
					String uname=JOptionPane.showInputDialog("Enter username:");
					String yn=JOptionPane.showInputDialog("Are you sure you want to delete "+uname+"from the database enter y to continue");
					if(yn.equals("y")||yn.equals("Y")) {
						deleteUser(conn, uname);	
					}
				} else if (option.equals("c")) {
					String hname=JOptionPane.showInputDialog("Enter exact name of the hastag to check:");
					int month = Integer.parseInt(JOptionPane.showInputDialog("Enter the month of the tweet:"));
					int year = Integer.parseInt(JOptionPane.showInputDialog("Enter the year of the tweet:"));
					String state = JOptionPane.showInputDialog("Enter the name of the state you want to check in:");
					checkHastag(conn, hname, month, year, state);
				}  
				else {
					break;
				}
			}
			// close the statement
			// close the connection
			if (conn != null) conn.close();
		} catch (Exception e) {
			
			System.out.println("Program terminates due to errors or user cancelation");
			e.printStackTrace(); // for debugging; 
		}
	}

}

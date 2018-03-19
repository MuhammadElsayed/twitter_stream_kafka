package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import model.Tweet;

public class RedShiftDataEmitter {
	static final String redshiftUrl = "jdbc:redshift://bdt-twitter-project.cgsgl8tibau7.us-east-2.redshift.amazonaws.com:5439/twitts";
	static final String masterUsername = "bdt_twitter_user";
	static final String password = "bdt_twitter_PASS_1";

	private static Connection connection = null;
	private static RedShiftDataEmitter redShiftDataEmitter = new RedShiftDataEmitter();

	public static RedShiftDataEmitter getInstance() {
		if (connection == null) {

			try {
				Class.forName("com.amazon.redshift.jdbc41.Driver");
				Properties properties = new Properties();
				properties.setProperty("user", masterUsername);
				properties.setProperty("password", password);
				connection = DriverManager.getConnection(redshiftUrl,
						properties);
				// Further code to follow
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return redShiftDataEmitter;
	}

	public void insert(Tweet tweet) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			String sql;
			sql = "insert into data values(" + tweet.getId() + ",'"
					+ tweet.getTag() 
					//+"job"
					+ "'," + tweet.getLat() + ","
					+ tweet.getLang() + ");";
			//System.out.println(sql);
			int result = stmt.executeUpdate(sql);

			//System.out.println(result);
			stmt.close();
			// conn.close();
		} catch (Exception ex) {
			// For convenience, handle all errors here.
			ex.printStackTrace();
		} finally {
			// Finally block to close resources.
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}// nothing we can do
			try {
				// if(conn!=null)
				// conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Tweet tweet = new Tweet();
		RedShiftDataEmitter.getInstance().insert(tweet);
	}
}
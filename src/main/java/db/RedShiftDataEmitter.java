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
	static RedShiftDataEmitter redShiftDataEmitter = new RedShiftDataEmitter();
    
    static RedShiftDataEmitter getInstance(){
    	if (connection == null) {

            try {
                Class.forName("com.amazon.redshift.jdbc41.Driver");
                Properties properties = new Properties();
                properties.setProperty("user", masterUsername);
                properties.setProperty("password", password);
                connection = DriverManager.getConnection(redshiftUrl, properties);
                // Further code to follow
            } catch(ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
    	}
		return redShiftDataEmitter;
    }

    public static void insert(Tweet tweet) {
        Statement stmt = null;
        try{
           stmt = connection.createStatement();
           String sql;
           sql = "select * from information_schema.tables;";
           ResultSet rs = stmt.executeQuery(sql);
           
           //Get the data from the result set.
           while(rs.next()){
              //Retrieve two columns.
              String catalog = rs.getString("table_catalog");
              String name = rs.getString("table_name");

              //Display values.
              System.out.print("Catalog: " + catalog);
              System.out.println(", Name: " + name);
           }
           rs.close();
           stmt.close();
           //conn.close();
        }catch(Exception ex){
           //For convenience, handle all errors here.
           ex.printStackTrace();
        }finally{
           //Finally block to close resources.
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(Exception ex){
           }// nothing we can do
           try{
//              if(conn!=null)
//                 conn.close();
           }catch(Exception ex){
              ex.printStackTrace();
           }
        }
    }
}
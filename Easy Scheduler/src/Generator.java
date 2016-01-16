import java.sql.*;

// main Generator class



public class Generator {

	// Connect to MySQL Database.
	public static void main(String[] args)
			throws SQLException, ClassNotFoundException {
				// Load the JDBC driver
				Class.forName("com.mysql.jdbc.Driver");
				System.out.println("Driver loaded");
				
				// Establish a connection
				Connection connection = DriverManager.getConnection
				("jdbc:mysql://easyschedule0.db.10711786.hostedresource.com", "easyschedule0", "MATH@3930h");
				System.out.println("Database connected");
				
				// Create a statement
				Statement statement = connection.createStatement();
				
				// use database name
				statement.execute("use easyschedule0");
				
				// Execute a statement
				ResultSet resultSet = statement.executeQuery
				("SELECT first_name, last_name, major, minor FROM student");
				
				// Iterate through the result set and print the returned results
				while (resultSet.next()){
					System.out.println(resultSet.getString("first_name"));
					System.out.println(resultSet.getString("last_name"));
					System.out.println(resultSet.getString("major"));
					System.out.println(resultSet.getString("minor"));
				}
				
				// Close the connection
				connection.close();
			}
	
	
	public Generator(){
		
	}
}

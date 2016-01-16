import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class Parser {

	public static void main (String[] args) throws ClassNotFoundException, SQLException
	{
		
		// Load the JDBC driver
		Class.forName("com.mysql.jdbc.Driver");
		System.out.println("Driver loaded");
		
		// Establish a connection
		Connection connection = DriverManager.getConnection
		("jdbc:mysql://easyschedule0.db.10711786.hostedresource.com", "easyschedule0", "MATH@3930h");
		System.out.println("Database connected");
		
		// Create a statement
		Statement statementParse = connection.createStatement();
		
		// use database name
		statementParse.execute("use easyschedule0");
		
		// Execute a statement
		ResultSet updateResult = statementParse.executeQuery("SELECT course FROM test_parser");
			
		// Iterate through the result set and print the returned results
		while (updateResult.next())
		{
			String course = updateResult.getString("course");
			String parse = parseString(course);
			//String update = "INSERT INTO semester_schedule VALUES('"+pid+"', '"+semester+"', '"+temp+"', '0')";
			String update = "UPDATE test_parser SET course='"+parse+"' WHERE course='"+course+"'";
			
			statementParse.executeUpdate(update);
			
		}
		
		// Close the connection
		connection.close();
		
		
		//String myString = "    mac   1234  c";
		//System.out.println(myString + " > " + parseString(myString));
	}
	
	static String parseString(String stringCourse)
	{
		final int NUM_LETTERS_IN_COURSE_NAME = 3;
		final int NUM_NUMBERS_IN_COURSE_NAME = 4;
		
		char[] charCourse = stringCourse.toCharArray();
		char[] charParsed = new char[7];
		
		int courseIndex = 0, parsedIndex = 0;
		
		//Check to see if the next character is white space, if so then skip it
		while (Character.isWhitespace(charCourse[courseIndex]))
		{
			courseIndex++;
		}
		
		//Copy the first 3 letters of the course name
		for (int i = 0; i < NUM_LETTERS_IN_COURSE_NAME; i++)
			charParsed[parsedIndex++] = Character.toUpperCase(charCourse[courseIndex++]);
		
		//Check to see if the next character is white space, if so then skip it
		while (Character.isWhitespace(charCourse[courseIndex]))
		{
			courseIndex++;
		}
		
		//We dont care about the letter(s) after the next four numbers, so we only need the
		//For loop, then return
		for (int i = 0; i < NUM_NUMBERS_IN_COURSE_NAME; i++)
			charParsed[parsedIndex++] = charCourse[courseIndex++];
		
		
		
		return new String(charParsed);
	}
}
